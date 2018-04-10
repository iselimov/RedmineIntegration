package com.defrag.redmineplugin.service.util.curl;

import com.defrag.redmineplugin.model.ConnectionInfo;

/**
 * Created by defrag on 24.09.17.
 */
public class RemainingHoursPostEntity extends CurlPostEntity<Float> {

    public RemainingHoursPostEntity(ConnectionInfo connectionInfo) {
        super(connectionInfo);
    }

    @Override
    String getCommand(int taskId, Float queryValue) {
        String remainingHoursTemplate = getRequestType();
        String updateTemplate = curlProperties.getProperty("curl.post.remaining.hours");
        return String.format(remainingHoursTemplate, connectionInfo.getCookie(), connectionInfo.getCsrfToken(),
                String.format(updateTemplate, queryValue), connectionInfo.getRedmineUri(), taskId);
    }
}