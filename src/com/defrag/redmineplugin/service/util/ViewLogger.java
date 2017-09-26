package com.defrag.redmineplugin.service.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.*;

/**
 * Created by defrag on 26.09.17.
 */
public class ViewLogger {

    private final Project project;

    public ViewLogger(Project project) {
        this.project = project;
    }

    public void info(String message) {
        logView(message, MessageType.INFO);
    }

    public void warning(String message) {
        logView(message, MessageType.WARNING);
    }

    public void error(String message) {
        logView(message, MessageType.ERROR);
    }

    private void logView(String message, MessageType messageType) {
        JFrame frame = WindowManager.getInstance().getFrame(project.isDefault() ? null : project);
        if (frame == null) {
            return;
        }

        JComponent component = frame.getRootPane();
        if (component == null){
            return;
        }

        Rectangle rect = component.getVisibleRect();
        final RelativePoint toolTipPoint = new RelativePoint(component, new Point(rect.x + rect.width - 10, rect.y + 10));

        final BalloonBuilder toolTipBuilder = JBPopupFactory.getInstance().
                createHtmlTextBalloonBuilder(message, messageType, null);
        toolTipBuilder
                .setShowCallout(false)
                .setCloseButtonEnabled(true)
                .createBalloon()
                .show(toolTipPoint, Balloon.Position.atLeft);
    }
}