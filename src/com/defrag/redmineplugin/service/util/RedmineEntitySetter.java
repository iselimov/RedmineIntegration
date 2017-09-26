package com.defrag.redmineplugin.service.util;

/**
 * Created by defrag on 26.09.17.
 */
public interface RedmineEntitySetter<T> {

    void post(int taskId, T queryValue);
}