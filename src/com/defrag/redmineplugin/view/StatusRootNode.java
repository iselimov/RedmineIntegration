package com.defrag.redmineplugin.view;

import com.defrag.redmineplugin.model.TaskStatus;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by defrag on 23.07.17.
 */
public class StatusRootNode extends SimpleNode {

    private final List<SimpleNode> myChildren = new ArrayList<>();

    public StatusRootNode() {
        addChildren();
    }

    @Override
    public String getName() {
        return "Tasks Status";
    }

    @Override
    public SimpleNode[] getChildren() {
        if (myChildren.isEmpty())
            addChildren();

        return myChildren.toArray(new SimpleNode[myChildren.size()]);
    }

    private void addChildren() {
        Stream.of(TaskStatus.values())
                .forEach(status -> myChildren.add(new StatusItemNode(status)));
    }
}