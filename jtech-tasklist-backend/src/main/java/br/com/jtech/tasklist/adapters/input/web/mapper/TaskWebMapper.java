package br.com.jtech.tasklist.adapters.input.web.mapper;

import br.com.jtech.tasklist.adapters.input.web.dto.TaskRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskResponse;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskUpdateRequest;
import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.domain.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskWebMapper {

    CreateTaskCommand toCreateTaskCommand(TaskRequest request);
    UpdateTaskCommand toUpdateTaskCommand(UUID id, TaskUpdateRequest request);

    @Mapping(target = "statusDescricao", source = "status.descricao")
    TaskResponse toResponse(Task domain);
}
