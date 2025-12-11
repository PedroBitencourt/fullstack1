package br.com.jtech.tasklist.application.port.in.command;

import br.com.jtech.tasklist.domain.model.TaskStatus;

import java.util.UUID;

public record UpdateTaskCommand(
        UUID id,
        String title,
        String description,
        TaskStatus status
) {}
