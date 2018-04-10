package com.defrag.redmineplugin.view.tree;

import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.service.RedmineFilter;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;

import java.util.List;

/**
 * Created by defrag on 03.09.17.
 */
public abstract class TaskItemNode extends SimpleNode {

    private final TaskManagerConsumer root;

    @Getter
    private final RedmineFilter itemNode;

    private final ViewLogger viewLogger;

    public TaskItemNode(TaskManagerConsumer root,
                        RedmineFilter itemNode,
                        ViewLogger viewLogger) {
        this.root = root;
        this.itemNode = itemNode;
        this.viewLogger = viewLogger;
    }

    @Override
    public String getName() {
        return itemNode.getName();
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }

    @Override
    public SimpleNode[] getChildren() {
        return new SimpleNode[0];
    }

    @Override
    public void handleSelection(SimpleTree tree) {
        if (root.getTaskManager() == null) {
            viewLogger.warning("Необходимо сгенерировать настройки плагина");
            return;
        }
        List<Task> tasks = root.getTaskManager().getTasks(RedmineFilter.getFilter(itemNode));
        root.getTaskModel().updateModel(tasks);
    }
}