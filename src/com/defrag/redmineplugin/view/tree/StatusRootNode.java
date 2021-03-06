package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;
import com.defrag.redmineplugin.service.util.ViewLogger;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusRootNode extends TaskRootNode {

    public StatusRootNode(RedmineFilter[] filterItems, ViewLogger viewLogger) {
        super(filterItems, viewLogger);
    }

    @Override
    public String getName() {
        return "Status filter";
    }
}