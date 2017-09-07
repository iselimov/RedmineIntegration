package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EnumComboBoxModel;
import lombok.Getter;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Optional;

public class LogWorkForm extends JDialog implements ValidatedDialog<LogWork> {

    private LogWork logWork;
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

        this.logWork = logWork;

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
        LogWork.Type type = (LogWork.Type) workTypeCmbx.getSelectedItem();
        String description = commentTArea.getText();
        Float value = (Float) timeSpinner.getValue();

        LogWork updated = new LogWork(LocalDate.now(), type, description, value);

        if (logWork != null) {
            updated.setId(logWork.getId());
        }

        return updated;
    }
}