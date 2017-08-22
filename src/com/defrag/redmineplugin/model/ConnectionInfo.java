package com.defrag.redmineplugin.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * Created by defrag on 18.08.17.
 */
@RequiredArgsConstructor
@Getter
public class ConnectionInfo {

    @NonNull
    private String redmineUri;

    @NonNull
    private String apiAccessKey;

    @Setter
    private String cookie;

    @Setter
    private String csrfToken;

    public boolean hasExtendedProps() {
        return StringUtils.isNotBlank(cookie)
                && StringUtils.isNotBlank(csrfToken);
    }
}