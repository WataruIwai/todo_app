package com.todo.useCase;

import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;

public class UnregisterRecurringTodoUseCase {
    final private TodoRepository todoRepository;

    public UnregisterRecurringTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo execute(long id, long userId) {
        return todoRepository.unregisterRecurringTodo(id, userId);
    }
}
