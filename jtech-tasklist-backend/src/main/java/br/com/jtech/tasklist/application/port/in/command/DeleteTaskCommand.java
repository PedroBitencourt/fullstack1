package main.java.br.com.jtech.tasklist.application.port.in.command;

import java.util.UUID;

public record DeleteTaskCommand(
        UUID id
) {}
