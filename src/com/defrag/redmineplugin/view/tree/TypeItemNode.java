package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.EnumInnerFieldWorker;
import com.intellij.ui.treeStructure.SimpleTree;

/**
 * Created by defrag on 03.09.17.
 */
public class TypeItemNode extends TaskItemNode {

    public TypeItemNode(TaskManagerConsumer root, EnumInnerFieldWorker type) {
        super(root, type);
    }

    @Override
    public void handleSelection(SimpleTree tree) {

    }
}