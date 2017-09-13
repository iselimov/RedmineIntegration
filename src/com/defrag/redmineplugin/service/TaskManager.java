package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.RedmineIssue;
import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by defrag on 13.08.17.
 */
@Slf4j
public class TaskManager {

    @Getter
    private final ConnectionInfo connectionInfo;

    private final TaskMapper mapper;

    private final RedmineManager redmineManager;

    public TaskManager(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;

        redmineManager = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(), connectionInfo.getApiAccessKey());

        TaskMapper mapper = new SimpleTaskMapper();
        if (connectionInfo.hasExtendedProps()) {
            log.info("Create extended task mapper");
            this.mapper = new ExtendedTaskMapper(mapper, connectionInfo);
        } else {
            log.info("Create simple task mapper");
            this.mapper = mapper;
        }
    }

    public List<Task> getTasks(Params filter) {
        log.info("filter is {}", filter.getList());
        List<RedmineIssue> redmineIssues;
        try {
            redmineIssues = redmineManager.getIssueManager().getIssues(filter).getResults()
                    .stream()
                    .map(RedmineIssue::new)
                    .peek(this::enrichWithLogWork)
                    .collect(Collectors.toList());
        } catch (RedmineException e) {
            log.error("Couldn't get issues, reason is {}", e.getLocalizedMessage());
            return Collections.emptyList();
        }

        return mapper.toPluginTasks(redmineIssues);
    }

    private void enrichWithLogWork(RedmineIssue issue) {
        List<TimeEntry> timeEntries;
        try {
            timeEntries = redmineManager.getTimeEntryManager().getTimeEntriesForIssue(issue.getIssue().getId());
        } catch (RedmineException e) {
            log.error("Couldn't get time entries if issue {}, reason is {}", issue.getIssue().getId(), e.getLocalizedMessage());
            return;
        }

        timeEntries.forEach(te -> issue.getTimeEntries().add(te));
    }

    public Task updateTask(Task task) {
        log.info("Got it!");
        return task;
    }

    public boolean pullTasks() {
        Params params = new Params()
                .add("f[]", "status_id")
                .add("op[status_id]", "=")
                .add("v[status_id][]", "2")
//                .add("v[status_id][]", "3")
                .add("f[]", "assigned_to_id")
                .add("op[assigned_to_id]", "=")
                .add("v[assigned_to_id][]", "me");

        return pullTasks(params);
    }

    public boolean pullTasks(Params filterParams) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(),
                connectionInfo.getApiAccessKey());
        List<Issue> redmineTasks;
        try {
            redmineTasks = mgr.getIssueManager().getIssues(filterParams).getResults();
        } catch (RedmineException e) {
            log.error("Can't refresh tasks {}", e.getMessage());
            return false;
        }

