package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.EnumInnerFieldWorker;
import lombok.Getter;

/**
 * Created by defrag on 23.07.17.
 */
@Getter
public enum TaskStatus implements EnumInnerFieldWorker {

    NEW("New"),
    IN_PROGRESS("In Progress"),
    WAITING_FOR_APPROVE("Waiting for approve"),
    RESOLVED("Resolved");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }
}