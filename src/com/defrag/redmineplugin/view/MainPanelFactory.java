package com.defrag.redmineplugin.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by defrag on 06.07.17.
 */
public class MainPanelFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        final MainPanel mainPanel = new MainPanel(project);
        final Content content = ContentFactory.SERVICE.getInstance().
                createContent(mainPanel, null, false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
    }
}