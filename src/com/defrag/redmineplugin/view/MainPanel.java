package com.defrag.redmineplugin.view;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.Task;
import com.defrag.redmineplugin.service.TaskManager;
import com.defrag.redmineplugin.view.form.SettingsForm;
import com.defrag.redmineplugin.view.form.TaskForm;
import com.defrag.redmineplugin.view.tree.MainRootNode;
import com.defrag.redmineplugin.view.tree.StatusTreeModel;
import com.defrag.redmineplugin.view.tree.StatusTreeStructure;
import com.defrag.redmineplugin.view.tree.TaskManagerConsumer;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

/**
 * Created by defrag on 06.07.17.
 */
public class MainPanel extends SimpleToolWindowPanel {

    private final Project project;

    private TaskManagerConsumer rootNode;

    private TasksTableModel taskModel;

    private JBTable taskTable;

    private ConnectionInfo connectionInfo;

    public MainPanel(Project project) {
        super(true);
        this.project = project;

        final DefaultTreeModel model = new StatusTreeModel();
        final SimpleTree reviewTree = new SimpleTree(model);

        final SimpleTreeStructure reviewTreeStructure = createTreeStructure();
        new AbstractTreeBuilder(reviewTree, model, reviewTreeStructure, null);
        reviewTree.invalidate();

        final JBSplitter mainSplitter = new JBSplitter(false, 0.2f);
        final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(reviewTree);
        mainSplitter.setFirstComponent(scrollPane);
        mainSplitter.setResizeEnabled(false);

        JBSplitter settingsSplitter = new JBSplitter(true, 0.1f);
        JComponent settingsToolbar = createSettingsToolbar(project);
        JComponent spTable = createTaskTable(project);
        settingsSplitter.setFirstComponent(settingsToolbar);
        settingsSplitter.setSecondComponent(spTable);
        settingsSplitter.setResizeEnabled(false);

        mainSplitter.setSecondComponent(settingsSplitter);

        setContent(mainSplitter);
    }

    private JComponent createTaskTable(Project project) {
        taskModel = new TasksTableModel(project);
        taskTable = new JBTable(taskModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setStriped(true);
        taskTable.setExpandableItemsEnabled(false);

        setUpColumnWidths(taskTable);

        return ScrollPaneFactory.createScrollPane(taskTable);
    }

    @NotNull
    private JComponent createSettingsToolbar(Project project) {
        JButton settingsBut = new JButton(getIcon("settings.png"));
        settingsBut.setFocusable(true);
        settingsBut.setBorderPainted(true);
        settingsBut.setHorizontalAlignment(SwingConstants.LEFT);
        settingsBut.setToolTipText("Plugin Settings");
        settingsBut.addActionListener(e -> {
            SettingsForm settingsForm = new SettingsForm(connectionInfo);
            new SettingsFormWrapper(project, settingsForm).show();
        });

        JButton editTaskBut = new JButton(getIcon("edit.png"));
        editTaskBut.setFocusable(true);
        editTaskBut.setBorderPainted(true);
        editTaskBut.setHorizontalAlignment(SwingConstants.LEFT);
        editTaskBut.setToolTipText("Edit task");
        editTaskBut.addActionListener(e -> {
            taskModel.getTask(taskTable.getSelectedRow())
                    .ifPresent(task -> {
                        TaskForm taskForm = new TaskForm(project, task);
                        new TaskFormWrapper(project, taskForm).show();
                    });
        });

        JButton addSubTaskBut = new JButton(getIcon("copy.png"));
        addSubTaskBut.setFocusable(true);
        addSubTaskBut.setBorderPainted(true);
        addSubTaskBut.setHorizontalAlignment(SwingConstants.LEFT);
        addSubTaskBut.setToolTipText("Create subtask (only for User story)");

        JButton mailBut = new JButton(getIcon("mail.png"));
        mailBut.setFocusable(true);
        mailBut.setBorderPainted(true);
        mailBut.setHorizontalAlignment(SwingConstants.LEFT);
        mailBut.setToolTipText("Send report to mail");

        JToolBar settingsToolBar = new JToolBar();
        settingsToolBar.setBorderPainted(true);
        settingsToolBar.setFocusable(true);
        settingsToolBar.setFloatable(true);
        settingsToolBar.setOpaque(true);
        settingsToolBar.setRequestFocusEnabled(true);
        settingsToolBar.add(settingsBut);
        settingsToolBar.add(editTaskBut);
        settingsToolBar.add(addSubTaskBut);
        settingsToolBar.add(mailBut);

        return settingsToolBar;
    }

    class SettingsFormWrapper extends ValidatedFormWrapper<ConnectionInfo> {

        SettingsFormWrapper(@Nullable Project project, ValidatedDialog<ConnectionInfo> settingsForm) {
            super(project, settingsForm);
        }

        @Override
        protected String getTitleName() {
            return "Настройки подключения к Redmine";
        }

        @Override
        protected void doOKAction() {
            connectionInfo = validatedDialog.getData();
            TaskManager taskManager = new TaskManager(connectionInfo);
            rootNode.setTaskManager(taskManager);
            rootNode.setTaskModel(taskModel);

            super.doOKAction();
        }
    }

    class TaskFormWrapper extends ValidatedFormWrapper<Task> {

        TaskFormWrapper(@Nullable Project project, ValidatedDialog<Task> taskForm) {
            super(project, taskForm);
        }

        @Override
        protected String getTitleName() {
            return "Редактирование задачи";
        }

        @Override
        protected void doOKAction() {

            super.doOKAction();
        }
    }

    private SimpleTreeStructure createTreeStructure() {
        rootNode = new MainRootNode();
        return new StatusTreeStructure(rootNode);
    }

    private Icon getIcon(String iconName) {
        return new ImageIcon(ResourceUtil.getResource(this.getClass().getClassLoader(), "", iconName));
    }

    private static void setUpColumnWidths(@NotNull final JBTable table) {
        table.setRowHeight(30);

        table.getColumnModel().getColumn(0).setResizable(false);
        table.getColumnModel().getColumn(1).setResizable(false);
        table.getColumnModel().getColumn(2).setResizable(false);
        table.getColumnModel().getColumn(3).setResizable(false);
        table.getColumnModel().getColumn(4).setResizable(false);
        table.getColumnModel().getColumn(5).setResizable(false);
        table.getColumnModel().getColumn(6).setResizable(false);

        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setMaxWidth(120);
        table.getColumnModel().getColumn(2).setMaxWidth(150);
        table.getColumnModel().getColumn(3).setMaxWidth(150);
        table.getColumnModel().getColumn(4).setMaxWidth(800);
        table.getColumnModel().getColumn(5).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(80);
    }
}