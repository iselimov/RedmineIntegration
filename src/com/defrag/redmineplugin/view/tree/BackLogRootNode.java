package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;
import com.defrag.redmineplugin.service.util.ViewLogger;

public class BackLogRootNode extends TaskRootNode {

    public BackLogRootNode(RedmineFilter[] filterItems, ViewLogger viewLogger) {
        super(filterItems, viewLogger);
    }

    @Override
    public String getName() {
        return "Back log filter";
    }
}