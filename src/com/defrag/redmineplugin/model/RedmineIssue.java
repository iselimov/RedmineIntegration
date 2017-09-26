package com.defrag.redmineplugin.model;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by defrag on 07.09.17.
 */
@RequiredArgsConstructor
@Getter
public class RedmineIssue {

    @NonNull
    private Issue issue;

    private List<TimeEntry> timeEntries = new ArrayList<>();
}