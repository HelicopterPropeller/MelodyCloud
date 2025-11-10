package com.example.neteasecloudmusic.data.model;

public class SingletonPlaylist {
    private static volatile SingletonPlaylist instance;
    private Playlist local;

    private SingletonPlaylist() {
        this.local = new Playlist();
    }

    public static SingletonPlaylist getInstance() {
        if (instance == null) {
            synchronized (SingletonUser.class) {
                if (instance == null) {
                    instance = new SingletonPlaylist();
                }
            }
        }
        return instance;
    }

    public Playlist getLocal() {
        return local;
    }
}
