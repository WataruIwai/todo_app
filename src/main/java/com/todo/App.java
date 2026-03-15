package com.todo;

import java.sql.Connection;

import com.todo.controller.TodoController;
import com.todo.db.DbConnection;
import com.todo.repository.TodoRepository;
import com.todo.repository.jdbc.JdbcTodoRepository;
import com.todo.useCase.CreateTodoUseCase;
import com.todo.useCase.DeleteTodoUseCase;
import com.todo.useCase.EditTodoUseCase;
import com.todo.useCase.FindAllTodoUseCase;
import com.todo.useCase.FindTodoByIdUseCase;
import com.todo.useCase.RegisterRecurringTodoUseCase;
import com.todo.useCase.UnregisterRecurringTodoUseCase;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        Connection connection = DbConnection.getConnection();
        TodoRepository todoRepository = new JdbcTodoRepository(connection);

        FindAllTodoUseCase findAllTodoUseCase = new FindAllTodoUseCase(todoRepository);
        FindTodoByIdUseCase findTodoByIdUseCase = new FindTodoByIdUseCase(todoRepository);
        CreateTodoUseCase createTodoUseCase = new CreateTodoUseCase(todoRepository);
        EditTodoUseCase editTodoUseCase = new EditTodoUseCase(todoRepository);
        DeleteTodoUseCase deleteTodoUseCase = new DeleteTodoUseCase(todoRepository);
        RegisterRecurringTodoUseCase registerRecurringTodoUseCase = new RegisterRecurringTodoUseCase(todoRepository);
        UnregisterRecurringTodoUseCase unregisterRecurringTodoUseCase = new UnregisterRecurringTodoUseCase(todoRepository);

        TodoController todoController = new TodoController(
            findAllTodoUseCase,
            findTodoByIdUseCase,
            createTodoUseCase,
            editTodoUseCase,
            deleteTodoUseCase,
            registerRecurringTodoUseCase,
            unregisterRecurringTodoUseCase);

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.allowHost("http://localhost:5173"));
            });
        }).start(7070);
        app.get("/todos", todoController::findAll);
        app.get("/todos/{id}", todoController::findById);
        app.post("/todos", todoController::create);
        app.put("/todos/{id}", todoController::edit);
        app.delete("/todos/{id}", todoController::delete);
        app.post("/todos/{id}/recurring", todoController::registerRecurring);
        app.delete("/todos/{id}/recurring", todoController::unregisterRecurring);
    }
}
