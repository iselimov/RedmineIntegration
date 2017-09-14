package com.defrag.redmineplugin.model;

import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.Builder;
import lombok.Getter;

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

    private String comments;

    public Optional<String> generateHtmlReport(Properties reportProperties, List<TimeEntry> timeEntries) {
        if (timeEntries.isEmpty()) {
            return Optional.empty();
        }

        Map<Integer, List<TimeEntry>> groupedByIdEntries = timeEntries
                .stream()
                .collect(Collectors.groupingBy(TimeEntry::getIssueId));

        return Optional.of(doGenerate(reportProperties, groupedByIdEntries));
    }

    private String doGenerate(Properties reportProperties, Map<Integer, List<TimeEntry>> groupedByIdEntries) {
        StringBuilder builder = new StringBuilder();

        builder.append(reportProperties.getProperty("report.header"));

        for (Map.Entry<Integer, List<TimeEntry>> entry : groupedByIdEntries.entrySet()) {
            String taskHeaderPattern = reportProperties.getProperty("report.task.header");
            builder.append(String.format(taskHeaderPattern, entry.getKey()));

            String timeEntryHeaderPattern = reportProperties.getProperty("report.time.entry.header");
            List<TimeEntry> entries = entry.getValue();
            for (int i = 0; i < entries.size(); i ++) {
                builder.append(String.format(timeEntryHeaderPattern, i + 1, entries.get(i).getComment()));
            }
        }

        builder.append(comments);

        String footerPattern = reportProperties.getProperty("report.footer");
        builder.append(String.format(footerPattern, reportInfo.getFullName(), reportInfo.getPosition(), reportInfo.getPhone(),
                reportInfo.getDomainName(), reportInfo.getDomainName(), reportInfo.getSkype(),reportProperties.getProperty("report.image")));

        return builder.toString();
    }
}