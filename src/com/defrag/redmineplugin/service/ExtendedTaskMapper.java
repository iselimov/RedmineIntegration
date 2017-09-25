package com.defrag.redmineplugin.service;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.model.LogWork;
import com.defrag.redmineplugin.model.RedmineIssue;
import com.defrag.redmineplugin.model.Task;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by defrag on 22.08.17.
 */
@Slf4j
public class ExtendedTaskMapper implements TaskMapper {

    private final TaskMapper taskMapper;

    private final ConnectionInfo connectionInfo;

    public ExtendedTaskMapper(TaskMapper taskMapper, ConnectionInfo connectionInfo) {
        this.taskMapper = taskMapper;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public Optional<Task> toPluginTask(RedmineIssue source) {
        Optional<Task> dest = taskMapper.toPluginTask(source);
        if (!dest.isPresent()) {
            return Optional.empty();
        }

        findRemainingHours(source.getIssue().getId()).ifPresent(hours -> dest.get().setRemaining(hours));
        return dest;
    }

    @Override
    public Optional<Issue> toRedmineTask(Task source) {
        Optional<Issue> dest = taskMapper.toRedmineTask(source);

        if (!dest.isPresent()) {
            return Optional.empty();
        }
        // todo
        return dest;
    }

    @Override
    public Optional<Issue> toRedmineTask(Task pluginTask, Issue toUpdateTask) {
        return taskMapper.toRedmineTask(pluginTask, toUpdateTask);
    }

    @Override
    public List<TimeEntry> toRedmineLogWorks(List<LogWork> pluginLogWorks, Map<Integer, TimeEntry> sourceTimeEntries, int taskId) {
        return taskMapper.toRedmineLogWorks(pluginLogWorks, sourceTimeEntries, taskId);
    }

    private Optional<Float> findRemainingHours(Integer taskId) {


        if (StringUtils.isBlank(remainingStr)) {
            log.info("Remaining hours is blank, set it to zero");
            return Optional.of(0.0f);
        }

        try {
            return Optional.of(Float.valueOf(remainingStr));
        } catch (NumberFormatException e) {
            log.error("Couldn't parse remaining str value {}", remainingStr);
            return Optional.empty();
        }
    }
}