package br.com.jtech.tasklist.adapters.output.persistence;

import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.persistence.mapper.TaskPersistenceMapper;
import br.com.jtech.tasklist.adapters.output.persistence.repositories.TaskRepository;
import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskPersistenceAdapter - Testes Unitários")
class TaskPersistenceAdapterTest {

    @Mock
    private TaskRepository jpaRepository;

    @Mock
    private TaskPersistenceMapper mapper;

    @InjectMocks
    private TaskPersistenceAdapter adapter;

    private Task task;
    private TaskEntity entity;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        task = new Task();
        task.setId(id);
        task.setTitle("Tarefa Teste");
        task.setDescription("Descrição");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        entity = TaskEntity.builder()
                .id(id)
                .title("Tarefa Teste")
                .description("Descrição")
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve salvar tarefa com sucesso")
    void deveSalvarTarefaComSucesso() {
        when(mapper.toEntity(task)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(task);

        Task result = adapter.save(task);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(jpaRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID")
    void deveBuscarTarefaPorId() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(task);

        Optional<Task> result = adapter.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve retornar empty quando tarefa não existe")
    void deveRetornarEmptyQuandoTarefaNaoExiste() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Task> result = adapter.findById(id);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void deveListarTodasTarefas() {
        TaskEntity entity2 = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Tarefa 2")
                .status(TaskStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Task task2 = new Task();
        task2.setId(entity2.getId());
        task2.setTitle("Tarefa 2");
        task2.setDescription("Desc 2");
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());

        when(jpaRepository.findAll()).thenReturn(List.of(entity, entity2));
        when(mapper.toDomain(entity)).thenReturn(task);
        when(mapper.toDomain(entity2)).thenReturn(task2);

        List<Task> result = adapter.findAll();

        assertThat(result).hasSize(2);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve verificar se tarefa existe")
    void deveVerificarSeTarefaExiste() {
        when(jpaRepository.existsById(id)).thenReturn(true);

        boolean exists = adapter.existsById(id);

        assertThat(exists).isTrue();
        verify(jpaRepository).existsById(id);
    }

    @Test
    @DisplayName("Deve deletar tarefa por ID")
    void deveDeletarTarefa() {
        doNothing().when(jpaRepository).deleteById(id);

        adapter.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }
}
