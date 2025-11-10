package com.example.neteasecloudmusic.data.model;

public class SingletonUser {
    private static volatile SingletonUser instance;
    private User self;

    private SingletonUser() {
        this.self = new User();
    }

    public static SingletonUser getInstance() {
        if (instance == null) {
            synchronized (SingletonUser.class) {
                if (instance == null) {
                    instance = new SingletonUser();
                }
            }
        }
        return instance;
    }

    public User getSelf() {
        return self;
    }
}