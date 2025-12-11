package br.com.jtech.tasklist.application.port.in;

import br.com.jtech.tasklist.application.port.in.command.CreateTaskCommand;
import br.com.jtech.tasklist.domain.model.Task;

public interface CreateTaskUseCase {
    Task create(CreateTaskCommand command);
}
