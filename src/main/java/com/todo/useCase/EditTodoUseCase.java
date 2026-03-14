package com.todo.useCase;

import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;

public class EditTodoUseCase {
    private TodoRepository todoRepository;

    public EditTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo execute(Todo todo) {
        return todoRepository.editTodo(todo);
    }
}
