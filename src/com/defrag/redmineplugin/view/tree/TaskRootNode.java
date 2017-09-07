package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.TaskManager;
import com.defrag.redmineplugin.view.form.model.TaskTableModel;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by defrag on 03.09.17.
 */
abstract class TaskRootNode extends SimpleNode implements TaskManagerConsumer {

    final List<SimpleNode> children = new ArrayList<>();

    private TaskManager taskManager;

    private TaskTableModel taskModel;

    @Override
    public SimpleNode[] getChildren() {
        if (children.isEmpty()) {
            addChildren();
        }

        return children.toArray(new SimpleNode[children.size()]);
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public TaskTableModel getTaskModel() {
        return taskModel;
    }

    @Override
    public void setTaskModel(TaskTableModel taskModel) {
        this.taskModel = taskModel;
    }

    abstract void addChildren();
}