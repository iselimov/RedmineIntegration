package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.Report;
import com.defrag.redmineplugin.model.ReportInfo;
import com.defrag.redmineplugin.service.util.PropertiesLoader;
import com.defrag.redmineplugin.service.util.ViewLogger;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by defrag on 14.09.17.
 */
@Slf4j
public class ReportManager {

    private final RedmineManager redmineManager;

    private final ViewLogger viewLogger;

    private final Properties reportProperties;

    public ReportManager(ConnectionInfo connectionInfo, ViewLogger viewLogger) {
        redmineManager = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(), connectionInfo.getApiAccessKey());
        this.viewLogger = viewLogger;
        reportProperties = PropertiesLoader.load(this.getClass().getClassLoader(), "report.properties");
    }

    public void sendReport(Report report) {
        List<TimeEntry> logWorks = findLogWorksOnDate(report.getDateFrom(), report.getDateNow());
        if (logWorks.isEmpty()) {
            viewLogger.info("На дату '%s' не было найдено ни одной отметки времени по задачам", report.getDateNow());
            return;
        }
        viewLogger.info("Количество отметок времени на дату '%s': '%d'", report.getDateNow(), logWorks.size());
        Optional<String> htmlReport = report.generateHtmlReport(reportProperties, logWorks);
        if (!htmlReport.isPresent()) {
            return;
        }
        doSendReport(htmlReport, report.getReportInfo(), report.getDateFrom(), report.getDateNow());
    }

    @SuppressWarnings("all")
    private void doSendReport(Optional<String> htmlReport,
                              ReportInfo reportInfo,
                              Optional<LocalDate> dateFrom,
                              LocalDate dateNow) {
        Properties props = new Properties();
        props.put("mail.smtp.host", reportProperties.getProperty("mail.smtp.host"));
        props.put("mail.debug", reportProperties.getProperty("mail.debug"));
        Session session = Session.getDefaultInstance(props);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(reportInfo.getEmailFrom()));

            InternetAddress[] emailToAddresses = new InternetAddress[reportInfo.getEmailsTo().length];
            for (int i = 0; i < reportInfo.getEmailsTo().length; i++) {
                emailToAddresses[i] = new InternetAddress(reportInfo.getEmailsTo()[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, emailToAddresses);

            String subject = reportProperties.getProperty("report.subject");
            String reportDateText;
            if (dateFrom.isPresent()) {
                reportDateText = String.format("%d-%s", dateFrom.get().getDayOfMonth(),
                        dateNow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                reportDateText = dateNow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
            msg.setSubject(String.format(subject, reportDateText));
            msg.setSentDate(new Date());
            msg.setContent(htmlReport.get(), "text/html; charset=utf-8");

            Transport.send(msg);
            viewLogger.info("Отчет успешно отправлен");
        } catch (MessagingException mex) {
            log.error("Error while sending report");
            viewLogger.error("Произошла ошибка при отправке отчета");
        }
    }

    private List<TimeEntry> findLogWorksOnDate(Optional<LocalDate> dateFrom, LocalDate dateNow) {
        Map<String, String> params = new HashMap<>();
        params.put(reportProperties.getProperty("report.user.filter"), "me");
        if (dateFrom.isPresent()) {
            params.put(reportProperties.getProperty("report.date.filter"), String.format("><%s|%s", dateFrom.get(),
                    dateNow.toString()));
        } else {
            params.put(reportProperties.getProperty("report.date.filter"), dateNow.toString());
        }
        try {
            return redmineManager.getTimeEntryManager().getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            viewLogger.error("Произошла ошибка при генерации отчета");
            log.error("Error while getting time entries");
            return new ArrayList<>();
        }
    }
}