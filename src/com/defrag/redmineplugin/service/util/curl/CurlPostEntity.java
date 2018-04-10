package com.defrag.redmineplugin.service.util.curl;

import com.defrag.redmineplugin.model.ConnectionInfo;
import com.defrag.redmineplugin.service.util.RedmineEntitySetter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by defrag on 24.09.17.
 */
@Getter
@Slf4j
abstract class CurlPostEntity<T> extends CurlEntity implements RedmineEntitySetter<T> {

    CurlPostEntity(ConnectionInfo connectionInfo) {
        super(connectionInfo, RequestType.POST);
    }

    @Override
    public void post(int taskId, T queryValue) {
        String[] curlCommand = new String[] {"/bin/bash", "-c", getCommand(taskId, queryValue)};
        try {
            log.info("Try to execute post command {}", curlCommand[2]);
            Process post = new ProcessBuilder(curlCommand).start();
            post.waitFor();
        } catch (IOException e) {
            log.error("Couldn't execute post command!");
        } catch (InterruptedException e) {
            log.error("Thread was interrupted while making post query");
        }
    }

    abstract String getCommand(int taskId, T queryValue);
}