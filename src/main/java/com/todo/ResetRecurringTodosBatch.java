package com.todo;

import java.sql.Connection;
import com.todo.db.DbConnection;

import com.todo.repository.TodoRepository;
import com.todo.repository.jdbc.JdbcTodoRepository;
import com.todo.useCase.ResetRecurringTodoDoneStatusUseCase;

public class ResetRecurringTodosBatch {
    public static void main(String[] args) {
        try(Connection connection = DbConnection.getConnection();) {
            TodoRepository todoRepository = new JdbcTodoRepository(connection);
            ResetRecurringTodoDoneStatusUseCase useCase =
            new ResetRecurringTodoDoneStatusUseCase(todoRepository);

            int updatedCount = useCase.execute();
            System.out.println("reset count: " + updatedCount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
