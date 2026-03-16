package com.todo.repository;

import java.util.List;

import com.todo.domain.Todo;

public interface TodoRepository {
    List<Todo> findAllByUserId(long userId);
    Todo findById(long todoId, long userId);
    void createTodo(Todo todo);
    Todo editTodo(Todo todo);
    void deleteTodo(long todoId, long userId);
    Todo registerRecurringTodo(long todoId, long userId);
    Todo unregisterRecurringTodo(long todoId, long userId);
    int resetRecurringTodoStatus();
}
