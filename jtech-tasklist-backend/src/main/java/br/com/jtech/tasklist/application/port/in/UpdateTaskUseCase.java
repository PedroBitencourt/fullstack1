package main.java.br.com.jtech.tasklist.application.port.in;

import main.java.br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import main.java.br.com.jtech.tasklist.domain.model.Task;

public interface UpdateTaskUseCase {
    Task update(UpdateTaskCommand command);
}
