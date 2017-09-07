package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EnumComboBoxModel;
import lombok.Getter;

import javax.swing.*;
import java.util.Optional;

public class LogWorkForm extends JDialog implements ValidatedDialog<LogWork> {

    @Getter
    private JPanel contentPane;

    private JComboBox<LogWork.Type> workTypeCmbx;

    private JSpinner timeSpinner;

    private JTextArea commentTArea;

    public LogWorkForm() {
        workTypeCmbx.setModel(new EnumComboBoxModel<>(LogWork.Type.class));
        timeSpinner.setModel(new SpinnerNumberModel(0.2, 0.2, 8, 0.2));

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public LogWorkForm(LogWork logWork) {
        this();

        workTypeCmbx.setSelectedItem(logWork.getType());
        timeSpinner.setValue(logWork.getValue());
        commentTArea.setText(logWork.getDescription());
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        return Optional.empty();
    }

    @Override
    public LogWork getData() {
        return null;
    }
}