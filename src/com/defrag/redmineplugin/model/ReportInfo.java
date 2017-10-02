package com.defrag.redmineplugin.model;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 14.09.17.
 */
@State(
        name = "ReportInfo",
        storages = @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/redmine-plugin.xml", scheme = StorageScheme.DIRECTORY_BASED)
)
@Getter
@Setter
public class ReportInfo implements PersistentStateComponent<ReportInfo> {

    private String fullName;

    private String position;

    private String phone;

    private String domainName;

    private String skype;

    private String emailFrom;

    private String[] emailsTo;

    @Nullable
    @Override
    public ReportInfo getState() {
        return this;
    }

    @Override
    public void loadState(ReportInfo state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}