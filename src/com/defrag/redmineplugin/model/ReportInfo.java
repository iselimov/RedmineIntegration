package com.defrag.redmineplugin.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by defrag on 14.09.17.
 */
@Getter
@Builder
public class ReportInfo {

    private String fullName;

    private String position;

    private String phone;

    private String domainName;

    private String skype;
}