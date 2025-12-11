package br.com.jtech.tasklist.adapters.output.persistence;

import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.persistence.mapper.TaskPersistenceMapper;
import br.com.jtech.tasklist.adapters.output.persistence.repositories.TaskRepository;
import br.com.jtech.tasklist.application.port.out.TaskRepositoryPort;
import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final TaskRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public Task save(Task task) {
        log.debug("Salvando tarefa no banco: {}", task);
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
