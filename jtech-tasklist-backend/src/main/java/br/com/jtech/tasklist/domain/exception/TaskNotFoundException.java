package main.java.br.com.jtech.tasklist.domain.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID id) {
        super(String.format("Tarefa não encontrada com ID: " + id));
    }
}
