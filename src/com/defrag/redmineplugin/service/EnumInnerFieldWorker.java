package com.defrag.redmineplugin.service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by defrag on 18.08.17.
 */
public interface EnumInnerFieldWorker {

    String getValue();

    static Optional<EnumInnerFieldWorker> getEnumItem(EnumInnerFieldWorker[] values, String fieldValue) {
        return Arrays.stream(values)
                .filter(enm -> enm.getValue().equalsIgnoreCase(fieldValue))
                .findAny();
    }
}