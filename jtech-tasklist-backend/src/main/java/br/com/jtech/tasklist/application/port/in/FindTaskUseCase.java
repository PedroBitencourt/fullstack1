package br.com.jtech.tasklist.application.port.in;

import br.com.jtech.tasklist.domain.model.Task;

import java.util.List;
import java.util.UUID;

public interface FindTaskUseCase {
    Task findById(UUID id);
    List<Task> findAll();
}
