package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.Optional;

/**
 * Created by defrag on 22.08.17.
 */
public interface TaskMapper {

    Optional<Task> toPluginTask(Issue redmineTask);
}
