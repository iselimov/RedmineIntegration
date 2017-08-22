package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.EnumInnerFieldWorker;
import lombok.Getter;

/**
 * Created by defrag on 17.08.17.
 */
@Getter
public enum TaskType implements EnumInnerFieldWorker {

    USER_STORY("User Story"),
    TASK("Task"),
    BUG("Bug");

    private final String value;

    TaskType(String value) {
        this.value = value;
    }
}