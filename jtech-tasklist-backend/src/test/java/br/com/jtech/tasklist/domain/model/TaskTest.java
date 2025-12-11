package br.com.jtech.tasklist.domain.model;

import br.com.jtech.tasklist.domain.exception.InvalidTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Task (Domain) - Testes Unitários")
class TaskTest {

    private Task task;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        task = new Task(
                UUID.randomUUID(),
                "Tarefa Teste",
                "Descrição",
                TaskStatus.PENDING,
                now,
                now
        );
    }

    @Test
    @DisplayName("Deve criar tarefa com construtor completo")
    void deveCriarTarefaComConstrutorCompleto() {
        UUID id = UUID.randomUUID();
        Task newTask = new Task(
                id,
                "Nova Tarefa",
                "Descrição detalhada",
                TaskStatus.PENDING,
                now,
                now
        );

        assertThat(newTask.getId()).isEqualTo(id);
        assertThat(newTask.getTitle()).isEqualTo("Nova Tarefa");
        assertThat(newTask.getDescription()).isEqualTo("Descrição detalhada");
        assertThat(newTask.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(newTask.getCreatedAt()).isEqualTo(now);
        assertThat(newTask.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Deve criar tarefa com construtor simplificado")
    void deveCriarTarefaComConstrutorSimplificado() {
        Task newTask = new Task("Tarefa Simples", "Descrição", now);

        assertThat(newTask.getId()).isNotNull();
        assertThat(newTask.getTitle()).isEqualTo("Tarefa Simples");
        assertThat(newTask.getDescription()).isEqualTo("Descrição");
        assertThat(newTask.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(newTask.getCreatedAt()).isEqualTo(now);
        assertThat(newTask.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Deve gerar UUID único para cada tarefa")
    void deveGerarUUIDUnicoParaCadaTarefa() {
        Task task1 = new Task("Tarefa 1", "Desc 1", now);
        Task task2 = new Task("Tarefa 2", "Desc 2", now);

        assertThat(task1.getId()).isNotEqualTo(task2.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar tarefa sem título")
    void deveLancarExcecaoAoCriarTarefaSemTitulo() {
        assertThatThrownBy(() -> new Task(null, "Descrição", now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("título");

        assertThatThrownBy(() -> new Task("", "Descrição", now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("título");

        assertThatThrownBy(() -> new Task("   ", "Descrição", now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("título");
    }

    @Test
    @DisplayName("Deve atualizar detalhes da tarefa")
    void deveAtualizarDetalhesDaTarefa() {
        LocalDateTime updateTime = now.plusHours(1);

        task.updateDetails("Título Atualizado", "Nova Descrição", updateTime);

        assertThat(task.getTitle()).isEqualTo("Título Atualizado");
        assertThat(task.getDescription()).isEqualTo("Nova Descrição");
        assertThat(task.getUpdatedAt()).isEqualTo(updateTime);
        assertThat(task.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com título inválido")
    void deveLancarExcecaoAoAtualizarComTituloInvalido() {
        assertThatThrownBy(() -> task.updateDetails(null, "Desc", now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("título");

        assertThatThrownBy(() -> task.updateDetails("", "Desc", now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("título");
    }

    @Test
    @DisplayName("Deve mudar status da tarefa")
    void deveMudarStatusDaTarefa() {
        LocalDateTime changeTime = now.plusMinutes(30);

        task.changeStatus(TaskStatus.IN_PROGRESS, changeTime);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getUpdatedAt()).isEqualTo(changeTime);
    }

    @Test
    @DisplayName("Deve permitir mudança de PENDING para IN_PROGRESS")
    void devePermitirMudancaDePendenteParaEmProgresso() {
        task.changeStatus(TaskStatus.IN_PROGRESS, now);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Deve permitir mudança de IN_PROGRESS para COMPLETED")
    void devePermitirMudancaDeEmProgressoParaConcluida() {
        task.changeStatus(TaskStatus.IN_PROGRESS, now);
        task.changeStatus(TaskStatus.COMPLETED, now.plusMinutes(10));

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Não deve permitir mudança de status de tarefa concluída")
    void naoDevePermitirMudancaDeStatusDeTarefaConcluida() {
        task.changeStatus(TaskStatus.COMPLETED, now);

        assertThatThrownBy(() -> task.changeStatus(TaskStatus.IN_PROGRESS, now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("concluída");

        assertThatThrownBy(() -> task.changeStatus(TaskStatus.PENDING, now))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessageContaining("concluída");
    }

    @Test
    @DisplayName("Deve aceitar descrição nula ou vazia")
    void deveAceitarDescricaoNulaOuVazia() {
        Task taskSemDesc = new Task("Título", null, now);
        assertThat(taskSemDesc.getDescription()).isNull();

        Task taskDescVazia = new Task("Título", "", now);
        assertThat(taskDescVazia.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("Deve preservar createdAt ao atualizar")
    void devePreservarCreatedAtAoAtualizar() {
        LocalDateTime creationTime = now;
        LocalDateTime updateTime = now.plusDays(1);

        task.updateDetails("Novo Título", "Nova Desc", updateTime);

        assertThat(task.getCreatedAt()).isEqualTo(creationTime);
        assertThat(task.getUpdatedAt()).isEqualTo(updateTime);
    }

    @Test
    @DisplayName("Deve preservar createdAt ao mudar status")
    void devePreservarCreatedAtAoMudarStatus() {
        LocalDateTime creationTime = now;
        LocalDateTime changeTime = now.plusHours(2);

        task.changeStatus(TaskStatus.COMPLETED, changeTime);

        assertThat(task.getCreatedAt()).isEqualTo(creationTime);
        assertThat(task.getUpdatedAt()).isEqualTo(changeTime);
    }

    @Test
    @DisplayName("Deve ter status PENDING ao criar tarefa")
    void deveTerStatusPendenteAoCriarTarefa() {
        Task newTask = new Task("Tarefa", "Desc", now);

        assertThat(newTask.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Deve usar setters corretamente")
    void deveUsarSettersCorretamente() {
        UUID newId = UUID.randomUUID();
        LocalDateTime newTime = now.plusHours(1);

        task.setId(newId);
        task.setTitle("Novo Título");
        task.setDescription("Nova Descrição");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setCreatedAt(newTime);
        task.setUpdatedAt(newTime);

        assertThat(task.getId()).isEqualTo(newId);
        assertThat(task.getTitle()).isEqualTo("Novo Título");
        assertThat(task.getDescription()).isEqualTo("Nova Descrição");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getCreatedAt()).isEqualTo(newTime);
        assertThat(task.getUpdatedAt()).isEqualTo(newTime);
    }

    @Test
    @DisplayName("Deve criar tarefa com construtor vazio")
    void deveCriarTarefaComConstrutorVazio() {
        Task emptyTask = new Task();

        assertThat(emptyTask).isNotNull();
        assertThat(emptyTask.getId()).isNull();
        assertThat(emptyTask.getTitle()).isNull();
    }
}