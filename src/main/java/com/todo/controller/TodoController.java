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

    private Long parseUserIdQueryParam(Context ctx) {
        String userIdParam = ctx.queryParam("userId");
        return parseUserId(userIdParam, ctx);
    }

    private Long parseUserIdFormParam(Context ctx) {
        String userIdParam = ctx.formParam("userId");
        return parseUserId(userIdParam, ctx);
    }

    private Long parseUserId(String userIdParam, Context ctx) {
        if (userIdParam == null) {
            ctx.status(400).result("userId is required");
            return null;
        }

        try {
            return Long.parseLong(userIdParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("userId must be a number");
            return null;
        }
    }

    public void findAll(Context ctx) {
        Long userId = parseUserIdQueryParam(ctx);
        if (userId == null) {
            return;
        }
        List<Todo> todos = findAllTodoUseCase.execute(userId);
        ctx.json(todos);
    }

    public void findById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Long userId = parseUserIdQueryParam(ctx);
        if (userId == null) {
            return;
        }

        Todo todo = findTodoByIdUseCase.execute(id, userId);
        if (todo == null) {
            ctx.status(404).result("Todo not found");
            return;
        }

        ctx.json(todo);
    }

    public void create(Context ctx) {
        String title = ctx.formParam("title");
        Long userId = parseUserIdFormParam(ctx);
        if (userId == null) {
            return;
        }
        Todo todo = new Todo(title, userId);
        createTodoUseCase.execute(todo);
        ctx.status(201).result("Todo created");
    }

    public void edit(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        String title = ctx.formParam("title");
        String doneParam = ctx.formParam("done");
        if (title == null || doneParam == null) {
            ctx.status(400).result("title, userId, and done are required");
            return;
        }

        Long userId = parseUserIdFormParam(ctx);
        if (userId == null) {
            return;
        }
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
        Long userId = parseUserIdQueryParam(ctx);
        if (userId == null) {
            return;
        }
        deleteTodoUseCase.execute(id, userId);
        ctx.status(200).result("Todo deleted");
    }

    public void registerRecurring(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Long userId = parseUserIdQueryParam(ctx);
        if (userId == null) {
            return;
        }
        Todo recurringTodo = recurringTodoUseCase.execute(id, userId);
        ctx.json(recurringTodo);
    }

    public void unregisterRecurring(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Long userId = parseUserIdQueryParam(ctx);
        if (userId == null) {
            return;
        }
        Todo recurringTodo = unregisterRecurringTodoUseCase.execute(id, userId);
        ctx.json(recurringTodo);
    }
}
