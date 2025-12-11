package br.com.jtech.tasklist.adapters.input.web.documentation;

import br.com.jtech.tasklist.adapters.input.web.dto.TaskRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskResponse;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Tasks", description = "Operações relacionadas ao gerenciamento de tarefas")
public interface TaskControllerDocs {

    @Operation(
            summary = "Cria uma nova tarefa",
            description = "Recebe título e descrição e cria uma nova tarefa no sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    ResponseEntity<TaskResponse> create(TaskRequest request);

    @Operation(
            summary = "Busca todas as tarefas",
            description = "Retorna os dados de todas as tarefas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefas encontradas",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
            }
    )
    ResponseEntity<List<TaskResponse>> findAll();

    @Operation(
            summary = "Busca uma tarefa pelo ID",
            description = "Retorna os dados da tarefa correspondente ao ID informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa encontrada",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    ResponseEntity<TaskResponse> findById(UUID id);

    @Operation(
            summary = "Atualiza os dados de uma tarefa",
            description = "Atualiza título, descrição e/ou status de uma tarefa específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa atualizada",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    ResponseEntity<TaskResponse> update(UUID id, TaskUpdateRequest request);

    @Operation(
            summary = "Remove uma tarefa",
            description = "Deleta a tarefa pelo ID informado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarefa removida"),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
            }
    )
    ResponseEntity<Void> delete(UUID id);
}
