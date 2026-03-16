package com.todo;

import com.todo.db.ConnectionProvider;
import com.todo.db.DefaultConnectionProvider;

import com.todo.repository.TodoRepository;
import com.todo.repository.jdbc.JdbcTodoRepository;
import com.todo.useCase.ResetRecurringTodoDoneStatusUseCase;

public class ResetRecurringTodosBatch {
    public static void main(String[] args) {
        ConnectionProvider connectionProvider = new DefaultConnectionProvider();
        TodoRepository todoRepository = new JdbcTodoRepository(connectionProvider);
        ResetRecurringTodoDoneStatusUseCase useCase =
        new ResetRecurringTodoDoneStatusUseCase(todoRepository);

        int updatedCount = useCase.execute();
        System.out.println("reset count: " + updatedCount);
    }
}
