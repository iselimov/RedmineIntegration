package com.defrag.redmineplugin.view.form.model;

import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by defrag on 17.07.17.
 */
@Slf4j
public class TaskTableModel extends DefaultTableModel {

    private final Project myProject;

    private final ViewLogger viewLogger;

    private Map<Integer, Task> tasks = new HashMap<>();

    public TaskTableModel(Project project, ViewLogger viewLogger) {
        myProject = project;
        this.viewLogger = viewLogger;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 5 || columnIndex == 6)
            return Float.class;
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Id";
            case 1:
                return "Type";
            case 2:
                return "Status";
            case 3:
                return "Author";
            case 4:
                return "Subject";
            case 5:
                return "Estimate";
            case 6:
                return "Remaining";
        }
        return super.getColumnName(column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Optional<Task> getTask(int rowIndex) {
        if (rowIndex == -1) {
            viewLogger.warning("Не выбрана ни одна из задач в списке");
            return Optional.empty();
        }

        Vector row = (Vector) getDataVector().get(rowIndex);
        Integer id = (Integer) row.get(0);

        return Optional.ofNullable(tasks.get(id));
    }

    public void updateTask(int rowIndex, Task updated) {
        setValueAt(updated.getStatus().getName(), rowIndex, 2);
        setValueAt(updated.getEstimate(), rowIndex, 5);
        setValueAt(updated.getRemaining(), rowIndex, 6);

        viewLogger.info("Обновление задачи произошло успешно");
    }

    public void updateModel(List<Task> tasks) {
        log.info("Called update model with tasks size {}", tasks.size());
        setRowCount(0);
        this.tasks.clear();

        tasks.forEach(task -> addRow(new Object[] {
                task.getId(),
                task.getType().getName(),
                task.getStatus().getName(),
                task.getAuthor(),
                task.getSubject(),
                task.getEstimate(),
                task.getRemaining()
        }));

        this.tasks.putAll(tasks
                .stream()
                .collect(Collectors.toMap(Task::getId, t -> t)));

        viewLogger.info("Количество задач в плагине: '%d'", this.tasks.size());
    }
}