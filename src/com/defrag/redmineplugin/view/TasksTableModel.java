package com.defrag.redmineplugin.view;

import com.intellij.openapi.project.Project;

import javax.swing.table.DefaultTableModel;

/**
 * Created by defrag on 17.07.17.
 */
public class TasksTableModel extends DefaultTableModel {

    private Project myProject;

    public TasksTableModel() {
    }

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
}