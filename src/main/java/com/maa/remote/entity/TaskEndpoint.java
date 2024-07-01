package com.maa.remote.entity;

import lombok.Data;

@Data
public class TaskEndpoint {
    private String user;
    private String device;
    private String task;
    private String status;
    private String payload;
}
