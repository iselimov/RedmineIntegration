package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.RedmineIssue;
import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by defrag on 22.08.17.
 */
public interface TaskMapper {

    Optional<Task> toPluginTask(RedmineIssue redmineTask);

    default List<Task> toPluginTasks(List<RedmineIssue> redmineTasks) {
        return redmineTasks
                .stream()
                .map(this::toPluginTask)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    Optional<Issue> toRedmineTask(Task pluginTask);
}