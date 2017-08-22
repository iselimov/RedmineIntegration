package com.defrag.redmineplugin.view;

import com.defrag.redmineplugin.model.TaskStatus;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusItemNode extends SimpleNode {

    private final TaskStatus status;

    public StatusItemNode(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return status.getValue();
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

    }
}