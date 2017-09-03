package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.TaskManager;

/**
 * Created by defrag on 03.09.17.
 */
public interface TaskManagerConsumer {

    TaskManager getTaskManager();

    void setTaskManager(TaskManager taskManager);
}