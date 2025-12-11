package br.com.jtech.tasklist.domain.model;

import main.java.br.com.jtech.tasklist.domain.exception.InvalidTaskException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(String title, String description, LocalDateTime now) {
        validateTitle(title);

        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.createdAt = now;
    }

    public void updateDetails(String title, String description, LocalDateTime now) {
        validateTitle(title);

        this.title = title;
        this.description = description;
        this.updatedAt = now;
    }

    public void changeStatus(TaskStatus newStatus, LocalDateTime now) {
        if (this.status == TaskStatus.COMPLETED) {
            throw InvalidTaskException.taskAlreadyCompleted();
        }
        this.status = newStatus;
        this.updatedAt = now;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw InvalidTaskException.titleIsRequired();
        }
    }
}

