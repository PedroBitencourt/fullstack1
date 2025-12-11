package br.com.jtech.tasklist.adapters.output.persistence.mapper;

import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;
import br.com.jtech.tasklist.domain.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskPersistenceMapper {

    Task toDomain(TaskEntity entity);

    TaskEntity toEntity(Task domain);
}
