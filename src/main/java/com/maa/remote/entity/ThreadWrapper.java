package com.maa.remote.entity;

import lombok.Data;

@Data
public class ThreadWrapper {
    private final Thread thread;
    private String payload;
}
