package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;
import com.defrag.redmineplugin.service.util.ViewLogger;

/**
 * Created by defrag on 03.09.17.
 */
public class TypeRootNode extends TaskRootNode {

    public TypeRootNode(RedmineFilter[] filterItems, ViewLogger viewLogger) {
        super(filterItems, viewLogger);
    }

    @Override
    public String getName() {
        return "Type filter";
    }
}