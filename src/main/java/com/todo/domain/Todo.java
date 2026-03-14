package com.todo.domain;

public class Todo {
    private Long id;
    private String title;
    private long userId;
    private boolean done;
    private boolean recurring;

    public Todo(String title, Long userId) {
        this(null, title, userId, false, false);
    }

    public Todo(Long id, String title, Long userId, boolean done, boolean recurring) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.done = done;
        this.recurring = recurring;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isRecurring() {
        return recurring;
    }
}
