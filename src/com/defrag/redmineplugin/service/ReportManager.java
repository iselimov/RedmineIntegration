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
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        InputStreamReader resourceReader = new InputStreamReader(currLoader.getResourceAsStream("report.properties"),
                "UTF-8");
        reportProperties.load(resourceReader);
    }

    public void sendReport(Report report) {
        lastReport = report;

        Map<String, String> params = new HashMap<>();
        params.put(reportProperties.getProperty("report.user.filter"), "me");
        params.put(reportProperties.getProperty("report.date.filter"), LocalDate.now().toString());

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

    public static void main(String[] args) throws IOException {
        String uri = "https://redmine.eastbanctech.ru";
        String apiAccessKey = "1c8cf98ca9cfaf2684c449014cf3f684b4e0c6db";
        ReportManager mgr = new ReportManager(new ConnectionInfo(uri, apiAccessKey));

        Report r = Report.builder()
                .fullName("Ильяс Селимов")
                .position("Разработчик")
                .phone("+7 953 803 6510")
                .domainName("i.selimov")
                .skype("all4fun7")
                .build();

        mgr.sendReport(r);
    }
}