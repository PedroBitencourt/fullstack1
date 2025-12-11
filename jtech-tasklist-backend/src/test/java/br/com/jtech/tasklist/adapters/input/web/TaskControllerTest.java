package br.com.jtech.tasklist.adapters.input.web;

import br.com.jtech.tasklist.adapters.input.web.dto.TaskRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskUpdateRequest;
import br.com.jtech.tasklist.adapters.input.web.mapper.TaskWebMapper;
import br.com.jtech.tasklist.application.port.in.CreateTaskUseCase;
import br.com.jtech.tasklist.application.port.in.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.port.in.FindTaskUseCase;
import br.com.jtech.tasklist.application.port.in.UpdateTaskUseCase;
import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.DeleteTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.domain.exception.TaskNotFoundException;
import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController - Unit Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTaskUseCase createTaskUseCase;

    @MockitoBean
    private FindTaskUseCase findTaskUseCase;

    @MockitoBean
    private UpdateTaskUseCase updateTaskUseCase;

    @MockitoBean
    private DeleteTaskUseCase deleteTaskUseCase;

    @MockitoBean
    private TaskWebMapper mapper;

    @MockitoBean
    private Clock clock;

    private LocalDateTime now;

    private Instant nowInstant;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 12, 11, 20, 30);
        nowInstant = now.atZone(ZoneId.systemDefault()).toInstant();

        lenient().when(clock.instant()).thenReturn(nowInstant);
        lenient().when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Deve criar tarefa com sucesso")
    void deveCriarTarefaComSucesso() throws Exception {

        TaskRequest request = new TaskRequest("Nova Tarefa", "Descrição");

        CreateTaskCommand command = new CreateTaskCommand(
                request.getTitle(),
                request.getDescription()
        );

        Task savedTask = new Task(
                UUID.randomUUID(),
                "Nova Tarefa",
                "Descrição",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(mapper.toCreateTaskCommand(any(TaskRequest.class))).thenReturn(command);
        when(createTaskUseCase.create(any(CreateTaskCommand.class))).thenReturn(savedTask);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(createTaskUseCase).create(any(CreateTaskCommand.class));
    }


    @Test
    @DisplayName("GET /api/v1/tasks - Deve listar tarefas")
    void deveListarTarefas() throws Exception {

        Task task = new Task(
                UUID.randomUUID(),
                "Teste",
                "Desc",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(findTaskUseCase.findAll()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(findTaskUseCase).findAll();
    }


    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Deve buscar tarefa por id")
    void deveBuscarPorId() throws Exception {

        UUID id = UUID.randomUUID();

        Task task = new Task(
                id,
                "Teste",
                "Desc",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(findTaskUseCase.findById(id)).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isOk());

        verify(findTaskUseCase).findById(id);
    }


    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Deve retornar 404")
    void deveRetornar404() throws Exception {

        UUID id = UUID.randomUUID();

        when(findTaskUseCase.findById(id)).thenThrow(new TaskNotFoundException(id));

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isNotFound());

        verify(findTaskUseCase).findById(id);
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Deve atualizar tarefa")
    void deveAtualizar() throws Exception {

        UUID id = UUID.randomUUID();

        TaskUpdateRequest request = new TaskUpdateRequest(
                "Atualizada",
                "Nova descrição",
                TaskStatus.IN_PROGRESS
        );

        UpdateTaskCommand command = new UpdateTaskCommand(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getStatus()
        );

        Task updated = new Task(
                id,
                "Atualizada",
                "Nova descrição",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(mapper.toUpdateTaskCommand(eq(id), any(TaskUpdateRequest.class))).thenReturn(command);
        when(updateTaskUseCase.update(any(UpdateTaskCommand.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(updateTaskUseCase).update(any(UpdateTaskCommand.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Deve deletar")
    void deveDeletar() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(deleteTaskUseCase).delete(any(DeleteTaskCommand.class));

        mockMvc.perform(delete("/api/v1/tasks/" + id))
                .andExpect(status().isNoContent());

        verify(deleteTaskUseCase).delete(any(DeleteTaskCommand.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Deve retornar 404")
    void deveRetornar404AoDeletar() throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new TaskNotFoundException(id))
                .when(deleteTaskUseCase)
                .delete(any(DeleteTaskCommand.class));

        mockMvc.perform(delete("/api/v1/tasks/" + id))
                .andExpect(status().isNotFound());

        verify(deleteTaskUseCase).delete(any(DeleteTaskCommand.class));
    }
}
