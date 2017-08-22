package com.defrag.redmineplugin.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

/**
 * Created by defrag on 13.08.17.
 */
@Getter
@RequiredArgsConstructor
public class LogWork {

    @NonNull
    private LocalDate date;

    @NonNull
    private String description;
}