package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.model.TaskComment;
import com.defrag.redmineplugin.model.TaskStatus;
import com.defrag.redmineplugin.service.RedmineFilter;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.form.model.LogWorkTableModel;
import com.defrag.redmineplugin.view.form.wrapper.LogWorkFormWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.table.JBTable;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
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

    private JPanel changeEstimatePane;

    private JSpinner estimateSpinner;

    private JPanel changeStatusPane;

    private JTextArea changeEstimateArea;

    private JTextArea changeStatusArea;

    public TaskForm(Project project, Task task, boolean canChangeTask) {
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
        logWorkTable.getColumnModel().getColumn(3).setResizable(false);
        logWorkTable.getColumnModel().getColumn(0).setMinWidth(100);
        logWorkTable.getColumnModel().getColumn(0).setMaxWidth(100);
        logWorkTable.getColumnModel().getColumn(1).setMinWidth(120);
        logWorkTable.getColumnModel().getColumn(1).setMaxWidth(120);
        logWorkTable.getColumnModel().getColumn(2).setMinWidth(50);
        logWorkTable.getColumnModel().getColumn(2).setMaxWidth(50);
        logWorkTable.getColumnModel().getColumn(3).setMinWidth(900);
        logWorkTable.getColumnModel().getColumn(3).setMaxWidth(900);

        addButtonListeners(project);
        addStatusChangeListener();
        addEstimateChangeListener();

        List<String> statuses = Stream.of(TaskStatus.values())
                .map(TaskStatus::getName)
                .collect(Collectors.toList());

        statusCmbx.setModel(new CollectionComboBoxModel<>(statuses));
        statusCmbx.setSelectedItem(task.getStatus().getName());

        estimateSpinner.setModel(new SpinnerNumberModel(0d, 0d, 8d, 0.2d));
        if (task.getEstimate() != null) {
            estimateSpinner.setValue(task.getEstimate().doubleValue());
        }
        if (!canChangeTask) {
            statusCmbx.setEnabled(false);
            estimateSpinner.setEnabled(false);
        }

        changeEstimatePane.setVisible(false);
        changeStatusPane.setVisible(false);

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void addButtonListeners(Project project) {
        addLogWorkBut.addActionListener(e -> {
            LogWorkFormWrapper wrapper = new LogWorkFormWrapper(project, new LogWorkForm());
            wrapper.show();
            if (wrapper.isOK()) {
                LogWork toAdd = wrapper.getData();
                logWorkModel.createLogWork(toAdd);
            }
        });

        editLogWorkBut.addActionListener(e -> {
            int selectedRowIndex = logWorkTable.getSelectedRow();

            if (selectedRowIndex == -1) {
                return;
            }

            LogWorkFormWrapper wrapper = new LogWorkFormWrapper(project, new LogWorkForm(logWorkModel.getLogWork(selectedRowIndex)));
            wrapper.show();
            if (wrapper.isOK()) {
                LogWork updated = wrapper.getData();
                logWorkModel.updateLogWork(selectedRowIndex, updated);
            }
        });

        removeLogWorkBut.addActionListener(e -> {
            if (logWorkTable.getSelectedRow() == -1) {
                return;
            }

            logWorkModel.removeLogWork(logWorkTable.getSelectedRow());
        });
    }

    private void addEstimateChangeListener() {
        estimateSpinner.addChangeListener(e -> {
            if (Float.compare(task.getEstimate(), ((Double) estimateSpinner.getValue()).floatValue()) == 0) {
                changeEstimatePane.setVisible(false);
            } else {
                changeEstimatePane.setVisible(true);
            }
        });
    }

    private void addStatusChangeListener() {
        statusCmbx.addActionListener(e -> {
            if (TaskStatus.WAITING_FOR_APPROVE == statusFromCmbx(statusCmbx)) {
                changeStatusPane.setVisible(true);
            } else {
                changeStatusPane.setVisible(false);
            }
        });
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        if (changeEstimatePane.isVisible() && StringUtils.isEmpty(changeEstimateArea.getText())) {
            return Optional.of(new ValidationInfo("Необходимо указать причину изменения оценки!",
                    changeEstimateArea));
        }

        if (changeStatusPane.isVisible() && StringUtils.isEmpty(changeStatusArea.getText())) {
            return Optional.of(new ValidationInfo("Необходимо указать, что было сделано и как протестировать задачу!",
                    changeStatusArea));
        }

        return Optional.empty();
    }

    @Override
    public Task getData() {
        task.getLogWorks().clear();

        task.getLogWorks().addAll(logWorkModel.getLogWorks());

        if (StringUtils.isNotBlank(changeEstimateArea.getText())) {
            task.setEstimate(((Double) estimateSpinner.getValue()).floatValue());
            task.getComments().add(new TaskComment(changeEstimateArea.getText()));
        }

        if (StringUtils.isNotBlank(changeStatusArea.getText())) {
            task.getComments().add(new TaskComment(changeStatusArea.getText()));
        }
        task.updateStatus(statusFromCmbx(statusCmbx));

        return task;
    }

    @SuppressWarnings("all")
    private TaskStatus statusFromCmbx(JComboBox<String> cmbx) {
        return RedmineFilter.getEnumItem(TaskStatus.values(), (String) cmbx.getSelectedItem()).get();
    }
}