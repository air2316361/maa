package com.maa.remote.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.maa.remote.entity.Identifier;
import com.maa.remote.entity.Task;
import com.maa.remote.entity.TaskEndpoint;
import com.maa.remote.entity.ThreadWrapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final String CAPTURE_IMAGE = "CaptureImageNow";

    private final Cache<String, List<Task>> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build();

    private final Cache<String, ThreadWrapper> threadCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    public List<Task> getTaskList(String device) {
        return Optional.ofNullable(cache.getIfPresent(device))
                .orElse(Collections.emptyList())
                .stream()
                .filter(task -> {
                    String type = task.getType();
                    return !"CaptureImageNow".equals(type) && !"StopTask".equals(type);
                }).collect(Collectors.toList());
    }

    private List<Task> getAllTaskList(String device) {
        return Optional.ofNullable(cache.getIfPresent(device)).orElse(Collections.emptyList());
    }

    public Map<String, List<Task>> getTaskList(Identifier identifier) {
        return Collections.singletonMap("tasks", getAllTaskList(identifier.getDevice()));
    }

    public void addTask(String device, String type, String params) {
        Task task = new Task(UUID.randomUUID().toString(), type, params);
        List<Task> taskList = cache.getIfPresent(device);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(task);
        cache.put(device, taskList);
    }

    public String captureImage(String device) {
        addTask(device, CAPTURE_IMAGE, null);
        threadCache.put(device, new ThreadWrapper(Thread.currentThread()));
        try {
            Thread.sleep(5000);
            threadCache.invalidate(device);
            return null;
        } catch (InterruptedException e) {
            String payload = Objects.requireNonNull(threadCache.getIfPresent(device)).getPayload();
            threadCache.invalidate(device);
            return payload;
        }
    }

    public void removeTask(String device, String task) {
        List<Task> taskList = getTaskList(device);
        boolean flag = false;
        boolean isFirst = true;
        for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext(); ) {
            Task t = iterator.next();
            if (t.getId().equals(task)) {
                iterator.remove();
                if (isFirst) {
                    taskList.add(new Task(UUID.randomUUID().toString(), "StopTask", null));
                }
                flag = true;
                break;
            }
            isFirst = false;
        }
        if (flag) {
            cache.put(device, taskList);
        }
    }

    public void moveTask(String device, String taskId, String nextTaskId) {
        List<Task> taskList = getTaskList(device);
        Task task = null;
        for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext(); ) {
            Task t = iterator.next();
            if (t.getId().equals(taskId)) {
                task = t;
                iterator.remove();
                break;
            }
        }
        if (task == null) {
            return;
        }
        if (nextTaskId == null) {
            taskList.add(task);
        } else {
            int index = 0;
            for (int i = 0; i < taskList.size(); ++i) {
                if (taskList.get(i).getId().equals(nextTaskId)) {
                    index = i;
                    break;
                }
            }
            taskList.add(index, task);
        }
    }

    public void purgeTask(String device) {
        cache.put(device, Collections.singletonList(new Task(UUID.randomUUID().toString(), "StopTask", null)));
    }

    public void reportTask(TaskEndpoint taskEndpoint) {
        List<Task> taskList = getAllTaskList(taskEndpoint.getDevice());
        for (Iterator<Task> iterator = taskList.iterator(); iterator.hasNext(); ) {
            Task task = iterator.next();
            if (!task.getId().equals(taskEndpoint.getTask())) {
                continue;
            }
            iterator.remove();
            if (CAPTURE_IMAGE.equals(task.getType())) {
                ThreadWrapper threadWrapper = threadCache.getIfPresent(taskEndpoint.getDevice());
                if (threadWrapper != null) {
                    threadWrapper.setPayload(taskEndpoint.getPayload());
                    threadCache.put(taskEndpoint.getDevice(), threadWrapper);
                    threadWrapper.getThread().interrupt();
                }
            }
            break;
        }
    }
}
