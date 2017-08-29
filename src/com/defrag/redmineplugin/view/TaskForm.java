package com.defrag.redmineplugin.view;

import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;

import javax.swing.*;
import java.util.Optional;

public class TaskForm extends JDialog implements ValidatedDialog {

    @Getter
    private JPanel contentPane;

    private JComboBox statusCmbx;

    private JTable logWorkTable;

    private JLabel addLogWorkLbl;

    public TaskForm() {
        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        return Optional.empty();
    }

    public static void main(String[] args) {
        TaskForm dialog = new TaskForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
