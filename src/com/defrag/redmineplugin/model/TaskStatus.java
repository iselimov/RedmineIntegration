package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.RedmineFilter;
import lombok.Getter;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by defrag on 23.07.17.
 */
public enum TaskStatus implements RedmineFilter {

    NEW("New", 1),
    IN_PROGRESS("In Progress", 2),
    WAITING_FOR_APPROVE("Waiting for approve", 10),
    PAUSED("Paused", 8),
    RESOLVED("Resolved", 3),
    CLOSED("Closed", 5);

    @Getter
    private final String name;

    private final int paramId;

    TaskStatus(String name, int paramId) {
        this.name = name;
        this.paramId = paramId;
    }

    @Override
    public BasicNameValuePair getCustomFilter() {
        return new BasicNameValuePair("v[status_id][]", String.valueOf(paramId));
    }
}