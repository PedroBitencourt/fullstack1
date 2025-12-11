package br.com.jtech.tasklist.application.port.in;

import br.com.jtech.tasklist.application.port.in.command.DeleteTaskCommand;

public interface DeleteTaskUseCase {
    void delete(DeleteTaskCommand command);
}
