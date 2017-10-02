package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.ReportInfo;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReportInfoForm extends JDialog implements ValidatedDialog<ReportInfo> {

    private static final String MAIL_PATTERN_FORMAT = "^\\S+@\\S+$";

    private final ViewLogger viewLogger;

    @Getter
    private JPanel contentPane;

    private JTextField fullNameTxt;

    private JTextField positionTxt;

    private JTextField phoneTxt;

    private JTextField domainNameTxt;

    private JTextField skypeTxt;

    private JTextField emailFromTxt;

    private JTextField emailsToTxt;

    private ReportInfo reportInfo;

    public ReportInfoForm(ReportInfo reportInfo, ViewLogger viewLogger) {
        fullNameTxt.setText(reportInfo.getFullName());
        positionTxt.setText(reportInfo.getPosition());
        phoneTxt.setText(reportInfo.getPhone());
        domainNameTxt.setText(reportInfo.getDomainName());
        skypeTxt.setText(reportInfo.getSkype());
        emailFromTxt.setText(reportInfo.getEmailFrom());
        emailsToTxt.setText(StringUtils.join(reportInfo.getEmailsTo(), ';'));

        this.reportInfo = reportInfo;
        this.viewLogger = viewLogger;

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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

        if (StringUtils.isBlank(emailFromTxt.getText())
                || !isValidEmailFormat(emailFromTxt.getText())) {
            return Optional.of(new ValidationInfo("Почта отправителя должна быть заполнена!", emailFromTxt));
        }

        if (StringUtils.isBlank(emailsToTxt.getText())
                || !isValidEmailFormat(emailsToTxt.getText())) {
            return Optional.of(new ValidationInfo("Почта получателей должна быть заполнена без пробелов через ';'!",
                    emailsToTxt));
        }

        return Optional.empty();
    }

    @Override
    public ReportInfo getData() {
        String[] emailsTo = emailsToTxt.getText().split(";");

        boolean correctEmailsTo = Stream.of(emailsTo)
                .allMatch(this::isValidEmailFormat);
        if (!correctEmailsTo) {
            viewLogger.error("Некорректный формат email");
        }

        reportInfo.setFullName(fullNameTxt.getText());
        reportInfo.setPosition(positionTxt.getText());
        reportInfo.setPhone(phoneTxt.getText());
        reportInfo.setDomainName(domainNameTxt.getText());
        reportInfo.setSkype(skypeTxt.getText());
        reportInfo.setEmailFrom(emailFromTxt.getText());
        reportInfo.setEmailsTo(emailsTo);

        return reportInfo;
    }

    private boolean isValidEmailFormat(String email) {
        return Pattern.compile(MAIL_PATTERN_FORMAT).matcher(email).find();
    }
}