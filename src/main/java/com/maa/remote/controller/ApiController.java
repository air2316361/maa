package com.maa.remote.controller;

import com.maa.remote.entity.Task;
import com.maa.remote.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Resource
    private TaskService taskService;

    @GetMapping("/taskList")
    public List<Task> taskList(@RequestParam String device) {
        return taskService.getTaskList(device);
    }

    @GetMapping("/addTask")
    public void addTask(@RequestParam String device, @RequestParam String type, @RequestParam(required = false) String params) {
        taskService.addTask(device, type, params);
    }

    @GetMapping("/removeTask")
    public void removeTask(@RequestParam String device, @RequestParam String task) {
        taskService.removeTask(device, task);
    }

    @GetMapping("/moveTask")
    public void moveTask(@RequestParam String device, @RequestParam String taskId, @RequestParam(required = false) String nextTaskId) {
        taskService.moveTask(device, taskId, nextTaskId);
    }

    @GetMapping("/purgeTask")
    public void purgeTask(@RequestParam String device) {
        taskService.purgeTask(device);
    }

    @GetMapping("/captureImage")
    public String captureImage(@RequestParam String device) {
        return taskService.captureImage(device);
    }
}
