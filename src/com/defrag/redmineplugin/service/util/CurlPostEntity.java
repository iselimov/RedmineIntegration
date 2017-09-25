package com.defrag.redmineplugin.service.util;

import com.defrag.redmineplugin.model.ConnectionInfo;
import lombok.Getter;

/**
 * Created by defrag on 24.09.17.
 */
@Getter
public class CurlPostEntity<T> extends CurlEntity {

    private T queryParamValue;

    public CurlPostEntity(ConnectionInfo connectionInfo, Integer taskId, T queryParamValue) {
        super(connectionInfo, taskId);
        this.queryParamValue = queryParamValue;
    }
}

