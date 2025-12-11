package br.com.jtech.tasklist.domain.model;

import br.com.jtech.tasklist.domain.exception.InvalidTaskException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task() {}
    public Task(UUID id, String title, String description, TaskStatus status,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateTitle(title);
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Task(String title, String description, LocalDateTime now) {
        validateTitle(title);

        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.createdAt = now;
        this.updatedAt = now;
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

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

