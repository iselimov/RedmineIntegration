package com.defrag.redmineplugin.view.form.wrapper;

import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 18.10.17.
 */
public class CheckoutBranchFormWrapper extends ValidatedFormWrapper<String> {

    public CheckoutBranchFormWrapper(@Nullable Project project, ValidatedDialog<String> validatedDialog, Task task) {
        super(project, validatedDialog, String.format("Create branch for task #%d - %s", task.getId(), task.getSubject()));
    }
}