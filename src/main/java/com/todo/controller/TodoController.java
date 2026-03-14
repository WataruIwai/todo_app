package com.todo.controller;

import java.util.List;

import com.todo.domain.Todo;
import com.todo.useCase.CreateTodoUseCase;
import com.todo.useCase.DeleteTodoUseCase;
import com.todo.useCase.EditTodoUseCase;
import com.todo.useCase.FindAllTodoUseCase;
import com.todo.useCase.FindTodoByIdUseCase;
import com.todo.useCase.RegisterRecurringTodoUseCase;
import com.todo.useCase.UnregisterRecurringTodoUseCase;

import io.javalin.http.Context;

public class TodoController {
    private final FindAllTodoUseCase findAllTodoUseCase;
    private final FindTodoByIdUseCase findTodoByIdUseCase;
    private final CreateTodoUseCase createTodoUseCase;
    private final EditTodoUseCase editTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;
    private final RegisterRecurringTodoUseCase recurringTodoUseCase;
    private final UnregisterRecurringTodoUseCase unregisterRecurringTodoUseCase;

    public TodoController(
            FindAllTodoUseCase findAllTodoUseCase,
            FindTodoByIdUseCase findTodoByIdUseCase,
            CreateTodoUseCase createTodoUseCase,
            EditTodoUseCase editTodoUseCase,
            DeleteTodoUseCase deleteTodoUseCase,
            RegisterRecurringTodoUseCase recurringTodoUseCase,
            UnregisterRecurringTodoUseCase unregisterRecurringTodoUseCase) {
        this.findAllTodoUseCase = findAllTodoUseCase;
        this.findTodoByIdUseCase = findTodoByIdUseCase;
        this.createTodoUseCase = createTodoUseCase;
        this.editTodoUseCase = editTodoUseCase;
        this.deleteTodoUseCase = deleteTodoUseCase;
        this.recurringTodoUseCase = recurringTodoUseCase;
        this.unregisterRecurringTodoUseCase = unregisterRecurringTodoUseCase;
    }

    public void findAll(Context ctx) {
        List<Todo> todos = findAllTodoUseCase.execute();
        ctx.json(todos);
    }

    public void findById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        long userId = Long.parseLong(ctx.queryParam("userId"));

        Todo todo = findTodoByIdUseCase.execute(id, userId);
        if (todo == null) {
            ctx.status(404).result("Todo not found");
            return;
        }

        ctx.json(todo);
    }

    public void create(Context ctx) {
        String title = ctx.formParam("title");
        long userId = Long.parseLong(ctx.formParam("userId"));
        Todo todo = new Todo(title, userId);
        createTodoUseCase.execute(todo);
        ctx.status(201).result("Todo created");
    }

    public void edit(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        String title = ctx.formParam("title");
        String userIdParam = ctx.formParam("userId");
        String doneParam = ctx.formParam("done");
        if (title == null || userIdParam == null || doneParam == null) {
            ctx.status(400).result("title, userId, and done are required");
            return;
        }

        long userId = Long.parseLong(userIdParam);
        boolean done = Boolean.parseBoolean(doneParam);
        Todo existingTodo = findTodoByIdUseCase.execute(id, userId);
        if (existingTodo == null) {
            ctx.status(404).result("Todo not found");
            return;
        }

        Todo todo = new Todo(id, title, userId, done, existingTodo.isRecurring());
        Todo updatedTodo = editTodoUseCase.execute(todo);
        ctx.json(updatedTodo);
    }

    public void delete(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        long userId = Long.parseLong(ctx.queryParam("userId"));
        deleteTodoUseCase.execute(id, userId);
        ctx.status(200).result("Todo deleted");
    }

    public void registerRecurring(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        long userId = Long.parseLong(ctx.queryParam("userId"));
        Todo recurringTodo = recurringTodoUseCase.execute(id, userId);
        ctx.json(recurringTodo);
    }

    public void unregisterRecurring(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        long userId = Long.parseLong(ctx.queryParam("userId"));
        Todo recurringTodo = unregisterRecurringTodoUseCase.execute(id, userId);
        ctx.json(recurringTodo);
    }
}
