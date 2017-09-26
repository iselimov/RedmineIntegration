package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.model.TaskType;
import com.defrag.redmineplugin.service.util.ViewLogger;

import java.util.stream.Stream;

/**
 * Created by defrag on 03.09.17.
 */
public class TypeRootNode extends TaskRootNode {

    public TypeRootNode(ViewLogger viewLogger) {
        addChildren(viewLogger);
    }

    @Override
    public String getName() {
        return "Type filter";
    }

    private void addChildren(ViewLogger viewLogger) {
        Stream.of(TaskType.values())
                .forEach(status -> children.add(new TypeItemNode(this, status, viewLogger)));
    }
}