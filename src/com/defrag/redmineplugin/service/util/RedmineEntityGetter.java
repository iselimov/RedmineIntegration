package com.defrag.redmineplugin.service.util;

import java.util.Optional;

/**
 * Created by defrag on 26.09.17.
 */
public interface RedmineEntityGetter {

    Optional<String> get(int taskId);
}