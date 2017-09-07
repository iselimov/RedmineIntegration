package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.TaskManager;
import com.defrag.redmineplugin.view.form.model.TaskTableModel;

/**
 * Created by defrag on 03.09.17.
 */
public interface TaskManagerConsumer {

    TaskManager getTaskManager();

    void setTaskManager(TaskManager taskManager);

    TaskTableModel getTaskModel();

    void setTaskModel(TaskTableModel taskModel);
}