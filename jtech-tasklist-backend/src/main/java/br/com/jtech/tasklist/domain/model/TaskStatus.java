package br.com.jtech.tasklist.domain.model;

public enum TaskStatus {
    PENDING("Pendente", "Tarefa ainda não iniciada"),
    IN_PROGRESS("Em Progresso", "Tarefa em andamento"),
    COMPLETED("Concluída", "Tarefa finalizada");

    private final String name;
    private final String description;

    TaskStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
