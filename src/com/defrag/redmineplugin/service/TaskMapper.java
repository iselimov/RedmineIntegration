package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.RedmineIssue;
import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.model.TaskStatus;
import com.defrag.redmineplugin.model.TaskType;
import com.defrag.redmineplugin.service.util.ConvertUtils;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.TimeEntryFactory;
import com.taskadapter.redmineapi.bean.TrackerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by defrag on 18.08.17.
 */
@Slf4j
public class TaskMapper {

    public List<Task> toPluginTasks(List<RedmineIssue> sources) {
        return sources
                .stream()
                .map(this::toPluginTask)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Task> toPluginTask(RedmineIssue source) {
        Issue sourceIssue = source.getIssue();
        log.info("Try map task with id {}", sourceIssue.getId());

        Optional<RedmineFilter> type = RedmineFilter.getEnumItem(TaskType.values(), sourceIssue.getTracker().getName());
        Optional<RedmineFilter> status = RedmineFilter.getEnumItem(TaskStatus.values(), sourceIssue.getStatusName());

        if (!isValidRedmineTask(sourceIssue, type, status)){
            return Optional.empty();
        }
        Task dest = new Task(type.get(), status.get(), sourceIssue.getAuthorName(), sourceIssue.getSubject());
        dest.setId(sourceIssue.getId());
        dest.setDescription(sourceIssue.getDescription());
        dest.setEstimate(sourceIssue.getEstimatedHours() != null ? sourceIssue.getEstimatedHours() : 0);
        dest.setRemaining(0f);
        dest.setParentId(sourceIssue.getParentId());
        toPluginLogWorks(dest, source.getTimeEntries());
        return Optional.of(dest);
    }

    public Optional<Issue> toRedmineTask(Task pluginTask) {
        Issue subTask = new Issue();
        subTask.setStatusId(Integer.parseInt(pluginTask.getStatus().getParamId()));
        subTask.setTracker(TrackerFactory.create(Integer.parseInt(pluginTask.getType().getParamId())));
        subTask.setDescription(pluginTask.getDescription());
        subTask.setAuthorName(pluginTask.getAuthor());
        subTask.setSubject(pluginTask.getSubject());
        subTask.setParentId(pluginTask.getId());
        return Optional.of(subTask);
    }

    public Optional<Issue> toRedmineTask(Task pluginTask, Issue toUpdateTask) {
        toUpdateTask.setSubject(pluginTask.getSubject());
        toUpdateTask.setDescription(pluginTask.getDescription());
        toUpdateTask.setStatusId(Integer.parseInt(pluginTask.getStatus().getParamId()));
        toUpdateTask.setEstimatedHours(pluginTask.getEstimate());
        return Optional.of(toUpdateTask);
    }

    public List<TimeEntry> toRedmineLogWorks(List<LogWork> pluginLogWorks, Map<Integer, TimeEntry> sourceTimeEntries, int taskId) {
        List<TimeEntry> timeEntries = new ArrayList<>();
        for (LogWork pluginLogWork : pluginLogWorks) {
            TimeEntry dest;
            if (pluginLogWork.getId() == null) {
                dest = TimeEntryFactory.create();
            } else {
                dest = sourceTimeEntries.getOrDefault(pluginLogWork.getId(), TimeEntryFactory.create(pluginLogWork.getId()));
            }
            dest.setIssueId(taskId);
            dest.setHours(pluginLogWork.getTime());
            dest.setComment(pluginLogWork.getDescription());
            dest.setSpentOn(java.sql.Date.valueOf(pluginLogWork.getDate()));
            dest.setActivityId(pluginLogWork.getType().getActivityId());
            timeEntries.add(dest);
        }
        return timeEntries;
    }

    private boolean isValidRedmineTask(Issue source, Optional<RedmineFilter> type, Optional<RedmineFilter> status) {
        if (!type.isPresent()) {
            log.error("Task type can't be empty");
            return false;
        }
        if (!status.isPresent()) {
            log.error("Task status can't be empty");
            return false;
        }
        if (StringUtils.isBlank(source.getAuthorName())) {
            log.error("Author can't be empty");
            return false;
        }
        if (StringUtils.isBlank(source.getSubject())) {
            log.error("Subject can't be empty");
            return false;
        }
        return true;
    }

    private void toPluginLogWorks(Task dest, List<TimeEntry> timeEntries) {
        timeEntries.forEach(te -> {
            LocalDate date = ConvertUtils.toLocalDate(te.getSpentOn());
            LogWork.Type type = LogWork.Type.typeByActivity(te.getActivityId());
            dest.getLogWorks().add(new LogWork(te.getId(), date, type, te.getComment(), te.getHours()));
        });
    }
}