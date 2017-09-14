package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.ReportInfo;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Optional;

public class ReportInfoForm extends JDialog implements ValidatedDialog<ReportInfo> {

    @Getter
    private JPanel contentPane;

    private JTextField fullNameTxt;

    private JTextField positionTxt;

    private JTextField phoneTxt;

    private JTextField domainNameTxt;

    private JTextField skypeTxt;

    public ReportInfoForm() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public ReportInfoForm(ReportInfo reportInfo) {
        this();

        if (reportInfo != null) {
            fullNameTxt.setText(reportInfo.getFullName());
            positionTxt.setText(reportInfo.getPosition());
            phoneTxt.setText(reportInfo.getPhone());
            domainNameTxt.setText(reportInfo.getDomainName());
            skypeTxt.setText(reportInfo.getSkype());
        }
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {

        if (StringUtils.isBlank(fullNameTxt.getText())) {
            return Optional.of(new ValidationInfo("Имя и фамилия должны быть заполнены!", fullNameTxt));
        }

        if (StringUtils.isBlank(positionTxt.getText())) {
            return Optional.of(new ValidationInfo("Должность дожна быть заполнена!", positionTxt));
        }

        if (StringUtils.isBlank(phoneTxt.getText())) {
            return Optional.of(new ValidationInfo("Телефон должен быть заполнен!", phoneTxt));
        }

        if (StringUtils.isBlank(domainNameTxt.getText())) {
            return Optional.of(new ValidationInfo("Имя домена должно быть заполнено!", domainNameTxt));
        }

        if (StringUtils.isBlank(skypeTxt.getText())) {
            return Optional.of(new ValidationInfo("Логин скайпа должен быть заполнен!", skypeTxt));
        }

        return Optional.empty();
    }

    @Override
    public ReportInfo getData() {
        return ReportInfo.builder()
                .fullName(fullNameTxt.getText())
                .position(positionTxt.getText())
                .phone(phoneTxt.getText())
                .domainName(domainNameTxt.getText())
                .skype(skypeTxt.getText())
                .build();
    }
}