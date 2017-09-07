package com.defrag.redmineplugin.model;

import com.defrag.redmineplugin.service.RedmineFilter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by defrag on 13.08.17.
 */
@Getter
@RequiredArgsConstructor
public class Task {

    @Setter
    private Integer id;

    @NonNull
    private RedmineFilter type;

    @NonNull
    private RedmineFilter status;

    @NonNull
    private String author;

    @NonNull
    private String subject;

    @Setter
    private String description;

    @Setter
    private Float estimate;

    @Setter
    private Float remaining;

    @Setter
    private Task parent;

    private List<LogWork> logWorks = new ArrayList<>();

    public void updateStatus(RedmineFilter status) {
        this.status = status;
    }

    public Task createChildTask() {
        Task child = new Task(type, status, author, subject);

        child.description = description;
        child.estimate = estimate;
        child.remaining = remaining;
        child.parent = this;

        return child;
    }
}