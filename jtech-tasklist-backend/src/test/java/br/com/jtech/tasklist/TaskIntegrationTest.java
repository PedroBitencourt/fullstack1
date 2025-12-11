package br.com.jtech.tasklist;

import br.com.jtech.tasklist.adapters.input.web.dto.TaskRequest;
import br.com.jtech.tasklist.adapters.input.web.dto.TaskUpdateRequest;
import br.com.jtech.tasklist.adapters.output.persistence.repositories.TaskRepository;
import br.com.jtech.tasklist.domain.model.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Deve criar tarefa via API e persistir no H2")
    void deveCriarTarefaViaAPI() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Tarefa de Integração")
                .description("Teste completo")
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Tarefa de Integração"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.statusDescription").exists());
    }

    @Test
    @Order(2)
    @DisplayName("Fluxo completo: Criar → Listar → Buscar → Atualizar → Deletar")
    void fluxoCompletoDeOperacoes() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Tarefa Fluxo Completo")
                .description("Teste de integração")
                .build();

        String response = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(response).get("id").asText());

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Tarefa Fluxo Completo"));

        TaskUpdateRequest update = TaskUpdateRequest.builder()
                .title("Tarefa Atualizada")
                .description("Descrição atualizada")
                .status(TaskStatus.COMPLETED)
                .build();

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Atualizada"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(delete("/api/v1/tasks/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @DisplayName("Deve criar múltiplas tarefas e listar todas")
    void deveCriarMultiplasTarefas() throws Exception {
        for (int i = 1; i <= 3; i++) {
            TaskRequest request = TaskRequest.builder()
                    .title("Tarefa " + i)
                    .build();

            mockMvc.perform(post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Order(4)
    @DisplayName("Deve validar campos obrigatórios")
    void deveValidarCamposObrigatorios() throws Exception {
        TaskRequest semTitulo = TaskRequest.builder()
                .title("")
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(semTitulo)))
                .andExpect(status().isBadRequest());

        TaskUpdateRequest semStatus = TaskUpdateRequest.builder()
                .title("Tarefa")
                .build();

        mockMvc.perform(put("/api/v1/tasks/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(semStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Deve retornar 404 para recursos inexistentes")
    void deveRetornar404ParaRecursosInexistentes() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(id.toString())));

        mockMvc.perform(delete("/api/v1/tasks/" + id))
                .andExpect(status().isNotFound());
    }


    @Test
    @Order(6)
    @DisplayName("Deve retornar lista vazia quando não houver tarefas")
    void deveRetornarListaVaziaQuandoNaoHouverTarefas() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}