package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Optional;

public class SettingsForm extends JDialog implements ValidatedDialog<ConnectionInfo> {

    @Getter
    private JPanel contentPane;

    private JTextField redmineUriTxt;

    private JTextField apiAccessKeyTxt;

    private JTextField cookieTxt;

    private JTextField csrfTokenTxt;

    private ConnectionInfo connectionInfo;

    public SettingsForm(ConnectionInfo connectionInfo) {
        redmineUriTxt.setText(connectionInfo.getRedmineUri());
        apiAccessKeyTxt.setText(connectionInfo.getApiAccessKey());
        cookieTxt.setText(connectionInfo.getCookie());
        csrfTokenTxt.setText(connectionInfo.getCsrfToken());

        this.connectionInfo = connectionInfo;

        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        if (StringUtils.isBlank(redmineUriTxt.getText())) {
            return Optional.of(new ValidationInfo("URL для Redmine должен быть заполнен!", redmineUriTxt));
        }

        if (StringUtils.isBlank(apiAccessKeyTxt.getText())) {
            return Optional.of(new ValidationInfo("Ключ доступа к API Redmine должен быть заполнен!", apiAccessKeyTxt));
        }

        return Optional.empty();
    }

    @Override
    public ConnectionInfo getData() {
        connectionInfo.setRedmineUri(redmineUriTxt.getText());
        connectionInfo.setApiAccessKey(apiAccessKeyTxt.getText());
        connectionInfo.setCookie(cookieTxt.getText());
        connectionInfo.setCsrfToken(csrfTokenTxt.getText());

        return connectionInfo;
    }
}