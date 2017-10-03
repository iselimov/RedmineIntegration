package com.defrag.redmineplugin.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by defrag on 23.09.17.
 */
@RequiredArgsConstructor
@Getter
public class TaskComment {

    @NonNull
    private String text;

    @NonNull
    private boolean isParentComment;
}