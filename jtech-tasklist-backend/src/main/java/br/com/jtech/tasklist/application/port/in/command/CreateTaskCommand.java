package br.com.jtech.tasklist.application.port.in.command;

public record CreateTaskCommand(
        String title,
        String description
) {}
