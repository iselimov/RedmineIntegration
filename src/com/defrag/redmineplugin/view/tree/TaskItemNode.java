package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;

/**
 * Created by defrag on 03.09.17.
 */
public abstract class TaskItemNode extends SimpleNode {

    private final TaskManagerConsumer root;

    private final RedmineFilter itemNode;

    public TaskItemNode(TaskManagerConsumer root,
                        RedmineFilter itemNode) {
        this.root = root;
        this.itemNode = itemNode;
    }

    @Override
    public String getName() {
        return itemNode.getName();
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }

    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }

    @Override
    public void handleSelection(SimpleTree tree) {
        root.getTaskManager().getTasks(RedmineFilter.getFilter(itemNode));
    }
}