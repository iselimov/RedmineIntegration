package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.RedmineIssue;
import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;

import java.util.List;
import java.util.Map;
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

    Optional<Issue> toRedmineTask(Task pluginTask, Issue toUpdateTask);

    List<TimeEntry> toRedmineLogWorks(List<LogWork> pluginLogWorks, Map<Integer, TimeEntry> sourceTimeEntries, int taskId);
}