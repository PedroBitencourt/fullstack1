package br.com.jtech.tasklist.adapters.input.web;

import br.com.jtech.tasklist.adapters.input.web.documentation.TaskControllerDocs;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskResponse;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskUpdateRequest;
import br.com.jtech.tasklist.adapters.input.web.mapper.TaskWebMapper;
import br.com.jtech.tasklist.application.port.in.CreateTaskUseCase;
import br.com.jtech.tasklist.application.port.in.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.port.in.FindTaskUseCase;
import br.com.jtech.tasklist.application.port.in.UpdateTaskUseCase;
import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.DeleteTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.domain.model.Task;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController implements TaskControllerDocs {

    private final CreateTaskUseCase createTaskUseCase;
    private final FindTaskUseCase findTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final TaskWebMapper mapper;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        log.info("POST /tasks - Criando tarefa: {}", request.getTitle());

        Task createdTask = createTaskUseCase.create(mapper.toCreateTaskCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(createdTask));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll() {
        log.info("GET /tasks - Listando todas");

        List<TaskResponse> responses = findTaskUseCase.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable UUID id) {
        log.info("GET /tasks/{}", id);

        Task task = findTaskUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable UUID id, @Valid @RequestBody TaskUpdateRequest request) {
        log.info("PUT /tasks/{}", id);

        UpdateTaskCommand task = mapper.toUpdateTaskCommand(id, request);
        Task updatedTask = updateTaskUseCase.update(task);
        return ResponseEntity.ok(mapper.toResponse(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /tasks/{}", id);

        deleteTaskUseCase.delete(new DeleteTaskCommand(id));
        return ResponseEntity.noContent().build();
    }
}