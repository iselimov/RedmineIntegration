package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.List;
import java.util.Optional;

/**
 * Created by defrag on 22.08.17.
 */
public interface TaskMapper {

    Optional<Task> toPluginTask(Issue redmineTask);

    List<Task> toPluginTasks(List<Issue> redmineTasks);

    Optional<Issue> toRedmineTask(Task pluginTask);
}
