package com.defrag.redmineplugin.service.util;

import com.defrag.redmineplugin.model.ConnectionInfo;

/**
 * Created by defrag on 24.09.17.
 */
public class RemainingHoursPostEntity extends CurlEntity {

    public RemainingHoursPostEntity(ConnectionInfo connectionInfo, Integer taskId) {
        super(connectionInfo, taskId, RequestType.POST);
    }

    @Override
    public String getCommand() {
        return super.getCommand();
    }
}
