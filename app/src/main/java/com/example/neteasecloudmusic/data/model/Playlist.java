package com.example.neteasecloudmusic.data.model;

import java.util.List;

public class Playlist {

    private String title;
    private String introduce;
    private User creator;
    private List<Song> list;
    private int coverId;
    private long times;
    boolean isAlbum;
    boolean isMine;
    boolean isCollect;

    public Playlist(String title, String introduce, User creator, List<Song> list, int coverId, long times, boolean isAlbum, boolean isMine, boolean isCollect) {
        this.title = title;
        this.introduce = introduce;
        this.creator = creator;
        this.list = list;
        this.coverId = coverId;
        this.times = times;
        this.isAlbum = isAlbum;
        this.isMine = isMine;
        this.isCollect = isCollect;
    }

    public Playlist(String title) {
        this.title = title;
    }

    public Playlist() {
    }

    public Playlist(int coverId) { // Music->Carousel
        this.coverId = coverId;
    }

    public Playlist(int coverId, String title, String introduce) { // do not contains times icon.
        this.coverId = coverId;
        this.title = title;
        this.introduce = introduce;
    }

    public Playlist(int coverId, String title, String introduce, long times) { // contains times icon.
        this.coverId = coverId;
        this.title = title;
        this.introduce = introduce;
        this.times = times;
    }

    public Playlist(List<Song> list) { // SONGS
        this.list = list;
    }

    public Playlist(int coverId, String title, boolean isAlbum) { // MINI
        this.coverId = coverId;
        this.title = title;
        this.isAlbum = isAlbum;
    }

    public Playlist(int coverId, String introduce, long times) { // MUSIC
        this.coverId = coverId;
        this.introduce = introduce;
        this.times = times;
    }

    public String getTitle() {
        return title;
    }

    public String getIntroduce() {
        return introduce;
    }

    public User getCreator() {
        return creator;
    }

    public List<Song> getList() {
        return list;
    }

    public int getCoverId() {
        return coverId;
    }

    public long getTimes() {
        return times;
    }

    public boolean isAlbum() {
        return isAlbum;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setList(List<Song> list) {
        this.list = list;
    }

    public void setCoverId(int coverId) {
        this.coverId = coverId;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public void setAlbum(boolean album) {
        isAlbum = album;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
