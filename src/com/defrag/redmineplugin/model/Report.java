package com.defrag.redmineplugin.model;

import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by defrag on 14.09.17.
 */
@Builder
@Getter
public class Report {

    private ReportInfo reportInfo;

    private LocalDate date;

    private String tomorrow;

    private String questions;

    public Optional<String> generateHtmlReport(Properties reportProperties, List<TimeEntry> timeEntries) {
        Map<Integer, List<TimeEntry>> groupedByIdEntries = timeEntries
                .stream()
                .collect(Collectors.groupingBy(TimeEntry::getIssueId));

        return Optional.of(doGenerate(reportProperties, groupedByIdEntries));
    }

    private String doGenerate(Properties reportProperties, Map<Integer, List<TimeEntry>> groupedByIdEntries) {
        StringBuilder builder = new StringBuilder();

        builder.append(reportProperties.getProperty("report.header"));
        String taskHeaderPattern = reportProperties.getProperty("report.task.header");
        String timeEntriesHeaderPattern = reportProperties.getProperty("report.time.entry.header.list");
        String timeEntryHeaderPattern = reportProperties.getProperty("report.time.entry.header");

        for (Map.Entry<Integer, List<TimeEntry>> entry : groupedByIdEntries.entrySet()) {

            builder.append(String.format(taskHeaderPattern, entry.getKey()));

            List<TimeEntry> entries = entry.getValue();
            if (entries.size() > 1) {
                for (int i = 0; i < entries.size(); i ++) {
                    builder.append(String.format(timeEntriesHeaderPattern, i + 1, entries.get(i).getComment()));
                }
            } else if (entries.size() == 1) {
                builder.append(String.format(timeEntryHeaderPattern, entries.get(0).getComment()));
            }
        }

        String tomorrowPattern = reportProperties.getProperty("report.tomorrow");
        builder.append(String.format(tomorrowPattern, tomorrow));

        String questionsPattern = reportProperties.getProperty("report.questions");
        builder.append(String.format(questionsPattern, questions));

        String footerPattern = reportProperties.getProperty("report.footer");
        builder.append(String.format(footerPattern, reportInfo.getFullName(), reportInfo.getPosition(), reportInfo.getPhone(),
                reportInfo.getDomainName(), reportInfo.getDomainName(), reportInfo.getSkype(),reportProperties.getProperty("report.image")));

        return builder.toString();
    }
}