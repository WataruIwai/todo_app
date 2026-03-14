package com.todo.useCase;

import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;

public class RegisterRecurringTodoUseCase {
    final private TodoRepository todoRepository;

    public RegisterRecurringTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo execute(long id, long userId) {
        return todoRepository.registerRecurringTodo(id, userId);
    }

}
