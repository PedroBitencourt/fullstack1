package br.com.jtech.tasklist.application.port.in;


import br.com.jtech.tasklist.application.port.in.command.UpdateTaskCommand;
import br.com.jtech.tasklist.domain.model.Task;

public interface UpdateTaskUseCase {
    Task update(UpdateTaskCommand command);
}
