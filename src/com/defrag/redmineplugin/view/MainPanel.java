package com.defrag.redmineplugin.view;

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

    public MainPanel(Project proj) {
        super(true);

        JBTable table = new JBTable(new TasksTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setStriped(true);
        table.setExpandableItemsEnabled(false);

        setUpColumnWidths(table);

        JScrollPane spTable = ScrollPaneFactory.createScrollPane(table);
        final SimpleTreeStructure reviewTreeStructure = createTreeStructure();
        final DefaultTreeModel model = new StatusTreeModel();
        final SimpleTree reviewTree = new SimpleTree(model);

        new AbstractTreeBuilder(reviewTree, model, reviewTreeStructure, null);
        reviewTree.invalidate();

        final JBSplitter splitter = new JBSplitter(false, 0.2f);
        final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(reviewTree);
        splitter.setFirstComponent(scrollPane);
        JBSplitter splitter2 = new JBSplitter(true, 0.1f);

        JPanel panel = new JPanel();

        ImageIcon icon = new ImageIcon(ResourceUtil.getResource(this.getClass().getClassLoader(), "", "refresh.png"));
        JButton label = new JButton();
        label.setBorderPainted(false);
        label.setIcon(icon);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setToolTipText("Refresh from Redmine");
        label.addActionListener(e -> new SettingsFormWrapper(proj, new SettingsForm()).show());

        ImageIcon icon2 = new ImageIcon(ResourceUtil.getResource(this.getClass().getClassLoader(), "", "menu-saveall.png"));
        JButton label2 = new JButton();
        label2.setBorderPainted(false);
        label2.setIcon(icon2);
        label2.setHorizontalAlignment(SwingConstants.LEFT);
        label2.setToolTipText("Save to Redmine");


        ImageIcon icon3 = new ImageIcon(ResourceUtil.getResource(this.getClass().getClassLoader(), "", "mail.png"));
        JButton label3 = new JButton();
        label3.setBorderPainted(false);
        label3.setIcon(icon3);
        label3.setHorizontalAlignment(SwingConstants.LEFT);
        label3.setToolTipText("Send report to mail");

        ImageIcon icon4 = new ImageIcon(ResourceUtil.getResource(this.getClass().getClassLoader(), "", "add.png"));
        JButton label4 = new JButton();
        label4.setBorderPainted(false);
        label4.setIcon(icon4);
        label4.setHorizontalAlignment(SwingConstants.LEFT);
        label4.setToolTipText("Add log work");

        panel.add(label);
        panel.add(label2);
        panel.add(label4);
        panel.add(label3);

        splitter2.setFirstComponent(panel);
        splitter2.setSecondComponent(spTable);
        splitter2.setResizeEnabled(false);

        splitter.setSecondComponent(splitter2);
        splitter.setResizeEnabled(false);
        setContent(splitter);
    }

    class SettingsFormWrapper extends ValidatedFormWrapper {

        public SettingsFormWrapper(@Nullable Project project, ValidatedDialog settingsForm) {
            super(project, settingsForm);
        }

        @Override
        protected String getTitleName() {
            return "Настройки Подлючения К Redmine";
        }
    }

    private SimpleTreeStructure createTreeStructure() {
        StatusRootNode root = new StatusRootNode();
        return new StatusTreeStructure(root);
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
