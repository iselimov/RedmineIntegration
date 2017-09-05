package com.defrag.redmineplugin.view;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.service.TaskManager;
import com.defrag.redmineplugin.view.form.SettingsForm;
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

    public MainPanel(Project project) {
        super(false);
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
        JPanel settingsPanel = createSettingsPanel(project);
        JScrollPane spTable = createTaskTable(project);
        settingsSplitter.setFirstComponent(settingsPanel);
        settingsSplitter.setSecondComponent(spTable);
        settingsSplitter.setResizeEnabled(false);

        mainSplitter.setSecondComponent(settingsSplitter);

        setContent(mainSplitter);
    }

    private JScrollPane createTaskTable(Project project) {
        taskModel = new TasksTableModel(project);
        JBTable taskTable = new JBTable(taskModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setStriped(true);
        taskTable.setExpandableItemsEnabled(false);

        setUpColumnWidths(taskTable);

        return ScrollPaneFactory.createScrollPane(taskTable);
    }

    @NotNull
    private JPanel createSettingsPanel(Project project) {
        JButton settingsBut = new JButton(getIcon("settings.png"));
        settingsBut.setBorderPainted(false);
        settingsBut.setHorizontalAlignment(SwingConstants.LEFT);
        settingsBut.setToolTipText("Plugin Settings");
        settingsBut.addActionListener(e -> new SettingsFormWrapper(project, new SettingsForm()).show());

        JButton mailBut = new JButton(getIcon("mail.png"));
        mailBut.setBorderPainted(false);
        mailBut.setHorizontalAlignment(SwingConstants.LEFT);
        mailBut.setToolTipText("Send report to mail");

        JButton addTaskBut = new JButton(getIcon("add.png"));
        addTaskBut.setBorderPainted(false);
        addTaskBut.setHorizontalAlignment(SwingConstants.LEFT);
        addTaskBut.setToolTipText("Add log work");

        JPanel settingsPanel = new JPanel();
        settingsPanel.add(settingsBut);
        settingsPanel.add(addTaskBut);
        settingsPanel.add(mailBut);
        return settingsPanel;
    }

    class SettingsFormWrapper extends ValidatedFormWrapper {

        public SettingsFormWrapper(@Nullable Project project, ValidatedDialog settingsForm) {
            super(project, settingsForm);
        }

        @Override
        protected String getTitleName() {
            return "Настройки подключения к Redmine";
        }

        @Override
        protected void doOKAction() {
            ConnectionInfo connection = ((SettingsForm) validatedDialog).prepareConnectionInfo();
            TaskManager taskManager = new TaskManager(connection);
            rootNode.setTaskManager(taskManager);
            rootNode.setTaskModel(taskModel);

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
        table.setRowHeight(50);
        table.getColumnModel().getColumn(0).setResizable(false);
        table.getColumnModel().getColumn(1).setResizable(false);
        table.getColumnModel().getColumn(2).setResizable(false);
        table.getColumnModel().getColumn(3).setResizable(false);
        table.getColumnModel().getColumn(4).setResizable(false);

        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setMinWidth(150);
        table.getColumnModel().getColumn(2).setMinWidth(650);
        table.getColumnModel().getColumn(3).setMinWidth(80);
        table.getColumnModel().getColumn(4).setMinWidth(80);
    }
}