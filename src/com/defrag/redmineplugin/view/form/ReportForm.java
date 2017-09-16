package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.Report;
import com.defrag.redmineplugin.model.ReportInfo;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.form.wrapper.ReportInfoFormWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Optional;

public class ReportForm extends JDialog implements ValidatedDialog<Report> {

    @Getter
    private JPanel contentPane;

    private JTextArea tomorrowArea;

    private JButton settingsBut;

    private JTextArea questionsArea;

    private ReportInfo reportInfo;

    public ReportForm(Project project, ReportInfo reportInfo) {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.reportInfo = reportInfo;
        addButtonListeners(project);
    }

    private void addButtonListeners(Project project) {
        settingsBut.addActionListener(e -> {
            ReportInfoFormWrapper wrapper = new ReportInfoFormWrapper(project, new ReportInfoForm(this.reportInfo));
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

        if (StringUtils.isBlank(tomorrowArea.getText())) {
            return Optional.of(new ValidationInfo("Необходимо заполнить планы на следующий рабочий день!", tomorrowArea));
        }

        return Optional.empty();
    }

    @Override
    public Report getData() {
        return Report.builder()
                .reportInfo(reportInfo)
                .tomorrow(tomorrowArea.getText())
                .questions(questionsArea.getText())
                .build();
    }
}