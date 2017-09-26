package com.defrag.redmineplugin.service.util.curl;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.service.util.PropertiesLoader;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * Created by defrag on 24.09.17.
 */
@RequiredArgsConstructor
@Getter
@Slf4j
abstract class CurlEntity {

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
    private final RequestType requestType;

    String getRequestType() {
        return curlProperties.getProperty(requestType.getCurlProperty());
    }
}