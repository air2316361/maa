package com.maa.remote.controller;

import com.maa.remote.entity.Identifier;
import com.maa.remote.entity.Task;
import com.maa.remote.entity.TaskEndpoint;
import com.maa.remote.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maa")
public class MaaController {

    @Resource
    private TaskService taskService;

    @PostMapping("/getTask")
    public Map<String, List<Task>> getTask(@RequestBody Identifier identifier) {
        return taskService.getTaskList(identifier);
    }

    @PostMapping("/reportStatus")
    public String reportStatus(@RequestBody TaskEndpoint taskEndpoint) {
        taskService.reportTask(taskEndpoint);
        return "success";
    }
}
