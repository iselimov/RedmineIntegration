package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.model.TaskStatus;
import com.defrag.redmineplugin.service.util.ViewLogger;

import java.util.stream.Stream;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusRootNode extends TaskRootNode {

    public StatusRootNode(ViewLogger viewLogger) {
        addChildren(viewLogger);
    }

    @Override
    public String getName() {
        return "Status filter";
    }

    private void addChildren(ViewLogger viewLogger) {
        Stream.of(TaskStatus.values())
                .forEach(status -> children.add(new StatusItemNode(this, status, viewLogger)));
    }
}