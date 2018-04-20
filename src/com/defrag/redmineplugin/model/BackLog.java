package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.RedmineFilter;
import lombok.Getter;
import org.apache.http.message.BasicNameValuePair;

import java.util.Arrays;
import java.util.List;

public enum BackLog implements RedmineFilter {

    ALL_TASKS("Not resolved tasks", "any");

    @Getter
    private final String name;

    @Getter
    private final String paramId;

    BackLog(String name, String paramId) {
        this.name = name;
        this.paramId = paramId;
    }

    @Override
    public List<BasicNameValuePair> getCustomFilters() {
        return Arrays.asList(
                new BasicNameValuePair( "f[]", "backlogs_issue_type"),
                new BasicNameValuePair("op[backlogs_issue_type]", "="),
                new BasicNameValuePair("v[backlogs_issue_type][]", paramId),
                new BasicNameValuePair( "f[]", "status_id"),
                new BasicNameValuePair("op[status_id]", "="),
                new BasicNameValuePair("v[status_id][]", TaskStatus.NEW.getParamId()),
                new BasicNameValuePair("v[status_id][]", TaskStatus.IN_PROGRESS.getParamId()),
                new BasicNameValuePair("v[status_id][]", TaskStatus.WAITING_FOR_APPROVE.getParamId())
        );
    }
}