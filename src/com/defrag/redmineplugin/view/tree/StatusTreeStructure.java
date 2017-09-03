package com.defrag.redmineplugin.view.tree;

import com.intellij.ui.treeStructure.SimpleTreeStructure;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusTreeStructure extends SimpleTreeStructure {

    private final TaskManagerConsumer rootNode;

    public StatusTreeStructure(TaskManagerConsumer rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public Object getRootElement() {
        return rootNode;
    }
}