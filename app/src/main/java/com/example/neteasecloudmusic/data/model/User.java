package com.example.neteasecloudmusic.data.model;

import java.util.List;

public class User {

    private String username;
    private boolean isArtist;
    private List<Playlist> createAlbums; // isArtist == true;
    private List<Playlist> collectPlaylists;
    private List<Song> favoriteSongs;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, boolean isArtist, List<Playlist> createAlbums, List<Playlist> collectPlaylists, List<Song> favoriteSongs) {
        this.username = username;
        this.isArtist = isArtist;
        this.createAlbums = createAlbums;
        this.collectPlaylists = collectPlaylists;
        this.favoriteSongs = favoriteSongs;
    }

    public String getUsername() {
        return username;
    }

    public boolean isArtist() {
        return isArtist;
    }

    public List<Playlist> getCreateAlbums() {
        return createAlbums;
    }

    public List<Playlist> getCollectPlaylists() {
        return collectPlaylists;
    }

    public List<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setArtist(boolean artist) {
        isArtist = artist;
    }

    public void setCreateAlbums(List<Playlist> createAlbums) {
        this.createAlbums = createAlbums;
    }

    public void setCollectPlaylists(List<Playlist> collectPlaylists) {
        this.collectPlaylists = collectPlaylists;
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }
}
