package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.Report;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
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

    @Getter
    private Report lastReport;

    public ReportManager(ConnectionInfo connectionInfo) throws IOException {
        redmineManager = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(), connectionInfo.getApiAccessKey());
        reportProperties = new Properties();
        ClassLoader currLoader = Thread.currentThread().getContextClassLoader();
        InputStream propIs = currLoader.getResourceAsStream("report.properties");
        reportProperties.load(propIs);
    }

    public void sendReport(Report report) {
        lastReport = report;

        Map<String, String> params = new HashMap<>();
        params.put("user_id", "me");
        params.put("spent_on", LocalDate.now().toString());

        List<TimeEntry> timeEntries;
        try {
            timeEntries = redmineManager.getTimeEntryManager().getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            log.error("Error while getting time entries");
            return;
        }

        Optional<String> htmlReport = report.generateHtmlReport(reportProperties, timeEntries);
        if (!htmlReport.isPresent()) {
            return;
        }

        String from = "i.selimov@eastbanctech.ru";
        String to = from;
        String host = "portal";
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.debug", "true");
        Session session = Session.getDefaultInstance(props);

        try {
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject("!Test");
            msg.setSentDate(new Date());
            msg.setContent(htmlReport.get(), "text/html; charset=utf-8");

            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
