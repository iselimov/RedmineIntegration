package com.defrag.redmineplugin.model;

import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.time.DayOfWeek;
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

    private Optional<LocalDate> dateFrom;

    private LocalDate dateNow;

    private String tomorrow;

    private String questions;

    public Optional<String> generateHtmlReport(Properties reportProperties, List<TimeEntry> timeEntries) {
        Map<Integer, List<TimeEntry>> groupedByIdEntries = timeEntries
                .stream()
                .collect(Collectors.groupingBy(TimeEntry::getIssueId));

        return Optional.of(doGenerate(reportProperties, groupedByIdEntries));
    }

    private String doGenerate(Properties reportProperties, Map<Integer, List<TimeEntry>> groupedByIdEntries) {
        StringBuilder reportBuilder = new StringBuilder();

        fillMainPart(reportProperties, groupedByIdEntries, reportBuilder);
        fillTomorrowPart(reportProperties, reportBuilder);
        fillQuestionsPart(reportProperties, reportBuilder);
        fillFooterPart(reportProperties, reportBuilder);

        return reportBuilder.toString();
    }

    private void fillMainPart(Properties reportProperties,
                              Map<Integer, List<TimeEntry>> groupedByIdEntries,
                              StringBuilder reportBuilder) {
        reportBuilder.append(reportProperties.getProperty("report.header"));
        String taskHeaderPattern = reportProperties.getProperty("report.task.header");
        String timeEntriesHeaderPattern = reportProperties.getProperty("report.time.entry.header.list");
        String timeEntryHeaderPattern = reportProperties.getProperty("report.time.entry.header");
        for (Map.Entry<Integer, List<TimeEntry>> entry : groupedByIdEntries.entrySet()) {
            reportBuilder.append(String.format(taskHeaderPattern, entry.getKey()));
            List<TimeEntry> entries = entry.getValue();
            if (entries.size() > 1) {
                for (int i = 0; i < entries.size(); i ++) {
                    reportBuilder.append(String.format(timeEntriesHeaderPattern, i + 1, entries.get(i).getComment()));
                }
            } else if (entries.size() == 1) {
                reportBuilder.append(String.format(timeEntryHeaderPattern, entries.get(0).getComment()));
            }
        }
    }

    private void fillTomorrowPart(Properties reportProperties, StringBuilder reportBuilder) {
        String tomorrowPattern;
        if (DayOfWeek.FRIDAY == dateNow.getDayOfWeek()) {
            tomorrowPattern = reportProperties.getProperty("report.tomorrow.friday");
        } else {
            tomorrowPattern = reportProperties.getProperty("report.tomorrow");
        }
        reportBuilder.append(String.format(tomorrowPattern, tomorrow));
    }

    private void fillQuestionsPart(Properties reportProperties, StringBuilder reportBuilder) {
        if (StringUtils.isNotBlank(questions)) {
            String questionsPattern = reportProperties.getProperty("report.questions");
            reportBuilder.append(String.format(questionsPattern, questions));
        }
    }

    private void fillFooterPart(Properties reportProperties, StringBuilder reportBuilder) {
        String footerPattern = reportProperties.getProperty("report.footer");
        reportBuilder.append(String.format(footerPattern,
                reportInfo.getFullName(),
                reportInfo.getPosition(),
                reportInfo.getPhone(),
                reportInfo.getDomainName(),
                reportInfo.getDomainName(),
                reportInfo.getSkype(),
                reportProperties.getProperty("report.image")));
    }
}