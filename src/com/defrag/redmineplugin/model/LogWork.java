package com.defrag.redmineplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by defrag on 13.08.17.
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class LogWork {

    @Setter
    private Integer id;

    @NonNull
    private LocalDate date;

    @NonNull
    private String description;
}