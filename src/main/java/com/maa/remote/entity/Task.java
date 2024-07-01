package com.maa.remote.entity;

import lombok.Data;

@Data
public class Task {
    private final String id;
    private final String type;
    private final String params;
}
