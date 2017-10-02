package com.defrag.redmineplugin.service.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by defrag on 26.09.17.
 */
public class ViewLogger {

    private static final int DELTA_Y = 50;

    private final Project project;

    private AtomicInteger incrementY = new AtomicInteger(10);

    public ViewLogger(Project project) {
        this.project = project;
    }

    public void info(String message, Object ... args) {
        logView(message, MessageType.INFO, args);
    }

    public void warning(String message, Object ... args) {
        logView(message, MessageType.WARNING, args);
    }

    public void error(String message, Object ... args) {
        logView(message, MessageType.ERROR, args);
    }

    private void logView(String message, MessageType messageType, Object ... args) {
        JFrame frame = WindowManager.getInstance().getFrame(project.isDefault() ? null : project);
        if (frame == null) {
            return;
        }

        JComponent component = frame.getRootPane();
        if (component == null){
            return;
        }

        Rectangle rect = component.getVisibleRect();
        final RelativePoint toolTipPoint = new RelativePoint(component, new Point(rect.x + rect.width - 10,
                rect.height - incrementY.getAndAdd(DELTA_Y)));

        final BalloonBuilder toolTipBuilder = JBPopupFactory.getInstance().
                createHtmlTextBalloonBuilder(String.format(message, args), messageType, null);

        Balloon balloon = toolTipBuilder
                .setShowCallout(false)
                .setCloseButtonEnabled(true)
                .createBalloon();

        balloon.addListener((new JBPopupListener.Adapter(){
            @Override
            public void onClosed(LightweightWindowEvent event) {
                incrementY.addAndGet(-DELTA_Y);
            }
        }));

        balloon.show(toolTipPoint, Balloon.Position.atLeft);
    }
}