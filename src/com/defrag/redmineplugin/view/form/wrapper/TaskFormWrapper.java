package com.defrag.redmineplugin.view.form.wrapper;

import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 07.09.17.
 */
public class TaskFormWrapper extends ValidatedFormWrapper<Task> {

    public TaskFormWrapper(@Nullable Project project, ValidatedDialog<Task> taskForm, Integer taskId) {
        super(project, taskForm, String.format("Edit task #%d", taskId));
    }
}