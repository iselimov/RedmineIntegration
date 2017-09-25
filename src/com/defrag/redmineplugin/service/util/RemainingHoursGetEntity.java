package com.defrag.redmineplugin.service.util;

import com.defrag.redmineplugin.model.ConnectionInfo;

/**
 * Created by defrag on 24.09.17.
 */
public class RemainingHoursGetEntity extends CurlEntity  {

    private static final String propertyName = "curl.get.remaining.hours";

    public RemainingHoursGetEntity(ConnectionInfo connectionInfo, Integer taskId) {
        super(connectionInfo, taskId, RequestType.GET);
    }

    @Override
    public String getCommand() {
        String curlTemplate = super.getCommand();
        String remainingHoursTemplate = curlTemplate + " | " + curlProperties.getProperty(propertyName);

        return String.format(remainingHoursTemplate, connectionInfo.getRedmineUri(), taskId);
    }
}