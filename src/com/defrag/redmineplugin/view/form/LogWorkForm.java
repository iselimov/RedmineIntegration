package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EnumComboBoxModel;
import lombok.Getter;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class LogWorkForm extends JDialog implements ValidatedDialog<LogWork> {

    private LogWork logWork;

    @Getter
    private JPanel contentPane;

    private JComboBox<LogWork.Type> workTypeCmbx;

    private JSpinner timeSpinner;

    private JTextArea commentTArea;

    private JSpinner dateSpinner;

    public LogWorkForm() {
        workTypeCmbx.setModel(new EnumComboBoxModel<>(LogWork.Type.class));
        timeSpinner.setModel(new SpinnerNumberModel(0.2, 0.2, 8, 0.2));
        dateSpinner.setModel(new SpinnerDateModel());

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public LogWorkForm(LogWork logWork) {
        this();
        this.logWork = logWork;

        workTypeCmbx.setSelectedItem(logWork.getType());
        timeSpinner.setValue(logWork.getTime());
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
        Float time = ((Double) timeSpinner.getValue()).floatValue();

        LocalDate date = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd")
                .format((Date) dateSpinner.getValue()), DateTimeFormatter.ISO_DATE);

        LogWork updated = new LogWork(date, type, description, time);

        if (logWork != null) {
            updated.setId(logWork.getId());
        }

        return updated;
    }
}