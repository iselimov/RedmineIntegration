package com.defrag.redmineplugin.service.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by defrag on 24.09.17.
 */
@Slf4j
public class PropertiesLoader {

    public static Properties load(ClassLoader classLoader, String propsName) {
        Properties props = new Properties();

        InputStreamReader resourceReader;
        try {
            resourceReader = new InputStreamReader(classLoader.getResourceAsStream(propsName),
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding error while reading properties");
            return props;
        }

        try {
            props.load(resourceReader);
        } catch (IOException e) {
            log.error("Error while properties");
        }

        return props;
    }
}