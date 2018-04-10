package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.RedmineIssue;
import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.service.util.RedmineEntityGetter;
import com.defrag.redmineplugin.service.util.RedmineEntitySetter;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.defrag.redmineplugin.service.util.curl.CommentPostEntity;
import com.defrag.redmineplugin.service.util.curl.RemainingHoursGetEntity;
import com.defrag.redmineplugin.service.util.curl.RemainingHoursPostEntity;
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by defrag on 13.08.17.
 */
@Slf4j
public class TaskManager {

    private final ConnectionInfo connectionInfo;

    private final TaskMapper mapper;

    private final RedmineManager redmineManager;

    private final ViewLogger viewLogger;

    private int projectId;

    private int userId;

    private RedmineEntityGetter remainingGetter;

    private RedmineEntitySetter<Float> remainingSetter;

    private RedmineEntitySetter<String> commentsSetter;

    public TaskManager(ConnectionInfo connectionInfo, ViewLogger viewLogger) {
        this.connectionInfo = connectionInfo;
        this.viewLogger = viewLogger;
        mapper = new TaskMapper();
        redmineManager = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(), connectionInfo.getApiAccessKey());
        try {
            projectId = redmineManager.getProjectManager().getProjectByKey(connectionInfo.getProjectKey()).getId();
        } catch (RedmineException e) {
            viewLogger.error("Ошибка при получении id проекта");
        }
        try {
            userId = redmineManager.getUserManager().getCurrentUser().getId();
        } catch (RedmineException e) {
            viewLogger.error("Ошибка при получении id текущего пользователя");
        }
        if (this.connectionInfo.hasExtendedProps()) {
            remainingGetter = new RemainingHoursGetEntity(connectionInfo);
            remainingSetter = new RemainingHoursPostEntity(connectionInfo);
            commentsSetter = new CommentPostEntity(connectionInfo);
        }
    }

    public List<Task> getTasks(Params filter) {
        log.info("filter is {}", filter.getList());
        List<RedmineIssue> redmineIssues;
        try {
            redmineIssues = redmineManager.getIssueManager().getIssues(filter).getResults()
                    .stream()
                    .map(RedmineIssue::new)
                    .peek(this::enrichWithLogWork)
                    .collect(Collectors.toList());
            viewLogger.info("Загружено из Redmine задач: '%d'", redmineIssues.size());
        } catch (RedmineException e) {
            log.error("Couldn't get issues, reason is {}", e.getLocalizedMessage());
            viewLogger.error("Возникла ошибка при загрузке задач c Redmine");
            return Collections.emptyList();
        }
        if (!connectionInfo.hasExtendedProps()) {
            return mapper.toPluginTasks(redmineIssues);
        }
        List<Task> tasks = mapper.toPluginTasks(redmineIssues);
        tasks.parallelStream()
             .forEach(this::enrichWithRemainingHours);
        return tasks;
    }

    public void createSubTask(Task pluginTask) {
        if (pluginTask.isTask()) {
            viewLogger.warning("Создание подзадачи недоступно для задач с типом 'Task'");
            return;
        }
        viewLogger.info("Создание подзадачи для '%d'", pluginTask.getId());
        mapper.toRedmineTask(pluginTask)
                .ifPresent(subTask -> {
                    try {
                        subTask.setAssigneeId(userId);
                        subTask.setProjectId(projectId);

                        Issue created = redmineManager.getIssueManager().createIssue(subTask);
                        viewLogger.info("Подзадача была успешно создана с id: '%d'", created.getId());
                    } catch (RedmineException e) {
                        viewLogger.error("Возникла ошибка создании подзадачи");
                    }
                });
    }

    public void updateTask(Task pluginTask) {
        log.info("Updating task with id {}", pluginTask.getId());
        viewLogger.info("Обновление задачи '%d'", pluginTask.getId());
        boolean wasUpdatedTask = doUpdateTask(pluginTask);
        if (!wasUpdatedTask) {
            viewLogger.error("Произошла ошибка при обновлении задачи");
            return;
        }
        updateComments(pluginTask);
        boolean wasUpdatedLogWorks = updateLogWorks(pluginTask);
        if (!wasUpdatedLogWorks) {
            viewLogger.error("Произошла ошибка при обновлении log work по задаче");
            return;
        }
        updateRemainingHours(pluginTask);
    }

    private boolean doUpdateTask(Task pluginTask) {
        Issue redmineTask;
        try {
            redmineTask = redmineManager.getIssueManager().getIssueById(pluginTask.getId());
        } catch (RedmineException e) {
            log.error("Error while getting task");
            return false;
        }
        Integer oldStatusId = redmineTask.getStatusId();
        Optional<Issue> toUpdate = mapper.toRedmineTask(pluginTask, redmineTask);
        if (!toUpdate.isPresent()) {
            return false ;
        }
        try {
            redmineManager.getIssueManager().update(toUpdate.get());
        } catch (RedmineException e) {
            log.error("Error while updating task");
            return false;
        }
        updateRelationTaskStatus(pluginTask, oldStatusId);
        return true;
    }

    private void updateRelationTaskStatus(Task pluginTask, Integer oldStatusId) {
        if (Objects.equals(oldStatusId, pluginTask.getStatus().getParamId())) {
            return;
        }
        Integer parentTaskId;
        if (pluginTask.isTask()) {
            parentTaskId = pluginTask.getParentId();
        } else {
            parentTaskId = pluginTask.getId();
        }
        Issue parentTask;
        try {
            parentTask = redmineManager.getIssueManager().getIssueById(parentTaskId, Include.children);
        } catch (RedmineException e) {
            log.error("Error while getting parent task");
            return;
        }
        if (parentTask.getChildren().isEmpty()
                || parentTask.getChildren().size() > 1) {
            return;
        }
        Issue toUpdateTask;
        if (pluginTask.isTask()) {
            toUpdateTask = parentTask;
        } else {
            Issue childTask = parentTask.getChildren().iterator().next();
            try {
                // приходится доставать таску заново, поскольку API не отдает всю информацию
                // по задачам-потомкам
                toUpdateTask = redmineManager.getIssueManager().getIssueById(childTask.getId());
            } catch (RedmineException e) {
                log.error("Error while getting child task");
                return;
            }
        }
        toUpdateTask.setStatusId(pluginTask.getStatus().getParamId());

        try {
            redmineManager.getIssueManager().update(toUpdateTask);
        } catch (RedmineException e) {
            log.error("Error while updating task");
        }
    }

    private boolean updateLogWorks(Task pluginTask) {
        if (!pluginTask.isTask()) {
            return true;
        }
        Map<Integer, TimeEntry> redmineLogWorks;
        try {
            redmineLogWorks = redmineManager.getTimeEntryManager().getTimeEntriesForIssue(pluginTask.getId())
                    .stream()
                    .collect(Collectors.toMap(TimeEntry::getId, te -> te));
        } catch (RedmineException e) {
            log.error("Error while getting log works for task");
            return false;
        }
        List<TimeEntry> pluginLogWorks = mapper.toRedmineLogWorks(pluginTask.getLogWorks(), redmineLogWorks, pluginTask.getId());
        for (TimeEntry pluginLogWork : pluginLogWorks) {
            if (pluginLogWork.getId() == null) {
                try {
                    redmineManager.getTimeEntryManager().createTimeEntry(pluginLogWork);
                } catch (RedmineException e) {
                    log.error("Error while creating log work with comment {}", pluginLogWork.getComment());
                }
            } else {
                try {
                    redmineManager.getTimeEntryManager().update(pluginLogWork);
                } catch (RedmineException e) {
                    log.error("Error while updating log work with comment {}", pluginLogWork.getComment());
                }
            }
        }
        synchronizeRedmineLogWorks(redmineLogWorks, pluginLogWorks);
        return true;
    }

    private void synchronizeRedmineLogWorks(Map<Integer, TimeEntry> redmineLogWorks, List<TimeEntry> pluginLogWorks) {
        Set<Integer> toUpdatePluginLogWorks = pluginLogWorks
                .stream()
                .filter(te -> te.getId() != null)
                .map(TimeEntry::getId)
                .collect(Collectors.toSet());
        redmineLogWorks.entrySet()
                .stream()
                .filter((entry) -> !toUpdatePluginLogWorks.contains(entry.getKey()))
                .forEach(entry -> {
                    try {
                        redmineManager.getTimeEntryManager().deleteTimeEntry(entry.getKey());
                    } catch (RedmineException e) {
                        log.error("Error while deleting log work with comment {}", entry.getValue().getComment());
                    }
                });
    }

    private void enrichWithRemainingHours(Task pluginTask) {
        Optional<String> remainingHours = remainingGetter.get(pluginTask.getId());
        if (!remainingHours.isPresent()) {
            log.warn("Remaining hours was not found");
            return;
        }
        String remainingStr = remainingHours.get();
        if (StringUtils.isBlank(remainingStr)) {
            log.info("Remaining hours is blank, set it to zero");
            pluginTask.setRemaining(0f);
        }
        try {
            pluginTask.setRemaining(Float.valueOf(remainingStr));
        } catch (NumberFormatException e) {
            log.error("Couldn't parse remaining str value {}", remainingStr);
        }
    }

    private void updateComments(Task pluginTask) {
        if (!this.connectionInfo.hasExtendedProps()) {
            return;
        }
        if (pluginTask.getComments().isEmpty()) {
            return;
        }
        pluginTask.getComments()
                .forEach(comment -> {
                    if (comment.isParentComment() && pluginTask.isTask()) {
                        commentsSetter.post(pluginTask.getParentId(), comment.getText());
                    } else {
                        commentsSetter.post(pluginTask.getId(), comment.getText());
                    }
                });
        pluginTask.getComments().clear();
    }

    private void updateRemainingHours(Task pluginTask) {
        if (!this.connectionInfo.hasExtendedProps() || !pluginTask.isTask()) {
            return;
        }
        float spentTime = (float) pluginTask.getLogWorks()
                .stream()
                .mapToDouble(LogWork::getTime)
                .sum();
        float remaining = pluginTask.getEstimate() > spentTime ? pluginTask.getEstimate() - spentTime : 0.0f;
        if (pluginTask.getRemaining() == null
                || pluginTask.getRemaining().equals(remaining)) {
            return;
        }
        remainingSetter.post(pluginTask.getId(), remaining);
        pluginTask.setRemaining(remaining);
    }

    private void enrichWithLogWork(RedmineIssue redmineTask) {
        List<TimeEntry> logWorks;
        try {
            logWorks = redmineManager.getTimeEntryManager().getTimeEntriesForIssue(redmineTask.getIssue().getId());
        } catch (RedmineException e) {
            log.error("Couldn't get time entries if issue {}, reason is {}", redmineTask.getIssue().getId(), e.getLocalizedMessage());
            return;
        }
        logWorks.forEach(te -> redmineTask.getTimeEntries().add(te));
    }
}