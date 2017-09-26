package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.Report;
import com.defrag.redmineplugin.service.util.PropertiesLoader;
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

    private final Properties reportProperties;

    public ReportManager(ConnectionInfo connectionInfo) {
        redmineManager = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(), connectionInfo.getApiAccessKey());
        reportProperties = PropertiesLoader.load(this.getClass().getClassLoader(), "report.properties");
    }

    public void sendReport(Report report) {
        List<TimeEntry> logWorks = findTodayLogWorks();

        Optional<String> htmlReport = report.generateHtmlReport(reportProperties, logWorks);
        if (!htmlReport.isPresent()) {
            return;
        }

        doSendReport(htmlReport);
    }

    @SuppressWarnings("all")
    private void doSendReport(Optional<String> htmlReport) {
        Properties props = new Properties();
        props.put("mail.smtp.host", reportProperties.getProperty("mail.smtp.host"));
        props.put("mail.debug", reportProperties.getProperty("mail.debug"));
        Session session = Session.getDefaultInstance(props);

        try {
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(reportProperties.getProperty("mail.from")));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(reportProperties.getProperty("mail.to")));
            String subject = reportProperties.getProperty("report.subject");
            msg.setSubject(String.format(subject, LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
            msg.setSentDate(new Date());
            msg.setContent(htmlReport.get(), "text/html; charset=utf-8");

            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private List<TimeEntry> findTodayLogWorks() {
        Map<String, String> params = new HashMap<>();
        params.put(reportProperties.getProperty("report.user.filter"), "me");
        params.put(reportProperties.getProperty("report.date.filter"), LocalDate.now().toString());

        try {
            return redmineManager.getTimeEntryManager().getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            log.error("Error while getting time entries");
            return new ArrayList<>();
        }
    }
}