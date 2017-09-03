package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.EnumInnerFieldWorker;
import com.defrag.redmineplugin.service.TaskManager;
import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by defrag on 03.09.17.
 */
public abstract class TaskItemNode extends SimpleNode {

    private final TaskManagerConsumer root;

    private final EnumInnerFieldWorker itemNode;

    public TaskItemNode(TaskManagerConsumer root,
                        EnumInnerFieldWorker itemNode) {
        this.root = root;
        this.itemNode = itemNode;
    }

    @Override
    public String getName() {
        return itemNode.getValue();
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }

    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }

    TaskManager getTaskManager() {
        return root.getTaskManager();
    }
}