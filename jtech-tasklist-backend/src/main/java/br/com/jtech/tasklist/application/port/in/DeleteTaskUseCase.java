package main.java.br.com.jtech.tasklist.application.port.in;

import main.java.br.com.jtech.tasklist.application.port.in.command.DeleteTaskCommand;

public interface DeleteTaskUseCase {
    void delete(DeleteTaskCommand command);
}
