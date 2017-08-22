package com.defrag.redmineplugin.view;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusTreeModel extends DefaultTreeModel {

    public StatusTreeModel() {
        super(new DefaultMutableTreeNode(), false);
    }
}