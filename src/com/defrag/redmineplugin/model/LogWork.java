package com.defrag.redmineplugin.model;

import lombok.*;

import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * Created by defrag on 13.08.17.
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class LogWork {

    public enum Type {
        DEVELOPMENT(9),
        ANALYSIS(11),
        QA(13);

        @Getter
        private final int activityId;

        Type(int activityId) {
            this.activityId = activityId;
        }

        public static Type typeByActivity(int activityId) {
            return Stream.of(values())
                    .filter(t -> t.activityId == activityId)
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Type for activity id %d was not found", activityId)));
        }
    }

    @Setter
    private Integer id;

    @NonNull
    private LocalDate date;

    @NonNull
    private Type type;

    @NonNull
    private String description;

    @NonNull
    private Float time;
}