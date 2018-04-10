package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.service.util.ConvertUtils;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EnumComboBoxModel;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class LogWorkForm extends JDialog implements ValidatedDialog<LogWork> {

    private static int COMMENT_MAX_LENGTH = 255;

    private LogWork logWork;

    @Getter
    private JPanel contentPane;

    private JComboBox<LogWork.Type> workTypeCmbx;

    private JSpinner timeSpinner;

    private JTextArea commentArea;

    private JSpinner dateSpinner;

    private JLabel lettersCounterTxt;

    public LogWorkForm() {
        workTypeCmbx.setModel(new EnumComboBoxModel<>(LogWork.Type.class));
        timeSpinner.setModel(new SpinnerNumberModel(0.2d, 0.2d, 8d, 0.2d));
        dateSpinner.setModel(new SpinnerDateModel());
        commentArea.addCaretListener(e ->
                lettersCounterTxt.setText(calcCommentLeftLetterLength(commentArea.getText())));
        lettersCounterTxt.setText(String.valueOf(COMMENT_MAX_LENGTH));

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public LogWorkForm(LogWork logWork) {
        this();
        this.logWork = logWork;

        workTypeCmbx.setSelectedItem(logWork.getType());
        timeSpinner.setValue(logWork.getTime().doubleValue());
        dateSpinner.setValue(java.sql.Date.valueOf(logWork.getDate()));
        commentArea.setText(logWork.getDescription());
        lettersCounterTxt.setText(calcCommentLeftLetterLength(logWork.getDescription()));
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        if (StringUtils.isBlank(commentArea.getText())) {
            return Optional.of(new ValidationInfo("Необходимо заполнить комментарий", commentArea));
        }
        if (commentArea.getText().length() > COMMENT_MAX_LENGTH) {
            return Optional.of(new ValidationInfo("Длина комментария должна быть меньше 255 символов", commentArea));
        }
        return Optional.empty();
    }

    @Override
    public LogWork getData() {
        LogWork.Type type = (LogWork.Type) workTypeCmbx.getSelectedItem();
        String description = commentArea.getText();
        Float time = ((Double) timeSpinner.getValue()).floatValue();
        LocalDate date = ConvertUtils.toLocalDate((Date) dateSpinner.getValue());
        LogWork updated = new LogWork(date, type, description, time);
        if (logWork != null) {
            updated.setId(logWork.getId());
        }
        return updated;
    }

    private String calcCommentLeftLetterLength(String comment) {
        return String.valueOf(COMMENT_MAX_LENGTH - comment.length());
    }
}