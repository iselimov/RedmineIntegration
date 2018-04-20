package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.model.BackLog;
import com.defrag.redmineplugin.model.TaskStatus;
import com.defrag.redmineplugin.model.TaskType;
import com.defrag.redmineplugin.service.TaskManager;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.defrag.redmineplugin.view.form.model.TaskTableModel;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by defrag on 03.09.17.
 */
public class MainRootNode extends SimpleNode implements TaskManagerConsumer {

    private final List<TaskManagerConsumer> children = new ArrayList<>();

    public MainRootNode(ViewLogger viewLogger) {
        addChildren(viewLogger);
    }

    @Override
    public String getName() {
        return "Tasks filter";
    }

    @Override
    public SimpleNode[] getChildren() {
        return children.toArray(new SimpleNode[children.size()]);
    }

    private void addChildren(ViewLogger viewLogger) {
        children.add(new StatusRootNode(TaskStatus.values(), viewLogger));
        children.add(new TypeRootNode(TaskType.values(), viewLogger));
        children.add(new BackLogRootNode(BackLog.values(), viewLogger));
    }

    @Override
    public TaskManager getTaskManager() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setTaskManager(TaskManager taskManager) {
        children.forEach(child -> child.setTaskManager(taskManager));
    }

    @Override
    public TaskTableModel getTaskModel() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setTaskModel(TaskTableModel taskModel) {
        children.forEach(child -> child.setTaskModel(taskModel));
    }
}