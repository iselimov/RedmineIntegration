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
import org.apache.commons.lang.NotImplementedException;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;
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

    public Optional<Task> createTask(Task task) {
        throw new NotImplementedException();
    }

    public Optional<Task> updateTask(Task task) {
        log.info("Updating task with id {}", task.getId());

        Issue issue;
        try {
            issue = redmineManager.getIssueManager().getIssueById(task.getId());
        } catch (RedmineException e) {
            log.error("Error while getting task");
            return Optional.empty();
        }
        Optional<Issue> toUpdate = mapper.toRedmineTask(task, issue);
        if (!toUpdate.isPresent()) {
            return Optional.empty();
        }

        Map<Integer, TimeEntry> redmineTimeEntries;
        try {
            redmineTimeEntries = redmineManager.getTimeEntryManager().getTimeEntriesForIssue(task.getId())
                    .stream()
                    .collect(Collectors.toMap(TimeEntry::getId, te -> te));
        } catch (RedmineException e) {
            log.error("Error while getting log works for task");
            return Optional.empty();
        }
        List<TimeEntry> pluginTimeEntries = mapper.toRedmineLogWorks(task.getLogWorks(), redmineTimeEntries, task.getId());

        try {
            redmineManager.getIssueManager().update(toUpdate.get());
        } catch (RedmineException e) {
            log.error("Error while updating task");
        }

        for (TimeEntry pluginTimeEntry : pluginTimeEntries) {
            if (pluginTimeEntry.getId() == null) {
                try {
                    redmineManager.getTimeEntryManager().createTimeEntry(pluginTimeEntry);
                } catch (RedmineException e) {
                    log.error("Error while creating log work with comment {}", pluginTimeEntry.getComment());
                }

            } else {
                try {
                    redmineManager.getTimeEntryManager().update(pluginTimeEntry);
                } catch (RedmineException e) {
                    log.error("Error while updating log work with comment {}", pluginTimeEntry.getComment());
                }
            }
        }

        Set<Integer> toUpdatePluginTimeEntries = pluginTimeEntries
                .stream()
                .filter(te -> te.getId() != null)
                .map(TimeEntry::getId)
                .collect(Collectors.toSet());

        redmineTimeEntries.entrySet()
                .stream()
                .filter((entry) -> !toUpdatePluginTimeEntries.contains(entry.getKey()))
                .forEach(entry -> {
                    try {
                        redmineManager.getTimeEntryManager().deleteTimeEntry(entry.getKey());
                    } catch (RedmineException e) {

                    }
                });

        return Optional.of(task);
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

}