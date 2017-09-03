package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.model.TaskType;

import java.util.stream.Stream;

/**
 * Created by defrag on 03.09.17.
 */
public class TypeRootNode extends TaskRootNode {

    public TypeRootNode() {
        addChildren();
    }

    @Override
    public String getName() {
        return "Type filter";
    }

    void addChildren() {
        Stream.of(TaskType.values())
                .forEach(status -> children.add(new TypeItemNode(this, status)));
    }
}