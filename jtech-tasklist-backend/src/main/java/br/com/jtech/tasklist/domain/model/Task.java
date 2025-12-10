package main.java.br.com.jtech.tasklist.domain.model;

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
        if (title == null || title.isBlank()) {
            throw InvalidTaskException.titleIsRequired();
        }
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.createdAt = now;
    }

    public Task(UUID id, String title,String description, TaskStatus status,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void concluir(LocalDateTime now) {
        this.status = TaskStatus.COMPLETED;
        this.updatedAt = now;
    }

    public void iniciarProgresso(LocalDateTime now) {
        if (this.status == TaskStatus.COMPLETED) {
            throw InvalidTaskException.taskAlreadyCompleted();
        }
        this.status = TaskStatus.IN_PROGRESS;
        this.updatedAt = now;
    }
}

