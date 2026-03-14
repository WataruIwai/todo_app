package com.todo.domain;

public class User {
    private long id;
    private String name;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
