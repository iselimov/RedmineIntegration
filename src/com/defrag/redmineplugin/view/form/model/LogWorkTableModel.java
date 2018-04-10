package com.defrag.redmineplugin.view.form.model;

import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.Task;
import lombok.Getter;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created by defrag on 07.09.17.
 */
public class LogWorkTableModel extends DefaultTableModel {

    @Getter
    private List<LogWork> logWorks = new ArrayList<>();

    public LogWorkTableModel(Task task) {
        logWorks.addAll(task.getLogWorks());
        task.getLogWorks().forEach(lw -> addRow(new Object[]{
                lw.getDate(),
                lw.getType(),
                lw.getTime(),
                lw.getDescription()
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
        if (columnIndex == 2) {
            return Float.class;
        }
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Date";
            case 1:
                return "Type";
            case 2:
                return "Value";
            case 3:
                return "Description";
        }
        return super.getColumnName(column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public LogWork getLogWork(int rowIndex) {
        return logWorks.get(rowIndex);
    }

    @SuppressWarnings("unchecked")
    public void createLogWork(LogWork toCreate) {
        int size = getDataVector().size();
        getDataVector().add(new Vector<>(Arrays.asList(
                toCreate.getDate(),
                toCreate.getType(),
                toCreate.getTime(),
                toCreate.getDescription()
        )));
        fireTableRowsInserted(size, size);
        logWorks.add(toCreate);
    }

    @SuppressWarnings("unchecked")
    public void updateLogWork(int rowIndex, LogWork toUpdate) {
        Vector row = (Vector) getDataVector().get(rowIndex);
        row.set(0, toUpdate.getDate());
        row.set(1, toUpdate.getType());
        row.set(2, toUpdate.getTime());
        row.set(3, toUpdate.getDescription());
        fireTableRowsUpdated(rowIndex, rowIndex);
        LogWork old = logWorks.get(rowIndex);
        toUpdate.setId(old.getId());
        logWorks.set(rowIndex, toUpdate);
    }

    public void removeLogWork(int rowIndex) {
        removeRow(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
        logWorks.remove(rowIndex);
    }
}