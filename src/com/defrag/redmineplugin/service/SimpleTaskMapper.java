package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.model.TaskStatus;
import com.defrag.redmineplugin.model.TaskType;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by defrag on 18.08.17.
 */
@Slf4j
public class SimpleTaskMapper implements TaskMapper {

    @Override
    public List<Task> toPluginTasks(List<Issue> redmineTasks) {
        return redmineTasks
                .stream()
                .map(this::toPluginTask)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> toPluginTask(Issue source) {
        log.info("Try map task with id {}", source.getId());

        Optional<RedmineFilter> type = RedmineFilter.getEnumItem(TaskType.values(), source.getTracker().getName());
        Optional<RedmineFilter> status = RedmineFilter.getEnumItem(TaskStatus.values(), source.getStatusName());

        if (!isValidRedmineTask(source, type, status)){
            return Optional.empty();
        }

        Task dest = new Task(type.get(), status.get(), source.getAuthorName(), source.getSubject());
        dest.setId(source.getId());
        dest.setDescription(source.getDescription());
        dest.setEstimate(source.getEstimatedHours());

        return Optional.of(dest);
    }

    @Override
    public Optional<Issue> toRedmineTask(Task pluginTask) {
        throw new NotImplementedException();
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

}
