package br.com.jtech.tasklist.application.service;

import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.DeleteTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.application.port.out.TaskRepositoryPort;
import br.com.jtech.tasklist.domain.exception.InvalidTaskException;
import br.com.jtech.tasklist.domain.exception.TaskNotFoundException;
import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - Testes Unitários")
class TaskServiceTest {

    @Mock
    private TaskRepositoryPort repository;

    @Mock
    private Clock clock;

    @InjectMocks
    private TaskService taskService;

    private Task validTask;
    private LocalDateTime now;
    private Instant nowInstant;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 12, 11, 20, 30);
        nowInstant = now.atZone(ZoneId.systemDefault()).toInstant();

        lenient().when(clock.instant()).thenReturn(nowInstant);
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        validTask = new Task("Tarefa de Teste", "Descrição da tarefa", now);
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso")
    void deveCriarTarefaComSucesso() {
        CreateTaskCommand newTaskCommand = new CreateTaskCommand("Nova Tarefa", "Descrição");

        when(repository.save(any(Task.class))).thenReturn(validTask);

        Task result = taskService.create(newTaskCommand);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Tarefa de Teste");
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar tarefa sem título")
    void deveLancarExcecaoAoCriarTarefaSemTitulo() {
        assertThatThrownBy(() -> new Task(null, "Descrição", now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("título");

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void deveBuscarTarefaPorIdComSucesso() {
        UUID taskId = validTask.getId();
        when(repository.findById(taskId)).thenReturn(Optional.of(validTask));

        Task result = taskService.findById(taskId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Tarefa de Teste");
        verify(repository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar tarefa inexistente")
    void deveLancarExcecaoAoBuscarTarefaInexistente() {
        UUID inexistentId = UUID.randomUUID();
        when(repository.findById(inexistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(inexistentId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(inexistentId.toString());

        verify(repository, times(1)).findById(inexistentId);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void deveListarTodasTarefas() {
        Task task2 = new Task("Segunda Tarefa", "Descrição 2", now);
        when(repository.findAll()).thenReturn(Arrays.asList(validTask, task2));

        List<Task> result = taskService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getTitle)
                .containsExactly("Tarefa de Teste", "Segunda Tarefa");
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver tarefas")
    void deveRetornarListaVaziaQuandoNaoHouverTarefas() {
        when(repository.findAll()).thenReturn(List.of());

        List<Task> result = taskService.findAll();

        assertThat(result).isEmpty();
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso")
    void deveAtualizarTarefaComSucesso() {
        UUID taskId = validTask.getId();
        UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(taskId, "Tarefa Atualizada", "Nova descrição", TaskStatus.IN_PROGRESS);

        when(repository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(repository.save(any(Task.class))).thenReturn(validTask);

        Task result = taskService.update(updateTaskCommand);

        assertThat(result).isNotNull();
        verify(repository, times(1)).findById(taskId);
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar tarefa inexistente")
    void deveLancarExcecaoAoAtualizarTarefaInexistente() {
        UUID inexistentId = UUID.randomUUID();
        UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(inexistentId, "Tarefa", "Descrição", TaskStatus.COMPLETED);

        when(repository.findById(inexistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(updateTaskCommand))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repository, times(1)).findById(inexistentId);
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve deletar tarefa com sucesso")
    void deveDeletarTarefaComSucesso() {
        UUID taskId = validTask.getId();
        DeleteTaskCommand deleteTaskCommand = new DeleteTaskCommand(taskId);

        when(repository.existsById(taskId)).thenReturn(true);
        doNothing().when(repository).deleteById(taskId);

        assertThatCode(() -> taskService.delete(deleteTaskCommand))
                .doesNotThrowAnyException();

        verify(repository, times(1)).existsById(taskId);
        verify(repository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar tarefa inexistente")
    void deveLancarExcecaoAoDeletarTarefaInexistente() {
        UUID inexistentId = UUID.randomUUID();
        DeleteTaskCommand deleteTaskCommand = new DeleteTaskCommand(inexistentId);

        when(repository.existsById(inexistentId)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(deleteTaskCommand))
                .isInstanceOf(TaskNotFoundException.class);

        verify(repository, times(1)).existsById(inexistentId);
        verify(repository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao mudar status de tarefa concluída")
    void deveLancarExcecaoAoMudarStatusDeTarefaConcluida() {
        Task completedTask = new Task("Tarefa Concluída", "Desc", now);
        completedTask.changeStatus(TaskStatus.COMPLETED, now);

        UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(completedTask.getId(), "Tarefa Atualizada", "Nova descrição", TaskStatus.IN_PROGRESS);

        when(repository.findById(completedTask.getId())).thenReturn(Optional.of(completedTask));

        assertThatThrownBy(() -> taskService.update(updateTaskCommand))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("concluída");
    }
}
