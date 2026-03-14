package com.todo.useCase;

import com.todo.repository.TodoRepository;

public class DeleteTodoUseCase {
    private TodoRepository todoRepository;

    public DeleteTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void execute(long todoId, long userId) {
        todoRepository.deleteTodo(todoId, userId);
    }
}
