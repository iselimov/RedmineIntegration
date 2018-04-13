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
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class ReportForm extends JDialog implements ValidatedDialog<Report> {

    @Getter
    private JPanel contentPane;

    private JTextArea tomorrowArea;

    private JButton settingsBut;

    private JTextArea questionsArea;

    private JSpinner reportDateSpinner;

    private JCheckBox reportPeriodCbx;

    private JSpinner reportDateFromSpinner;

    private ReportInfo reportInfo;

    public ReportForm(Project project, ReportInfo reportInfo, ViewLogger viewLogger) {
        this.reportInfo = reportInfo;
        reportDateSpinner.setModel(new SpinnerDateModel());
        reportDateFromSpinner.setModel(new SpinnerDateModel());
        reportDateFromSpinner.setVisible(false);
        addSettingsButtonListener(project, viewLogger);
        addReportPeriodCbxListener();

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
        if (reportPeriodCbx.isSelected()) {
            if (reportDateFromSpinner.getValue() == null) {
                return Optional.of(new ValidationInfo("Необходимо заполнить дату начала, на которую должен " +
                        "формироваться отчет!",reportDateFromSpinner));
            }
            LocalDate dateFrom = ConvertUtils.toLocalDate((Date) reportDateFromSpinner.getValue());
            LocalDate dateNow = ConvertUtils.toLocalDate((Date) reportDateSpinner.getValue());
            if (dateFrom.equals(dateNow)) {
                return Optional.of(new ValidationInfo("Даты начала и окончания периода отчета не должны совпадать",
                        reportDateFromSpinner));
            }
            if (dateFrom.isAfter(dateNow)) {
                return Optional.of(new ValidationInfo("Даты окончания периода отчета должна быть больше даты начала",
                        reportDateFromSpinner));
            }
        }
        if (StringUtils.isBlank(tomorrowArea.getText())) {
            return Optional.of(new ValidationInfo("Необходимо заполнить планы на следующий рабочий день!", tomorrowArea));
        }
        return Optional.empty();
    }

    @Override
    public Report getData() {
        LocalDate dateFrom = reportPeriodCbx.isSelected()
                ? ConvertUtils.toLocalDate((Date) reportDateFromSpinner.getValue())
                : null;
        return Report.builder()
                .reportInfo(reportInfo)
                .dateFrom(Optional.ofNullable(dateFrom))
                .dateNow(ConvertUtils.toLocalDate((Date) reportDateSpinner.getValue()))
                .tomorrow(tomorrowArea.getText())
                .questions(questionsArea.getText())
                .build();
    }

    private void addSettingsButtonListener(Project project, ViewLogger viewLogger) {
        settingsBut.addActionListener(e -> {
            ReportInfoFormWrapper wrapper = new ReportInfoFormWrapper(project, new ReportInfoForm(this.reportInfo, viewLogger));
            wrapper.show();
            if (wrapper.isOK()) {
                this.reportInfo = wrapper.getData();
            }
        });
    }

    private void addReportPeriodCbxListener() {
        reportPeriodCbx.addItemListener(e -> {
            if (((JCheckBox)e.getItem()).isSelected()) {
                reportDateFromSpinner.setVisible(true);
            } else {
                reportDateFromSpinner.setVisible(false);
            }
        });
    }
}