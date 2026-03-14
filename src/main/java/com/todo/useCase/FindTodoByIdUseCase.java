package com.todo.useCase;

import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;

public class FindTodoByIdUseCase {
    private final TodoRepository todoRepository;

    public FindTodoByIdUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    public Todo execute(long todoId, long userId) {
        Todo todo = todoRepository.findById(todoId, userId);
        return todo;
    }
}
