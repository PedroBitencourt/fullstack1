package br.com.jtech.tasklist.adapters.output.persistence.mapper;

import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;
import br.com.jtech.tasklist.domain.model.Task;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskPersistenceMapperTest {

    private final TaskPersistenceMapper mapper = Mappers.getMapper(TaskPersistenceMapper.class);

    @Test
    @DisplayName("Deve mapear TaskEntity para Domain Task")
    void deveMapearEntityParaDomain() {

        TaskEntity entity = new TaskEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Título Entity");
        entity.setDescription("Descrição Entity");
        entity.setStatus(TaskStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now().minusDays(1));
        entity.setUpdatedAt(LocalDateTime.now());

        Task domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getTitle()).isEqualTo("Título Entity");
        assertThat(domain.getDescription()).isEqualTo("Descrição Entity");
        assertThat(domain.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve mapear Domain Task para TaskEntity")
    void deveMapearDomainParaEntity() {

        Task domain = new Task();
        domain.setId(UUID.randomUUID());
        domain.setTitle("Título Domain");
        domain.setDescription("Descrição Domain");
        domain.setStatus(TaskStatus.COMPLETED);
        domain.setCreatedAt(LocalDateTime.now().minusHours(2));
        domain.setUpdatedAt(LocalDateTime.now().minusMinutes(10));

        TaskEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getTitle()).isEqualTo("Título Domain");
        assertThat(entity.getDescription()).isEqualTo("Descrição Domain");
        assertThat(entity.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(domain.getUpdatedAt());
    }
}
