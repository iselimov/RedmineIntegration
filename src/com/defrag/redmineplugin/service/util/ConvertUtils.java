package com.defrag.redmineplugin.service.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by defrag on 24.09.17.
 */
public class ConvertUtils {

    private ConvertUtils() {
    }

    public static LocalDate toLocalDate(Date date) {
        return LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd")
                .format(date), DateTimeFormatter.ISO_DATE);
    }
}