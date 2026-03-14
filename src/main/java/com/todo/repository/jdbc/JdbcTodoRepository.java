package com.todo.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.todo.repository.TodoRepository;
import com.todo.domain.Todo;

public class JdbcTodoRepository implements TodoRepository {
    private Connection connection;

    public JdbcTodoRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Todo> findAll() {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT id, user_id, title, done, recurring FROM todos")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Todo> todos = new ArrayList<>();
                while (resultSet.next()) {
                    Todo todo = new Todo(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getLong("user_id"),
                        resultSet.getBoolean("done"),
                        resultSet.getBoolean("recurring")
                    );
                    todos.add(todo);
                }
                return todos;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Todo findById(long todoId, long userId) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT id, title, user_id, done, recurring FROM todos WHERE id = ? AND user_id = ?")) {
                statement.setLong(1, todoId);
                statement.setLong(2, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    return new Todo(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getLong("user_id"),
                        resultSet.getBoolean("done"),
                        resultSet.getBoolean("recurring")
                    );
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public void createTodo(Todo todo) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO todos (title, user_id) VALUES (?, ?)")) {
            statement.setString(1, todo.getTitle());
            statement.setLong(2, todo.getUserId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Unexpected number of inserted rows: " + affectedRows);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Todo editTodo(Todo todo) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE todos SET title = ?, done = ? WHERE id = ? AND user_id = ?")) {
            statement.setString(1, todo.getTitle());
            statement.setBoolean(2, todo.isDone());
            statement.setLong(3, todo.getId());
            statement.setLong(4, todo.getUserId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Unexpected number of updated rows: " + affectedRows);
            }
            return findById(todo.getId(), todo.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTodo(long todoId, long userId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM todos WHERE id = ? AND user_id = ?")) {
            statement.setLong(1, todoId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Unexpected number of deleted rows: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Todo registerRecurringTodo(long todoId, long userId) {
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE todos SET recurring = true WHERE id = ? AND user_id = ?")) {
                statement.setLong(1, todoId);
                statement.setLong(2, userId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows != 1) {
                    throw new RuntimeException("Unexpected number of updated rows: " + affectedRows);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO daily_todos (todo_id, user_id) VALUES (?, ?)")) {
                statement.setLong(1, todoId);
                statement.setLong(2, userId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows != 1) {
                    throw new RuntimeException("Unexpected number of inserted rows: " + affectedRows);
                }
            }
            connection.commit();
            return findById(todoId, userId);
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback recurring registration.", rollbackException);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to reset auto-commit.", e);
            }
        }
    }

    @Override
    public Todo unregisterRecurringTodo(long todoId, long userId) {
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE todos SET recurring = false WHERE id = ? AND user_id = ?")) {
                statement.setLong(1, todoId);
                statement.setLong(2, userId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows != 1) {
                    throw new RuntimeException("Unexpected number of updated rows: " + affectedRows);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM daily_todos WHERE todo_id = ? AND user_id = ?")) {
                statement.setLong(1, todoId);
                statement.setLong(2, userId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows != 1) {
                    throw new RuntimeException("Unexpected number of deleted rows: " + affectedRows);
                }
            }
            connection.commit();
            return findById(todoId, userId);
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Failed to rollback recurring unregistration.", rollbackException);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to reset auto-commit.", e);
            }
        }
    }

    @Override
    public int resetRecurringTodoStatus() {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE todos " +
                "SET done = false " +
                "WHERE id IN (SELECT todo_id FROM daily_todos) " +
                "AND done = true")) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
