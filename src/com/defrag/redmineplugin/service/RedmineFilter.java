package com.defrag.redmineplugin.service;

import com.taskadapter.redmineapi.Params;
import org.apache.http.message.BasicNameValuePair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by defrag on 18.08.17.
 */
public interface RedmineFilter {

    Params commonParams = new Params()
                    .add("f[]", "assigned_to_id")
                    .add("op[assigned_to_id]", "=")
                    .add("v[assigned_to_id][]", "me");

    String getName();

    String getParamId();

    List<BasicNameValuePair> getCustomFilters();

    static <T extends RedmineFilter> Optional<T> getEnumItem(T[] values, String name) {
        return Arrays.stream(values)
                .filter(enm -> enm.getName().equalsIgnoreCase(name))
                .findAny();
    }

    static Params getFilter(RedmineFilter value) {
        Params filter = new Params();
        filter.getList().addAll(commonParams.getList());
        filter.getList().addAll(value.getCustomFilters());
        return filter;
    }
}