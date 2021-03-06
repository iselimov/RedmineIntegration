package com.defrag.redmineplugin.model;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by defrag on 18.08.17.
 */
@State(
        name = "ConnectionInfo",
        storages = @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/redmine-plugin.xml", scheme = StorageScheme.DIRECTORY_BASED)
)
@Getter
@Setter
public class ConnectionInfo implements PersistentStateComponent<ConnectionInfo> {

    private String redmineUri;

    private String apiAccessKey;

    private String projectKey;

    private String cookie;

    private String csrfToken;

    public boolean hasKeyParams() {
        return StringUtils.isNotBlank(redmineUri)
                && StringUtils.isNotBlank(apiAccessKey);
    }

    public boolean hasExtendedProps() {
        return StringUtils.isNotBlank(cookie)
                && StringUtils.isNotBlank(csrfToken);
    }

    public String getURIForTask(Integer taskId) {
        return String.format("%s/issues/%d", redmineUri, taskId);
    }

    @Nullable
    @Override
    public ConnectionInfo getState() {
        return this;
    }

    @Override
    public void loadState(ConnectionInfo state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}