package com.todo.useCase;

import java.util.List;

import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;

public class FindAllTodoUseCase {
    private final TodoRepository todoRepository;

    public FindAllTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> execute() {
        return todoRepository.findAll();
    }
}
