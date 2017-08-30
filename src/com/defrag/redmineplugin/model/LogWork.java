package com.defrag.redmineplugin.model;

import lombok.*;

import java.time.LocalDate;

/**
 * Created by defrag on 13.08.17.
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class LogWork {

    public enum Type {
        DEVELOPMENT,
        ANALYSIS,
        QA
    }

    @Setter
    private Integer id;

    @NonNull
    private LocalDate date;

    @NonNull
    private Type type;

    @NonNull
    private String description;
}