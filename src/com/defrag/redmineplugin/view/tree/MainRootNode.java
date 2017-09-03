package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.TaskManager;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by defrag on 03.09.17.
 */
public class MainRootNode extends SimpleNode implements TaskManagerConsumer {

    private final List<TaskManagerConsumer> children = new ArrayList<>();

    public MainRootNode() {
        addChildren();
    }

    @Override
    public String getName() {
        return "Tasks filter";
    }

    @Override
    public SimpleNode[] getChildren() {
        if (children.isEmpty()) {
            addChildren();
        }

        return children.toArray(new SimpleNode[children.size()]);
    }

    private void addChildren() {
        children.add(new StatusRootNode());
        children.add(new TypeRootNode());
    }

    @Override
    public TaskManager getTaskManager() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setTaskManager(TaskManager taskManager) {
        children.forEach(child -> child.setTaskManager(taskManager));
    }
}