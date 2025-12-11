package br.com.jtech.tasklist.application.service;

import br.com.jtech.tasklist.application.port.in.CreateTaskUseCase;
import br.com.jtech.tasklist.application.port.in.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.port.in.FindTaskUseCase;
import br.com.jtech.tasklist.application.port.in.UpdateTaskUseCase;
import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.DeleteTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.application.port.out.TaskRepositoryPort;
import br.com.jtech.tasklist.domain.exception.TaskNotFoundException;
import br.com.jtech.tasklist.domain.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService implements
        CreateTaskUseCase,
        UpdateTaskUseCase,
        DeleteTaskUseCase,
        FindTaskUseCase {

    private final TaskRepositoryPort repository;
    private final Clock clock;

    @Override
    public Task create(CreateTaskCommand command) {
        log.info("Criando tarefa");
        var now = LocalDateTime.now(clock);
        Task task = new Task(
                command.title(),
                command.description(),
                now
        );

        return repository.save(task);
    }

    @Override
    public Task findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public List<Task> findAll() {
        return repository.findAll();
    }

    @Override
    public Task update(UpdateTaskCommand command) {
        log.info("Atualizando tarefa: {}", command.id());

        Task task = repository.findById(command.id())
                .orElseThrow(() -> new TaskNotFoundException(command.id()));

        task.updateDetails(
                command.title(),
                command.description(),
                LocalDateTime.now(clock)
        );

        if (command.status() != null) {
            task.changeStatus(command.status(), LocalDateTime.now(clock));
        }

        return repository.save(task);
    }

    @Override
    public void delete(DeleteTaskCommand command) {
        log.info("Deletando tarefa: {}", command.id());

        if (!repository.existsById(command.id())) {
            throw new TaskNotFoundException(command.id());
        }

        repository.deleteById(command.id());
    }
}
