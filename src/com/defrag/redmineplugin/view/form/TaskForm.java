package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.table.JBTable;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;

public class TaskForm extends JDialog implements ValidatedDialog {

    @Getter
    private JPanel contentPane;

    private JComboBox statusCmbx;

    private JBTable logWorkTable;

    private JLabel addLogWorkLbl;
    private JLabel editLogWorkLbl;
    private JLabel deleteLogWorkLbl;

    public TaskForm() {
        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        logWorkTable.setModel(new LogWorkTableModel());
        logWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logWorkTable.setStriped(true);
        logWorkTable.setExpandableItemsEnabled(false);

        logWorkTable.setRowHeight(20);
        logWorkTable.getColumnModel().getColumn(0).setResizable(false);
        logWorkTable.getColumnModel().getColumn(1).setResizable(false);
        logWorkTable.getColumnModel().getColumn(2).setResizable(false);

        logWorkTable.getColumnModel().getColumn(0).setMinWidth(100);
        logWorkTable.getColumnModel().getColumn(1).setMinWidth(100);
        logWorkTable.getColumnModel().getColumn(2).setMinWidth(900);
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

    class LogWorkTableModel extends DefaultTableModel {

        public LogWorkTableModel() {
            addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));
            addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));
            addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));
            addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));
            addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));
            addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));addRow(new Vector<Object>(Arrays.asList(
                    LocalDate.now(),
                    LogWork.Type.DEVELOPMENT,
                    "Сделано все хорошо"
            )));



        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return LocalDate.class;
            }
            if (columnIndex == 1) {
                return LogWork.Type.class;
            }
            return String.class;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Date";
                case 1:
                    return "Type";
                case 2:
                    return "Description";
            }
            return super.getColumnName(column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}