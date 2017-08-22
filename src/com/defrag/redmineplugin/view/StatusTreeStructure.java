package com.defrag.redmineplugin.view;

import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTreeStructure;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusTreeStructure extends SimpleTreeStructure {

    private final SimpleNode root;

    public StatusTreeStructure(SimpleNode root) {
        this.root = root;
    }

    @Override
    public Object getRootElement() {
        return root;
    }
}