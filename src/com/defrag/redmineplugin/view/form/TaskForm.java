package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.model.TaskStatus;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.table.JBTable;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskForm extends JDialog implements ValidatedDialog<Task> {

    private final Task task;

    private LogWorkTableModel logWorkModel;

    @Getter
    private JPanel contentPane;

    private JComboBox<String> statusCmbx;

    private JBTable logWorkTable;

    private JButton addLogWorkBut;

    private JButton editLogWorkBut;

    private JButton removeLogWorkBut;

    public TaskForm(Project project, Task task) {
        this.task = task;

        logWorkModel = new LogWorkTableModel(task);
        logWorkTable.setModel(logWorkModel);
        logWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logWorkTable.setStriped(true);
        logWorkTable.setExpandableItemsEnabled(false);
        logWorkTable.setRowHeight(20);
        logWorkTable.getColumnModel().getColumn(0).setResizable(false);
        logWorkTable.getColumnModel().getColumn(1).setResizable(false);
        logWorkTable.getColumnModel().getColumn(2).setResizable(false);
        logWorkTable.getColumnModel().getColumn(0).setMinWidth(100);
        logWorkTable.getColumnModel().getColumn(1).setMinWidth(150);
        logWorkTable.getColumnModel().getColumn(2).setMinWidth(900);

        addLogWorkBut.addActionListener(e -> new LogWorkFormWrapper(project, new LogWorkForm()).show());
        editLogWorkBut.addActionListener(e -> {
            if (logWorkTable.getSelectedRow() == -1) {
                return;
            }

            LogWork selected = task.getLogWorks().get(logWorkTable.getSelectedRow());
            new LogWorkFormWrapper(project, new LogWorkForm(selected)).show();
        });
        removeLogWorkBut.addActionListener(e -> {
            if (logWorkTable.getSelectedRow() == -1) {
                return;
            }

            logWorkModel.removeRow(logWorkTable.getSelectedRow());
        });

        List<String> statuses = Stream.of(TaskStatus.values())
                .map(TaskStatus::getName)
                .collect(Collectors.toList());

        statusCmbx.setModel(new CollectionComboBoxModel<>(statuses));
        statusCmbx.setSelectedItem(task.getStatus().getName());

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }


    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        return Optional.empty();
    }

    @Override
    public Task getData() {
        task.getLogWorks().clear();



        return task;
    }

    class LogWorkTableModel extends DefaultTableModel {

        LogWorkTableModel(Task task) {
            task.getLogWorks().forEach(lw -> addRow(new Object[]{
                    lw.getDate(),
                    lw.getType(),
                    lw.getDescription(),
                    lw.getId()
            }));
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

    class LogWorkFormWrapper extends ValidatedFormWrapper<LogWork> {

        LogWorkFormWrapper(@Nullable Project project, ValidatedDialog<LogWork> logWorkForm) {
            super(project, logWorkForm);
        }

        @Override
        protected String getTitleName() {
            return "Log work";
        }

        @Override
        protected void doOKAction() {

            super.doOKAction();
        }
    }
}