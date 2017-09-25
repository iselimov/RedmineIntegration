package com.defrag.redmineplugin.service.util;

import com.defrag.redmineplugin.model.ConnectionInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Properties;

/**
 * Created by defrag on 24.09.17.
 */
@RequiredArgsConstructor
@Getter
public abstract class CurlEntity {

    static final Properties curlProperties;

    static {
        curlProperties = PropertiesLoader.load(CurlEntity.class.getClassLoader(), "curl.properties");
    }

    enum RequestType {
        GET("curl.get"),
        POST("curl.post");

        @Getter
        private final String curlProperty;

        RequestType(String curlProperty) {
            this.curlProperty = curlProperty;
        }
    }

    @NonNull
    final ConnectionInfo connectionInfo;

    @NonNull
    final Integer taskId;

    @NonNull
    final RequestType requestType;

    public String getCommand() {
        return requestType.getCurlProperty();
    }
}