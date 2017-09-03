package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.model.TaskStatus;

import java.util.stream.Stream;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusRootNode extends TaskRootNode {

    public StatusRootNode() {
        addChildren();
    }

    @Override
    public String getName() {
        return "Status filter";
    }

    void addChildren() {
        Stream.of(TaskStatus.values())
                .forEach(status -> children.add(new StatusItemNode(this, status)));
    }
}