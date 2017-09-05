package com.defrag.redmineplugin.view;

import com.defrag.redmineplugin.model.Task;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Created by defrag on 17.07.17.
 */
@Slf4j
public class TasksTableModel extends DefaultTableModel {

    private Project myProject;

    public TasksTableModel(Project project) {
        myProject = project;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 3 || columnIndex == 4)
            return Double.class;
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Id";
            case 1:
                return "Author";
            case 2:
                return "Subject";
            case 3:
                return "Estimate";
            case 4:
                return "Remaining";
        }
        return super.getColumnName(column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void updateModel(List<Task> tasks) {
        log.info("Called update model with tasks size {}", tasks.size());
        setRowCount(0);

        tasks.forEach(task -> addRow(new Object[] {
                task.getId(),
                task.getAuthor(),
                task.getSubject(),
                task.getEstimate(),
                task.getRemaining()
        }));
    }
}