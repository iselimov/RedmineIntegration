package com.defrag.redmineplugin.view.form.wrapper;

import com.defrag.redmineplugin.model.ReportInfo;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 14.09.17.
 */
public class ReportInfoFormWrapper extends ValidatedFormWrapper<ReportInfo> {

    public ReportInfoFormWrapper(@Nullable Project project, ValidatedDialog<ReportInfo> reportInfoForm) {
        super(project, reportInfoForm);
    }

    @Override
    protected String getTitleName() {
        return "Настройки отчета";
    }
}