//        tasks.addAll(mapper.toPluginTasks(redmineTasks));
        return true;
    }

    public void sendReport() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", "me");
        params.put("spent_on", LocalDate.now().toString());

        List<TimeEntry> timeEntries;
        try {
            timeEntries = redmineManager.getTimeEntryManager().getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            log.error("Error while time entries ");
            return;
        }

        generateHtmlReport(timeEntries);
    }

    private static Optional<String> generateHtmlReport(List<TimeEntry> timeEntries) {
        if (timeEntries.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("<div id=\"x_divtagdefaultwrapper\" dir=\"ltr\" style=\"font-size:12pt; color:rgb(0,0,0); " +
                "font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;," +
                "NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;" +
                ",&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols\">" +
                "<p>Привет!</p>" +
                "<p><br></p>");

        builder.append(
                "<p>По #<span>41399:</span></p>\n" +
                "<p><span>Тест.</span></p>\n" +
                "<p>По #<span>41399:</span></p>\n" +
                "<p><span>Тест.</span></p>\n");

        builder.append("<span></span></p><p style=\"font-family:Calibri,Helvetica,sans-serif,EmojiFont," +
                "&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;," +
                "&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;" +
                ",NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols;font-size:16px\">" +
                "<span style=\"font-size:12pt; color:rgb(33,33,33); font-family:Calibri,sans-serif,serif,EmojiFont\">" +
                "С уважением,</span><br></p><p style=\"font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple " +
                "Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android " +
                "Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;," +
                "NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols; font-size:16px\">" +
                "</p><div style=\"font-size:16px; font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple Color " +
                "Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;" +
                "Android Emoji&quot;,EmojiSymbols\"><font face=\"wf_segoe-ui_normal,Segoe UI,Segoe WP,Tahoma,Arial," +
                "sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:15px\"><div>" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"3\" color=\"#212121\">" +
                "<span style=\"font-size:12pt\">Ильяс Селимов</span></font></div></div><div><div style=\"margin:0px\">" +
                "<font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"" +
                "font-size:10pt\">Разработчик</span></font></div></div><div><div style=\"margin:0px\"><font face=\"Calibri" +
                ",sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:10pt\">" +
                "Отдел технологий Java</span></font></div></div><div style=\"margin-bottom:9pt\"><div style=\"margin:0px\">" +
                "<font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span lang=\"en-US\" " +
                "style=\"font-size:10pt\">EastBanc Technologies</span></font></div>\n" +
                "</div>\n" +
                "<div style=\"margin-bottom:4.5pt\">\n" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span lang=\"en-US\" style=\"font-size:10.5pt\">+7 953 803 6510</span></font></div>\n" +
                "</div>\n" +
                "<div style=\"margin-bottom:4.5pt\">\n" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#94BA0F\"><span lang=\"en-US\" style=\"font-size:9pt\">i.selimov</span></font><a href=\"mailto:s.lozhkin@eastbanctech.ru\" target=\"_blank\" rel=\"noopener noreferrer\" title=\"mailto:s.lozhkin@eastbanctech.ru\n" +
                "Ctrl+Click or tap to follow the link\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\"><span style=\"font-size:11pt\"><font size=\"2\"><span lang=\"en-US\" style=\"font-size:9pt\">@eastbanctech.ru</span></font></span></font></a></div>\n" +
                "</div>\n" +
                "<div style=\"margin-bottom:14.25pt\">\n" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:9pt\">Skype:</span></font><font face=\"Times New Roman,serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\">&nbsp;</span></font><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:9pt\">&nbsp;live:iselimov92</span></font></div>\n" +
                "</div>\n" +
                "<div style=\"margin-bottom:20.25pt\">\n" +
                "<div style=\"margin:0px\"><img src=\"cid:image003.png@01D2D573.93364B50\" width=\"92\" height=\"30\" border=\"0\" alt=\"id:image003.png@01D2D573.93364B50\" id=\"x_x_x_x_x_x_x_x_x_x_x_x_x_x_x_x_x__x0000_i1025\" style=\"\"></div>\n" +
                "</div>\n" +
                "<div>\n" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">Россия, 630128, Новосибирск</span></font></div>\n" +
                "</div>\n" +
                "<div>\n" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">Кутателадзе, 4Г, офис 318</span></font></div>\n" +
                "</div>\n" +
                "<div>\n" +
                "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">+7 (383) 363-33-51, 363-33-50</span></font></div>\n" +
                "</div>\n" +
                "<div style=\"margin-top:5pt; margin-bottom:5pt\">\n" +
                "<div style=\"margin:0px\"><a href=\"mailto:info@eastbanctech.ru\" target=\"_blank\" rel=\"noopener noreferrer\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\"><span style=\"font-size:11pt\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">info@eastbanctech.ru</span></font></span></font></a><font face=\"Times New Roman,serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\">&nbsp;</span></font><font face=\"Times New Roman,serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\"><br>\n" +
                "</span></font><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\"><span style=\"font-size:11pt\"><font size=\"2\"><span style=\"font-size:10pt\"><a href=\"http://www.eastbanctech.ru/\" target=\"_blank\" rel=\"noopener noreferrer\">www.eastbanctech.ru</a></span></font></span></font></div>\n" +
                "</div>\n" +
                "</span></font></div>\n" +
                "\n" +
                "<p></p>\n" +
                "<div id=\"x_Signature\">\n" +
                "<div id=\"x_divtagdefaultwrapper\" style=\"font-size:12pt; color:rgb(0,0,0); background-color:rgb(255,255,255); font-family:Calibri,Arial,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols\">\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>");

        return Optional.of(builder.toString());
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws RedmineException, MessagingException {
        String uri = "https://redmine.eastbanctech.ru";
        String apiAccessKey = "1c8cf98ca9cfaf2684c449014cf3f684b4e0c6db";
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
        Map<String, String> params = new HashMap<>();
        params.put("user_id", "me");
        params.put("spent_on", LocalDate.now().toString());

        List<TimeEntry> entries = mgr.getTimeEntryManager().getTimeEntries(params).getResults();
        Object i = entries;
//
////        &set_filter=1&f%5B%5D=status_id&op%5Bstatus_id%5D=o&f%5B%5D=author_id&op%5
////        Bauthor_id%5D=%3D&v%5Bauthor_id%5D%5B%5D=me&f%5B%5D=&c%5B%5D=project&c
////         %5B%5D=parent&c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=priority&c%5B%5D=subject&c%5B%5D=assigned_to
////         &c%5B%5D=updated_on&c%5B%5D=due_date&c%5B%5D=estimated_hours&c%5B%5D=spent_hours&c%5B%5D=
////         total_estimated_hours&c%5B%5D=total_spent_hours&group_by=tracker
////        Params params = new Params()
////                .add("set_filter", "1")
////                .add("f[]", "summary")
////                .add("op[summary]", "~")
////                .add("v[summary]", "another")
////                .add("f[]", "description")
////                .add("op[description]", "~")
////                .add("v[description][]", "abc");
////        https://redmine.eastbanctech.ru/issues?utf8=%E2%9C%93&set_filter=1&f%5B%5D=status_id&op%5Bstatus_id%5D=%3D&v%5Bstatus_id%5D%5B%5D=2&f%5B%5D=&c%5B%5D=project&c%5B%5D=parent&c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=priority&c%5B%5D=subject&c%5B%5D=assigned_to&c%5B%5D=updated_on&c%5B%5D=due_date&c%5B%5D=estimated_hours&c%5B%5D=spent_hours&c%5B%5D=total_estimated_hours&c%5B%5D=total_spent_hours&group_by=
////        /**
////         * f[]:status_id
////         op[status_id]:=
////         v[status_id][]:2
////         f[]:assigned_to_id
////         op[assigned_to_id]:=
////         v[assigned_to_id][]:me
////         */
//        Params params = new Params()
//                // add("f[]", "tracker_id").add("op[tracker_id]", "=").add("v[tracker_id][]", "4")
//                .add("f[]", "status_id")
//                .add("op[status_id]", "=")
//                .add("v[status_id][]", "2")
////                .add("v[status_id][]", "3")
//                .add("f[]", "assigned_to_id")
//                .add("op[assigned_to_id]", "=")
//                .add("v[assigned_to_id][]", "me");
//        // need issueId, comment
//        List<Issue> myIssues = mgr.getIssueManager().getIssues(params).getResults();
//        Map<Integer, Set<String>> commentsByIssueIds = myIssues
//                .stream()
//                .map(issue -> {
//                    try {
//                        return Optional.of(mgr.getTimeEntryManager().getTimeEntriesForIssue(issue.getId()));
//                    } catch (RedmineException e) {
//                        return Optional.empty();
//                    }
//                })
//                .reduce(new HashMap<>(),
//                        (HashMap<Integer, Set<String>> res, Optional<?> comment) -> {
//                            if (!comment.isPresent())
//                                return res;
//                            ((List<TimeEntry>) comment.get())
//                                    .stream()
//                                    .forEach(entry -> {
//                                        if (res.containsKey(entry.getIssueId()))
//                                            res.get(entry.getIssueId()).add(entry.getComment());
//                                        else
//                                            res.put(entry.getIssueId(), new HashSet<>(Collections.singletonList(entry.getComment())));
//                                    });
//                            return res;
//                        },
//                        (map1, map2) -> map1
//                );
//        Issue toUpdate = myIssues.get(0);
//        toUpdate.setDescription(toUpdate.getDescription() + " test!");
//        mgr.getIssueManager().update(toUpdate);
//        TimeEntry newEntry = TimeEntryFactory.create();
//        mgr.getTimeEntryManager().createTimeEntry(newEntry);
//        List<CustomFieldDefinition> customFieldDefinitions = mgr.getCustomFieldManager().getCustomFieldDefinitions();
//        Object o = 1;
////        Issue toUpdate = myIssues.get(0);
////        mgr.getIssueManager().update(toUpdate);

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
//            msg.setContent(
//                    "<div id=\"x_divtagdefaultwrapper\" dir=\"ltr\" style=\"font-size:12pt; color:rgb(0,0,0); " +
//                    "font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;," +
//                    "NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;" +
//                    ",&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols\">\n" +
//                    "<p>Привет!</p>\n" +
//                    "<p><br></p>\n" +
//                    "<p>По #<span>41399:</span></p>\n" +
//                    "<p><span>Тест.</span></p>\n" +
//                    "<p>По #<span>41399:</span></p>\n" +
//                    "<p><span>Тест.</span></p>\n" +
//                    "<p><br></p>\n" +
//                    "<p><span></span></p>\n" +
//                    "<p style=\"font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols; font-size:16px\">\n" +
//                    "<span style=\"font-size:12pt; color:rgb(33,33,33); font-family:Calibri,sans-serif,serif,EmojiFont\">С уважением,</span><br>\n" +
//                    "</p>\n" +
//                    "<p style=\"font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols; font-size:16px\">\n" +
//                    "</p>\n" +
//                    "<div style=\"font-size:16px; font-family:Calibri,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols\">\n" +
//                    "<font face=\"wf_segoe-ui_normal,Segoe UI,Segoe WP,Tahoma,Arial,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:15px\">\n" +
//                    "<div>\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\">Ильяс Селимов</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div>\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:10pt\">Разработчик</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div>\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:10pt\">Отдел технологий Java</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div style=\"margin-bottom:9pt\">\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span lang=\"en-US\" style=\"font-size:10pt\">EastBanc Technologies</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div style=\"margin-bottom:4.5pt\">\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span lang=\"en-US\" style=\"font-size:10.5pt\">+7 953 803 6510</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div style=\"margin-bottom:4.5pt\">\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#94BA0F\"><span lang=\"en-US\" style=\"font-size:9pt\">i.selimov</span></font><a href=\"mailto:s.lozhkin@eastbanctech.ru\" target=\"_blank\" rel=\"noopener noreferrer\" title=\"mailto:s.lozhkin@eastbanctech.ru\n" +
//                    "Ctrl+Click or tap to follow the link\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\"><span style=\"font-size:11pt\"><font size=\"2\"><span lang=\"en-US\" style=\"font-size:9pt\">@eastbanctech.ru</span></font></span></font></a></div>\n" +
//                    "</div>\n" +
//                    "<div style=\"margin-bottom:14.25pt\">\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:9pt\">Skype:</span></font><font face=\"Times New Roman,serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\">&nbsp;</span></font><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#212121\"><span style=\"font-size:9pt\">&nbsp;live:iselimov92</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div style=\"margin-bottom:20.25pt\">\n" +
//                    "<div style=\"margin:0px\"><img src=\"cid:image003.png@01D2D573.93364B50\" width=\"92\" height=\"30\" border=\"0\" alt=\"id:image003.png@01D2D573.93364B50\" id=\"x_x_x_x_x_x_x_x_x_x_x_x_x_x_x_x_x__x0000_i1025\" style=\"\"></div>\n" +
//                    "</div>\n" +
//                    "<div>\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">Россия, 630128, Новосибирск</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div>\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">Кутателадзе, 4Г, офис 318</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div>\n" +
//                    "<div style=\"margin:0px\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">+7 (383) 363-33-51, 363-33-50</span></font></div>\n" +
//                    "</div>\n" +
//                    "<div style=\"margin-top:5pt; margin-bottom:5pt\">\n" +
//                    "<div style=\"margin:0px\"><a href=\"mailto:info@eastbanctech.ru\" target=\"_blank\" rel=\"noopener noreferrer\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\"><span style=\"font-size:11pt\"><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\" color=\"#9A9A9A\"><span style=\"font-size:10pt\">info@eastbanctech.ru</span></font></span></font></a><font face=\"Times New Roman,serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\">&nbsp;</span></font><font face=\"Times New Roman,serif,serif,EmojiFont\" size=\"3\" color=\"#212121\"><span style=\"font-size:12pt\"><br>\n" +
//                    "</span></font><font face=\"Calibri,sans-serif,serif,EmojiFont\" size=\"2\"><span style=\"font-size:11pt\"><font size=\"2\"><span style=\"font-size:10pt\"><a href=\"http://www.eastbanctech.ru/\" target=\"_blank\" rel=\"noopener noreferrer\">www.eastbanctech.ru</a></span></font></span></font></div>\n" +
//                    "</div>\n" +
//                    "</span></font></div>\n" +
//                    "\n" +
//                    "<p></p>\n" +
//                    "<div id=\"x_Signature\">\n" +
//                    "<div id=\"x_divtagdefaultwrapper\" style=\"font-size:12pt; color:rgb(0,0,0); background-color:rgb(255,255,255); font-family:Calibri,Arial,Helvetica,sans-serif,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols,EmojiFont,&quot;Apple Color Emoji&quot;,&quot;Segoe UI Emoji&quot;,NotoColorEmoji,&quot;Segoe UI Symbol&quot;,&quot;Android Emoji&quot;,EmojiSymbols\">\n" +
//                    "</div>\n" +
//                    "</div>\n" +
//                    "</div>", "text/html; charset=utf-8");
            msg.setContent(generateHtmlReport(entries).get(), "text/html; charset=utf-8");

            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}