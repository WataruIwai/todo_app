package com.todo.useCase;

import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;

public class CreateTodoUseCase {
    private TodoRepository todoRepository;

    public CreateTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void execute(Todo todo) {
        todoRepository.createTodo(todo);
    }
}
