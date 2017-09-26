package com.defrag.redmineplugin.view.form.wrapper;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.view.ValidatedDialog;
import com.defrag.redmineplugin.view.ValidatedFormWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 07.09.17.
 */
public class SettingsFormWrapper extends ValidatedFormWrapper<ConnectionInfo> {

    public SettingsFormWrapper(@Nullable Project project, ValidatedDialog<ConnectionInfo> settingsForm) {
        super(project, settingsForm);
    }

    @Override
    protected String getTitleName() {
        return "Настройки подключения к Redmine";
    }
}