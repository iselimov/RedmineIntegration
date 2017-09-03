package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EnumComboBoxModel;
import lombok.Getter;

import javax.swing.*;
import java.util.Optional;

public class LogWorkForm extends JDialog implements ValidatedDialog {

    @Getter
    private JPanel contentPane;

    private JComboBox workTypeCmbx;

    private JSpinner timeSpinner;

    private JTextArea commentTArea;

    public LogWorkForm() {
        setContentPane(contentPane);
        setModal(true);

        workTypeCmbx.setModel(new EnumComboBoxModel<>(LogWork.Type.class));
        timeSpinner.setModel(new SpinnerNumberModel(0.2, 0.2, 8, 0.2));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        return null;
    }

    public static void main(String[] args) {
        LogWorkForm dialog = new LogWorkForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
