package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.service.RedmineFilter;
import com.defrag.redmineplugin.service.util.ViewLogger;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusItemNode extends TaskItemNode {

    public StatusItemNode(TaskManagerConsumer root, RedmineFilter status, ViewLogger viewLogger) {
        super(root, status, viewLogger);
    }
}