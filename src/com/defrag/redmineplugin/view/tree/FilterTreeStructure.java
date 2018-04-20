package com.defrag.redmineplugin.view.tree;

import com.intellij.ui.treeStructure.SimpleTreeStructure;

/**
 * Created by defrag on 23.07.17.
 */
public class FilterTreeStructure extends SimpleTreeStructure {

    private final TaskManagerConsumer rootNode;

    public FilterTreeStructure(TaskManagerConsumer rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public Object getRootElement() {
        return rootNode;
    }
}