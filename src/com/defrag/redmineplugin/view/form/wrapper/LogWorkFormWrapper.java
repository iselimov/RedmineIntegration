package com.defrag.redmineplugin.view.form.wrapper;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by i.selimov on 07.09.17.
 */
public class LogWorkFormWrapper extends ValidatedFormWrapper<LogWork> {

    public LogWorkFormWrapper(@Nullable Project project, ValidatedDialog<LogWork> logWorkForm) {
        super(project, logWorkForm);
    }

    @Override
    protected String getTitleName() {
        return "Log work";
    }
}