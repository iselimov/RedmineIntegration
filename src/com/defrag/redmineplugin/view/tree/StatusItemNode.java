package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;
import com.intellij.ui.treeStructure.SimpleTree;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusItemNode extends TaskItemNode {

    public StatusItemNode(TaskManagerConsumer root, RedmineFilter status) {
        super(root, status);
    }

    @Override
    public void handleSelection(SimpleTree tree) {

    }
}