package br.com.jtech.tasklist.domain.exception;

public class InvalidTaskException extends RuntimeException {

    public InvalidTaskException(String message) {
        super(message);
    }

    public static InvalidTaskException titleIsRequired() {
        return new InvalidTaskException("O título da tarefa é obrigatório");
    }

    public static InvalidTaskException taskAlreadyCompleted() {
        return new InvalidTaskException("Tarefa já concluída");
    }
}