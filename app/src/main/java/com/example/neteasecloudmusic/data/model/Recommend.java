package com.example.neteasecloudmusic.data.model;

import com.example.neteasecloudmusic.R;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<Recommend> recommendsPageDataSimulation() {

        List<Playlist> top_clear_playlists = new ArrayList<>();
        top_clear_playlists.add(new Playlist(R.drawable.jay_1, "每日推荐", "今日限定好歌推荐"));
        top_clear_playlists.add(new Playlist(R.drawable.jay_2, "漫游", "多样频道无限常听"));
        top_clear_playlists.add(new Playlist(R.drawable.jay_3, "独家策划", "如果只能食用AI音乐，你愿意吗？"));
        top_clear_playlists.add(new Playlist(R.drawable.jay_4, "心动模式", "红心歌曲与相似推荐"));
        top_clear_playlists.add(new Playlist(R.drawable.jay_5, "相似歌曲",  "从你喜欢的歌听起"));

        List<Integer> top_clear_innerTypes = Arrays.asList(
                COLORED_TYPE_ICON, COLORED_TYPE_ICON, COLORED_TYPE_CLEAR, COLORED_TYPE_ICON, COLORED_TYPE_CLEAR);

        Recommend top_clear = new Recommend(null, RECOMMEND_TYPE_COLORED, top_clear_playlists, top_clear_innerTypes, true);

        /* --- */

        List<Song> second_songs_playlists_songs_1 = new ArrayList<>();
        second_songs_playlists_songs_1.add(new Song(R.drawable.jay_6, "夜曲", new User("周杰伦"), true, true));
        second_songs_playlists_songs_1.add(new Song(R.drawable.jay_7, "菊花台", new User("周杰伦"), true, true));
        second_songs_playlists_songs_1.add(new Song(R.drawable.jay_8, "彩虹", new User("周杰伦"), true, true));
        second_songs_playlists_songs_1.add(new Song(R.drawable.jay_9, "给我一首歌的时间", new User("周杰伦"), true, true));
        second_songs_playlists_songs_1.add(new Song(R.drawable.jay_10, "烟花易冷", new User("周杰伦"), true, true));

        List<Song> second_songs_playlists_songs_2 = new ArrayList<>();
        second_songs_playlists_songs_2.add(new Song(R.drawable.jay_11, "琴伤", new User("周杰伦"), true, true));
        second_songs_playlists_songs_2.add(new Song(R.drawable.jay_12, "明明就", new User("周杰伦"), true, true));
        second_songs_playlists_songs_2.add(new Song(R.drawable.jay_13, "算什么男人", new User("周杰伦"), true, true));
        second_songs_playlists_songs_2.add(new Song(R.drawable.jay_14, "告白气球", new User("周杰伦"), true, true));
        second_songs_playlists_songs_2.add(new Song(R.drawable.jay_15, "最伟大的作品", new User("周杰伦"), true, true));

        List<Playlist> second_songs_playlists = new ArrayList<>();
        second_songs_playlists.add(new Playlist(second_songs_playlists_songs_1));
        second_songs_playlists.add(new Playlist(second_songs_playlists_songs_2));

        List<Integer> second_songs_innerTypes = Arrays.asList(
                SONGS_TYPE, SONGS_TYPE);

        Recommend second_songs = new Recommend("根据你喜爱的歌曲推荐", RECOMMEND_TYPE_SONGS, second_songs_playlists, second_songs_innerTypes, false);

        /* --- */

        List<Playlist> comment_with_times_playlists = new ArrayList<>();
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_1, "私人雷达", "今天从《飞机场的10:30》听起", 23_180_000_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_2, "时光雷达", "听你爱的《小镇姑娘》", 1_380_000_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_3, "怀旧精选", "从《黑色柳丁》", 140_000_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_4, "华语雷达", "陶喆DavidTao的歌，总令人心情愉悦", 550_000_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_5, "云村雷达", "音乐合伙人私藏《今天你要嫁给我》", 2_450_000_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_6, "新歌雷达", "DavidTao新歌快递请查收", 2_986_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_7, "私人雷达", "黑胶专属从《那个女孩》开始听", 560_000_000l));
        comment_with_times_playlists.add(new Playlist(R.drawable.david_tao_8, "宝藏雷达", "宝藏雷达", 740_000_000l));

        List<Integer> comment_with_times_innerTypes = Arrays.asList(
                COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES, COMMENT_TYPE_TIMES);

        Recommend comment_with_times = new Recommend("我先矛顿的雷达歌单", RECOMMEND_TYPE_COMMENT, comment_with_times_playlists, comment_with_times_innerTypes, false);

        /* --- */

        List<Playlist> mini_playlists = new ArrayList<>();
        mini_playlists.add(new Playlist(R.drawable.khalil_1, "SoulBoy", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_2, "爱爱爱", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_3, "未来", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_4, "橙月", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_5, "Timeless", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_6, "15", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_7, "回到未来", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_8, "危险世界", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_9, "JTW西游记", true));
        mini_playlists.add(new Playlist(R.drawable.khalil_10, "梦想家", true));

        List<Integer> mini_innerTypes = Arrays.asList(
                MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE, MINI_TYPE);

        Recommend mini = new Recommend("最近常听", RECOMMEND_TYPE_MINI, mini_playlists, mini_innerTypes, false);

        /* --- */

        List<Recommend> result = new ArrayList<>();
        result.add(top_clear);
        result.add(second_songs);
        result.add(comment_with_times);
        result.add(mini);

        return result;
    }

    public static List<Recommend> musicPageDataSimulation() {
        List<Playlist> carouselImages = new ArrayList<>();
        carouselImages.add(new Playlist(R.drawable.jay_1));
        carouselImages.add(new Playlist(R.drawable.jay_2));
        carouselImages.add(new Playlist(R.drawable.jay_3));
        carouselImages.add(new Playlist(R.drawable.jay_4));
        carouselImages.add(new Playlist(R.drawable.jay_5));

        Recommend carousel = new Recommend(null, RECOMMEND_TYPE_CAROUSEL, carouselImages, null, true);
        Recommend expand = new Recommend(null, RECOMMEND_TYPE_EXPAND, null, null, true);
        Recommend pages = new Recommend(null, RECOMMEND_TYPE_PAGES, null, null, true);

        /* --- */

        List<Playlist> music_playlists = new ArrayList<>();
        music_playlists.add(new Playlist(R.drawable.david_tao_1, "[复古DANCE FLOOR]来点骚气摇摆旋律", 966_000l));
        music_playlists.add(new Playlist(R.drawable.david_tao_2, "岁月流金|那些永不过时的R&B经典", 330_000l));
        music_playlists.add(new Playlist(R.drawable.david_tao_3, "写作业不分心 深度沉浸式学习", 2_249_000l));
        music_playlists.add(new Playlist(R.drawable.david_tao_4, "R&B·jazz·微醺/慵懒/落日咖啡店", 699_000l));
        music_playlists.add(new Playlist(R.drawable.david_tao_5, "《精 选 国 产 摇 滚 乐 队》", 1_631_000l));
        music_playlists.add(new Playlist(R.drawable.david_tao_6, "欧美rap|节奏与歌词的摩擦碰撞", 1_073_000l));

        List<Integer> music_innerTypes = Arrays.asList(
                MUSIC_TYPE_TIMES, MUSIC_TYPE_TIMES, MUSIC_TYPE_TIMES, MUSIC_TYPE_TIMES, MUSIC_TYPE_TIMES, MUSIC_TYPE_TIMES);

        Recommend music = new Recommend("甄选歌单", RECOMMEND_TYPE_MUSIC, music_playlists, music_innerTypes, false);

        /* --- */

        List<Song> songs_playlists_songs_1 = new ArrayList<>();
        songs_playlists_songs_1.add(new Song(R.drawable.jay_6, "夜曲", new User("周杰伦"), true, true));
        songs_playlists_songs_1.add(new Song(R.drawable.jay_7, "菊花台", new User("周杰伦"), true, true));
        songs_playlists_songs_1.add(new Song(R.drawable.jay_8, "彩虹", new User("周杰伦"), true, true));
        songs_playlists_songs_1.add(new Song(R.drawable.jay_9, "给我一首歌的时间", new User("周杰伦"), true, true));
        songs_playlists_songs_1.add(new Song(R.drawable.jay_10, "烟花易冷", new User("周杰伦"), true, true));

        List<Song> songs_playlists_songs_2 = new ArrayList<>();
        songs_playlists_songs_2.add(new Song(R.drawable.jay_11, "琴伤", new User("周杰伦"), true, true));
        songs_playlists_songs_2.add(new Song(R.drawable.jay_12, "明明就", new User("周杰伦"), true, true));
        songs_playlists_songs_2.add(new Song(R.drawable.jay_13, "算什么男人", new User("周杰伦"), true, true));
        songs_playlists_songs_2.add(new Song(R.drawable.jay_14, "告白气球", new User("周杰伦"), true, true));
        songs_playlists_songs_2.add(new Song(R.drawable.jay_15, "最伟大的作品", new User("周杰伦"), true, true));

        List<Playlist> songs_playlists = new ArrayList<>();
        songs_playlists.add(new Playlist(songs_playlists_songs_1));
        songs_playlists.add(new Playlist(songs_playlists_songs_2));

        List<Integer> songs_innerTypes = Arrays.asList(
                SONGS_TYPE, SONGS_TYPE);

        Recommend songs = new Recommend("根据你喜爱的歌曲推荐", RECOMMEND_TYPE_SONGS, songs_playlists, songs_innerTypes, false);


        /* --- */

        List<Recommend> result = new ArrayList<>();
        result.add(carousel);
        result.add(expand);
        result.add(pages);
        result.add(music);
        result.add(songs);

        return result;
    }

//    public static Recommend minePageDataSimulation() {
//
//    }
}
