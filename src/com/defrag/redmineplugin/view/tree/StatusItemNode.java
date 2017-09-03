package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.EnumInnerFieldWorker;
import com.intellij.ui.treeStructure.SimpleTree;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusItemNode extends TaskItemNode {

    public StatusItemNode(TaskManagerConsumer root, EnumInnerFieldWorker status) {
        super(root, status);
    }

    public void handleSelection(SimpleTree tree) {

    }
}