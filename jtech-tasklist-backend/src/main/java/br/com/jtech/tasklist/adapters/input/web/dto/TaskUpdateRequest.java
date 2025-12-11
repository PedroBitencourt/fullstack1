package br.com.jtech.tasklist.adapters.input.web.dto;

import br.com.jtech.tasklist.domain.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {

    @NotBlank(message = "O título da tarefa é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "O status da tarefa é obrigatório")
    private TaskStatus status;
}
