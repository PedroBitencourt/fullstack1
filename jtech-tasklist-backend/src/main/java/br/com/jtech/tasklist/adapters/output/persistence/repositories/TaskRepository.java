package br.com.jtech.tasklist.adapters.output.persistence.repositories;

import br.com.jtech.tasklist.domain.model.TaskStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByStatus(TaskStatus status);
}