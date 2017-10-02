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
        List<TimeEntry> logWorks = findLogWorksOnDate(report.getDate());
        if (logWorks.isEmpty()) {
            viewLogger.info("На дату '%s' не было найдено ни одной отметки времени по задачам", report.getDate());
            return;
        }

        viewLogger.info("Количество отметок времени на дату '%s': '%d'", report.getDate(), logWorks.size());
        Optional<String> htmlReport = report.generateHtmlReport(reportProperties, logWorks);
        if (!htmlReport.isPresent()) {
            return;
        }

        doSendReport(htmlReport, report.getReportInfo(), report.getDate());
    }

    @SuppressWarnings("all")
    private void doSendReport(Optional<String> htmlReport, ReportInfo reportInfo, LocalDate reportDate) {
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
            msg.setSubject(String.format(subject, reportDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
            msg.setSentDate(new Date());
            msg.setContent(htmlReport.get(), "text/html; charset=utf-8");

            Transport.send(msg);
            viewLogger.info("Отчет успешно отправлен");
        } catch (MessagingException mex) {
            log.error("Error while sending report");
            viewLogger.error("Произошла ошибка при отправке отчета");
        }
    }

    private List<TimeEntry> findLogWorksOnDate(LocalDate reportDate) {
        Map<String, String> params = new HashMap<>();
        params.put(reportProperties.getProperty("report.user.filter"), "me");
        params.put(reportProperties.getProperty("report.date.filter"), reportDate.toString());

        try {
            return redmineManager.getTimeEntryManager().getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            viewLogger.error("Произошла ошибка при генерации отчета");
            log.error("Error while getting time entries");
            return new ArrayList<>();
        }
    }
}