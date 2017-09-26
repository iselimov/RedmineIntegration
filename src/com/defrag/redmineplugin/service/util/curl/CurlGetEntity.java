package com.defrag.redmineplugin.service.util.curl;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.service.util.RedmineEntityGetter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * Created by defragon 26.09.17.
 */
@Slf4j
abstract class CurlGetEntity extends CurlEntity implements RedmineEntityGetter {

    CurlGetEntity(ConnectionInfo connectionInfo) {
        super(connectionInfo, RequestType.GET);
    }

    @Override
    public Optional<String> get(int taskId) {

        String[] curlCommand = new String[] {"/bin/bash", "-c", getCommand(taskId)};
        String remainingStr;
        try {
            log.info("Try to execute get command {}", curlCommand[2]);

            Process proc = new ProcessBuilder(curlCommand).start();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                remainingStr = in.readLine();
            }
        } catch (IOException e) {
            log.error("Couldn't execute get command!");
            return Optional.empty();
        }

        return Optional.ofNullable(remainingStr);
    }

    abstract String getCommand(int taskId);
}