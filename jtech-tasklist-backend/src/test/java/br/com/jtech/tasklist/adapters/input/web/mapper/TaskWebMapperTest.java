package br.com.jtech.tasklist.adapters.input.web.mapper;


import br.com.jtech.tasklist.adapters.input.web.dto.TaskRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskUpdateRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskResponse;
import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskWebMapperTest {

    private final TaskWebMapper mapper = Mappers.getMapper(TaskWebMapper.class);

    @Test
    @DisplayName("Deve mapear TaskRequest para CreateTaskCommand")
    void deveMapearTaskRequestParaCreateTaskCommand() {

        TaskRequest request = new TaskRequest();
        request.setTitle("Estudar geografia");
        request.setDescription("Estudar para prova de segunda");

        CreateTaskCommand command = mapper.toCreateTaskCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.title()).isEqualTo("Estudar geografia");
        assertThat(command.description()).isEqualTo("Estudar para prova de segunda");
    }

    @Test
    @DisplayName("Deve mapear TaskUpdateRequest + ID para UpdateTaskCommand")
    void deveMapearTaskUpdateRequestParaUpdateTaskCommand() {

        UUID id = UUID.randomUUID();

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Atualizada");
        request.setDescription("Descrição nova");
        request.setStatus(TaskStatus.COMPLETED);

        UpdateTaskCommand command = mapper.toUpdateTaskCommand(id, request);

        assertThat(command).isNotNull();
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.title()).isEqualTo("Atualizada");
        assertThat(command.description()).isEqualTo("Descrição nova");
        assertThat(command.status()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Deve mapear Domain Task para TaskResponse incluindo statusDescription")
    void deveMapearTaskParaTaskResponse() {

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Ler Capitaes da Areia");
        task.setDescription("Estou no quinto capitulo");
        task.setStatus(TaskStatus.IN_PROGRESS);

        TaskResponse response = mapper.toResponse(task);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(task.getId());
        assertThat(response.getTitle()).isEqualTo("Ler Capitaes da Areia");
        assertThat(response.getDescription()).isEqualTo("Estou no quinto capitulo");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.getStatusDescription())
                .isEqualTo(TaskStatus.IN_PROGRESS.getDescription());
    }
}

