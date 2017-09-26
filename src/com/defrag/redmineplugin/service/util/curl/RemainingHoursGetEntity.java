package com.defrag.redmineplugin.service.util.curl;

import com.defrag.redmineplugin.model.ConnectionInfo;

/**
 * Created by defrag on 24.09.17.
 */
public class RemainingHoursGetEntity extends CurlGetEntity {


    public RemainingHoursGetEntity(ConnectionInfo connectionInfo) {
        super(connectionInfo);
    }

    @Override
    String getCommand(int taskId) {
        String remainingHoursTemplate = getRequestType() + " | " +
                curlProperties.getProperty("curl.get.remaining.hours");

        return String.format(remainingHoursTemplate, connectionInfo.getCookie(), connectionInfo.getCsrfToken(),
                connectionInfo.getRedmineUri(), taskId);
    }
}