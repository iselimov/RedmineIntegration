package com.defrag.redmineplugin.service.util;

import com.defrag.redmineplugin.model.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * Created by defrag on 24.09.17.
 */
@Slf4j
public class CurlExecutor {

    public Optional<String> get(CurlEntity request) {
        String[] curlCommand = new String[]{"/bin/bash", "-c", request.getCommand()};

        String remainingStr;
        try {
            log.info("Try to execute get command");

            Process proc = new ProcessBuilder(curlCommand).start();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                remainingStr = in.readLine();
            }
        } catch (IOException e) {
            log.error("Couldn't execute get command!");
            return Optional.empty();
        }

        return Optional.of(remainingStr);
    }

    public void post(CurlEntity request) {
        ConnectionInfo connectionInfo = request.getConnectionInfo();

        String[] curlCommand = new String[]{"/bin/bash", "-c", request.getCommand()};


    }
}