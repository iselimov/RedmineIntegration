package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.RedmineFilter;
import lombok.Getter;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by defrag on 17.08.17.
 */
public enum TaskType implements RedmineFilter {

    USER_STORY("User Story", 5),
    TASK("Task", 4),
    BUG("Bug", 1);

    @Getter
    private final String name;

    private final int paramId;

    TaskType(String name, int paramId) {
        this.name = name;
        this.paramId = paramId;
    }

    @Override
    public BasicNameValuePair getCustomFilter() {
        return new BasicNameValuePair("v[tracker_id][]", String.valueOf(paramId));
    }
}