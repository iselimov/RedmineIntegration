package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;

/**
 * Created by defrag on 03.09.17.
 */
public class TypeItemNode extends TaskItemNode {

    public TypeItemNode(TaskManagerConsumer root, RedmineFilter type) {
        super(root, type);
    }
}