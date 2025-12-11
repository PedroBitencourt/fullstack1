package br.com.jtech.tasklist.application.port.out;


import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findAll();

    List<Task> findByStatus(TaskStatus status);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    long count();
}
