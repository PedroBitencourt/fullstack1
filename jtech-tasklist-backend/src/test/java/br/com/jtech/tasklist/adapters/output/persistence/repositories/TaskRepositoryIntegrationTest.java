package br.com.jtech.tasklist.adapters.output.persistence.repositories;


import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TaskRepository - Testes de Integração")
class TaskRepositoryIntegrationTest {

    @Autowired
    private TaskRepository repository;

    private TaskEntity taskEntity;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        taskEntity = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Tarefa de Teste")
                .description("Descrição de teste")
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve salvar tarefa no banco H2")
    void deveSalvarTarefaNoBanco() {
        TaskEntity saved = repository.save(taskEntity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Tarefa de Teste");
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID")
    void deveBuscarTarefaPorId() {
        TaskEntity saved = repository.save(taskEntity);

        Optional<TaskEntity> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Tarefa de Teste");
    }

    @Test
    @DisplayName("Deve retornar empty para ID inexistente")
    void deveRetornarEmptyParaIdInexistente() {
        Optional<TaskEntity> found = repository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void deveListarTodasTarefas() {
        repository.save(taskEntity);

        TaskEntity task2 = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Segunda Tarefa")
                .status(TaskStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        repository.save(task2);

        List<TaskEntity> tasks = repository.findAll();

        assertThat(tasks).hasSize(2);
    }

    @Test
    @DisplayName("Deve atualizar tarefa existente")
    void deveAtualizarTarefaExistente() {
        TaskEntity saved = repository.save(taskEntity);

        saved.setTitle("Título Atualizado");
        saved.setStatus(TaskStatus.COMPLETED);
        TaskEntity updated = repository.save(saved);

        assertThat(updated.getTitle()).isEqualTo("Título Atualizado");
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Deve deletar tarefa")
    void deveDeletarTarefa() {
        TaskEntity saved = repository.save(taskEntity);
        UUID id = saved.getId();

        repository.deleteById(id);

        Optional<TaskEntity> found = repository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar se tarefa existe")
    void deveVerificarSeTarefaExiste() {
        TaskEntity saved = repository.save(taskEntity);

        boolean exists = repository.existsById(saved.getId());
        boolean notExists = repository.existsById(UUID.randomUUID());

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}