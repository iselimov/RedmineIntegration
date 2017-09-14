package com.defrag.redmineplugin.view.form.wrapper;

import com.defrag.redmineplugin.model.Report;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 14.09.17.
 */
public class ReportFormWrapper extends ValidatedFormWrapper<Report> {

    public ReportFormWrapper(@Nullable Project project, ValidatedDialog<Report> reportsForm) {
        super(project, reportsForm);
    }

    @Override
    protected String getTitleName() {
        return "Отправка отчета";
    }
}