package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.TimeEntryFactory;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by defrag on 13.08.17.
 */
@Slf4j
public class TaskManager {

    private final ConnectionInfo connectionInfo;

    private final TaskMapper mapper;

    private final RedmineManager redmineManager;

    public TaskManager(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;

        TaskMapper mapper = new SimpleTaskMapper();
        if (connectionInfo.hasExtendedProps()) {
            this.mapper = new ExtendedTaskMapper(mapper, connectionInfo);
        } else {
            this.mapper = mapper;
        }

        redmineManager = RedmineManagerFactory.createWithApiKey(connectionInfo.getRedmineUri(), connectionInfo.getApiAccessKey());
    }

    public List<Task> getTasks(Params filter) {
        List<Issue> redmineIssues;
        try {
            redmineIssues = redmineManager.getIssueManager().getIssues(filter).getResults();
        } catch (RedmineException e) {
            log.error("Couldn't get issues, reason is {}", e.getLocalizedMessage());
            return Collections.emptyList();
        }

        return mapper.toPluginTasks(redmineIssues);
    }

    public void pushTask(Task task) {

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

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws RedmineException, MessagingException {
        String uri = "https://redmine.eastbanctech.ru";
        String apiAccessKey = "1c8cf98ca9cfaf2684c449014cf3f684b4e0c6db";
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);

//        &set_filter=1&f%5B%5D=status_id&op%5Bstatus_id%5D=o&f%5B%5D=author_id&op%5
//        Bauthor_id%5D=%3D&v%5Bauthor_id%5D%5B%5D=me&f%5B%5D=&c%5B%5D=project&c
//         %5B%5D=parent&c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=priority&c%5B%5D=subject&c%5B%5D=assigned_to
//         &c%5B%5D=updated_on&c%5B%5D=due_date&c%5B%5D=estimated_hours&c%5B%5D=spent_hours&c%5B%5D=
//         total_estimated_hours&c%5B%5D=total_spent_hours&group_by=tracker
//        Params params = new Params()
//                .add("set_filter", "1")
//                .add("f[]", "summary")
//                .add("op[summary]", "~")
//                .add("v[summary]", "another")
//                .add("f[]", "description")
//                .add("op[description]", "~")
//                .add("v[description][]", "abc");
//        https://redmine.eastbanctech.ru/issues?utf8=%E2%9C%93&set_filter=1&f%5B%5D=status_id&op%5Bstatus_id%5D=%3D&v%5Bstatus_id%5D%5B%5D=2&f%5B%5D=&c%5B%5D=project&c%5B%5D=parent&c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=priority&c%5B%5D=subject&c%5B%5D=assigned_to&c%5B%5D=updated_on&c%5B%5D=due_date&c%5B%5D=estimated_hours&c%5B%5D=spent_hours&c%5B%5D=total_estimated_hours&c%5B%5D=total_spent_hours&group_by=
//        /**
//         * f[]:status_id
//         op[status_id]:=
//         v[status_id][]:2
//         f[]:assigned_to_id
//         op[assigned_to_id]:=
//         v[assigned_to_id][]:me
//         */
        Params params = new Params()
                // add("f[]", "tracker_id").add("op[tracker_id]", "=").add("v[tracker_id][]", "4")
                .add("f[]", "status_id")
                .add("op[status_id]", "=")
                .add("v[status_id][]", "2")
//                .add("v[status_id][]", "3")
                .add("f[]", "assigned_to_id")
                .add("op[assigned_to_id]", "=")
                .add("v[assigned_to_id][]", "me");
        // need issueId, comment
        List<Issue> myIssues = mgr.getIssueManager().getIssues(params).getResults();
        Map<Integer, Set<String>> commentsByIssueIds = myIssues
                .stream()
                .map(issue -> {
                    try {
                        return Optional.of(mgr.getTimeEntryManager().getTimeEntriesForIssue(issue.getId()));
                    } catch (RedmineException e) {
                        return Optional.empty();
                    }
                })
                .reduce(new HashMap<>(),
                        (HashMap<Integer, Set<String>> res, Optional<?> comment) -> {
                            if (!comment.isPresent())
                                return res;
                            ((List<TimeEntry>) comment.get())
                                    .stream()
                                    .forEach(entry -> {
                                        if (res.containsKey(entry.getIssueId()))
                                            res.get(entry.getIssueId()).add(entry.getComment());
                                        else
                                            res.put(entry.getIssueId(), new HashSet<>(Collections.singletonList(entry.getComment())));
                                    });
                            return res;
                        },
                        (map1, map2) -> map1
                );
        Issue toUpdate = myIssues.get(0);
        toUpdate.setDescription(toUpdate.getDescription() + " test!");
        mgr.getIssueManager().update(toUpdate);
        TimeEntry newEntry = TimeEntryFactory.create();
        mgr.getTimeEntryManager().createTimeEntry(newEntry);
        List<CustomFieldDefinition> customFieldDefinitions = mgr.getCustomFieldManager().getCustomFieldDefinitions();
        Object o = 1;
//        Issue toUpdate = myIssues.get(0);
//        mgr.getIssueManager().update(toUpdate);

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
            msg.setText("Test");

            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}