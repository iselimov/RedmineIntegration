package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.Report;
import com.defrag.redmineplugin.model.ReportInfo;
import com.defrag.redmineplugin.service.util.ConvertUtils;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.form.wrapper.ReportInfoFormWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Date;
import java.util.Optional;

public class ReportForm extends JDialog implements ValidatedDialog<Report> {

    @Getter
    private JPanel contentPane;

    private JTextArea tomorrowArea;

    private JButton settingsBut;

    private JTextArea questionsArea;

    private JSpinner reportDateSpinner;

    private ReportInfo reportInfo;

    public ReportForm(Project project, ReportInfo reportInfo, ViewLogger viewLogger) {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.reportInfo = reportInfo;
        reportDateSpinner.setModel(new SpinnerDateModel());
        addButtonListeners(project, viewLogger);
    }

    private void addButtonListeners(Project project, ViewLogger viewLogger) {
        settingsBut.addActionListener(e -> {
            ReportInfoFormWrapper wrapper = new ReportInfoFormWrapper(project, new ReportInfoForm(this.reportInfo, viewLogger));
            wrapper.show();
            if (wrapper.isOK()) {
                this.reportInfo = wrapper.getData();
            }
        });
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        if (reportInfo == null) {
            return Optional.of(new ValidationInfo("Необходимо заполнить настройки отчета!", settingsBut));
        }

        if (reportDateSpinner.getValue() == null) {
            return Optional.of(new ValidationInfo("Необходимо заполнить дату, на которую должен формироваться отчет!",
                    reportDateSpinner));
        }

        if (StringUtils.isBlank(tomorrowArea.getText())) {
            return Optional.of(new ValidationInfo("Необходимо заполнить планы на следующий рабочий день!", tomorrowArea));
        }

        return Optional.empty();
    }

    @Override
    public Report getData() {
        return Report.builder()
                .reportInfo(reportInfo)
                .date(ConvertUtils.toLocalDate((Date) reportDateSpinner.getValue()))
                .tomorrow(tomorrowArea.getText())
                .questions(questionsArea.getText())
                .build();
    }
}