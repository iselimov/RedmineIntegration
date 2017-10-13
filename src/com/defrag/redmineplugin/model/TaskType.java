package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.RedmineFilter;
import lombok.Getter;
import org.apache.http.message.BasicNameValuePair;

import java.util.Arrays;
import java.util.List;

/**
 * Created by defrag on 17.08.17.
 */
public enum TaskType implements RedmineFilter {

    USER_STORY("User Story", 5),
    TASK("Task", 4),
    BUG("Bug", 1),
    USER_FEEDBACK("User feedback", 8),
    USER_BUG("User bug", 9);

    @Getter
    private final String name;

    @Getter
    private final int paramId;

    TaskType(String name, int paramId) {
        this.name = name;
        this.paramId = paramId;
    }

    @Override
    public List<BasicNameValuePair> getCustomFilters() {
        return Arrays.asList(
                new BasicNameValuePair("f[]", "tracker_id"),
                new BasicNameValuePair("op[tracker_id]", "="),
                new BasicNameValuePair("v[tracker_id][]", String.valueOf(paramId))
        );
    }
}