package com.defrag.redmineplugin.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by defrag on 29.08.17.
 */
public abstract class ValidatedFormWrapper<T> extends DialogWrapper {

    protected final ValidatedDialog<T> validatedDialog;

    public ValidatedFormWrapper(@Nullable Project project, ValidatedDialog<T> validatedDialog, String titleName) {
        super(project);
        this.validatedDialog = validatedDialog;

        init();
        setTitle(titleName);
        setValidationDelay(1000);
    }

    public T getData() {
        return validatedDialog.getData();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return validatedDialog.getValidationInfo().orElse(null);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return validatedDialog.getContentPane();
    }
}