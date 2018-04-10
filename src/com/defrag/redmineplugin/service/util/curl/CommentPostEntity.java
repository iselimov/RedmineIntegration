package com.defrag.redmineplugin.service.util.curl;

import com.defrag.redmineplugin.model.ConnectionInfo;

/**
 * Created by defrag on 26.09.17.
 */
public class CommentPostEntity extends CurlPostEntity<String> {

    public CommentPostEntity(ConnectionInfo connectionInfo) {
        super(connectionInfo);
    }

    @Override
    String getCommand(int taskId, String queryValue) {
        String commentsTemplate = getRequestType();
        String updateTemplate = curlProperties.getProperty("curl.post.comment");
        return String.format(commentsTemplate, connectionInfo.getCookie(), connectionInfo.getCsrfToken(),
                String.format(updateTemplate, queryValue), connectionInfo.getRedmineUri(), taskId);
    }
}