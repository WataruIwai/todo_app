package com.todo.useCase;

import com.todo.repository.TodoRepository;

public class ResetRecurringTodoDoneStatusUseCase {
    final private TodoRepository todoRepository;

    public ResetRecurringTodoDoneStatusUseCase (TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public int execute() {
        return todoRepository.resetRecurringTodoStatus();
    }
}
