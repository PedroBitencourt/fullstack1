package main.java.br.com.jtech.tasklist.application.port.in;

import main.java.br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import main.java.br.com.jtech.tasklist.domain.model.Task;

public interface CreateTaskUseCase {
    Task create(CreateTaskCommand command);
}
