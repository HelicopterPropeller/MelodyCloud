package com.example.neteasecloudmusic.data.model;

import android.graphics.Bitmap;

public class Song {

    private long id;
    private String path;
    private String title;
    private User author;
    private long duration;
    private Bitmap cover;
    private int coverId;
    private Playlist belongAlbum;
    private Boolean isLike;
    private Boolean isVip;

    public Song(long id, String path, String title, User author, long duration, Bitmap cover, int coverId, Playlist belongAlbum, Boolean isLike, Boolean isVip) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.cover = cover;
        this.coverId = coverId;
        this.belongAlbum = belongAlbum;
        this.isLike = isLike;
        this.isVip = isVip;
    }

    public Song(long id, String path, String title, String author, long duration, String belongAlbum, Bitmap cover) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.author = new User(author);
        this.duration = duration;
        this.belongAlbum = new Playlist(belongAlbum);
        this.cover = cover;
    }

    public Song(int coverId, String title, User author, Boolean isLike, Boolean isVip) {
        this.coverId = coverId;
        this.title = title;
        this.author = author;
        this.isLike = isLike;
        this.isVip = isVip;
    } // SONGS

    public Song() {
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public User getAuthor() {
        return author;
    }

    public long getDuration() {
        return duration;
    }

    public Bitmap getCover() {
        return cover;
    }

    public int getCoverId() {
        return coverId;
    }

    public Playlist getBelongAlbum() {
        return belongAlbum;
    }

    public Boolean isLike() {
        return isLike;
    }

    public Boolean isVip() {
        return isVip;
    }
}
