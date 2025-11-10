package com.example.neteasecloudmusic.data.model;

import java.util.List;

public class Recommend {

    // outerType
    public static final int RECOMMEND_TYPE_COLORED = 0;
    public static final int RECOMMEND_TYPE_COMMENT = 1;
    public static final int RECOMMEND_TYPE_MINI = 2;
    public static final int RECOMMEND_TYPE_SONGS = 3;
    public static final int RECOMMEND_TYPE_MUSIC = 4;
    public static final int RECOMMEND_TYPE_CAROUSEL = 5;
    public static final int RECOMMEND_TYPE_EXPAND = 6;
    public static final int RECOMMEND_TYPE_PAGES = 7;

    // innerType
    public static final int COLORED_TYPE_ICON = 0; // 日推、漫游或心动
    public static final int COLORED_TYPE_CLEAR = 1;
    public static final int COMMENT_TYPE_TIMES = 2; // 有听歌次数标识
    public static final int COMMENT_TYPE_CLEAR = 3;
    public static final int MINI_TYPE = 4;
    public static final int SONGS_TYPE = 5;
    public static final int MUSIC_TYPE_TIMES = 6; // 有听歌次数标识
    public static final int MUSIC_TYPE_CLEAR = 7;
    public static final int IRRELEVANT = 8;

    private String title;
    private int outerType;
    private List<Playlist> playlists;
    private List<Integer> innerTypes;
    private boolean isClear;

    public Recommend(String title, int outerType, List<Playlist> playlists, List<Integer> innerTypes, boolean isClear) {
        this.title = title;
        this.outerType = outerType;
        this.playlists = playlists;
        this.innerTypes = innerTypes;
        this.isClear = isClear;
    }

    public String getTitle() {
        return title;
    }

    public int getOuterType() {
        return outerType;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public List<Integer> getInnerTypes() {
        return innerTypes;
    }

    public boolean isClear() {
        return isClear;
    }
}
