package com.example.neteasecloudmusic.ui.main;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Playlist;
import com.example.neteasecloudmusic.data.model.SingletonPlaylist;
import com.example.neteasecloudmusic.data.model.SingletonUser;
import com.example.neteasecloudmusic.data.model.Song;
import com.example.neteasecloudmusic.data.model.User;
import com.example.neteasecloudmusic.ui.followed.FollowedFragment;
import com.example.neteasecloudmusic.ui.home.HomeFragment;
import com.example.neteasecloudmusic.ui.mine.MineFragment;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnOpenDrawerListener {

    private SingletonUser singleInstance;
    private User self;
    private SingletonPlaylist singletonPlaylist;
    private Playlist local;

    private final Executor executorPool = Executors.newSingleThreadExecutor();

    private DrawerLayout drawerLayout;
    private ViewGroup drawer;
    private BottomNavigationView bottomNavigation;
    private ViewGroup sideNavigationContentContainer;
    private View mask;
    private ImageView playOrPause;
    private ImageView cover;
    private ObjectAnimator rotationAnimator;
    private TextView currentSongInformation;
    private CircularProgressIndicator circularProgressIndicator;

    private ExoPlayer player;
    private DefaultDataSource.Factory dataSourceFactory;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private final Map<Long, Song> idSongMap = new HashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance("", ""))
                .commit();

        bindView();

        initBottomNavigation();

        initDrawerLayout();

        // 让play_window的textView获得焦点以持续回马灯效果
        findViewById(R.id.textView).setSelected(true);

        if (singleInstance == null) {
            singleInstance = SingletonUser.getInstance();
            self = singleInstance.getSelf();

            SharedPreferences sp = getSharedPreferences("self", MODE_PRIVATE);
            self.setUsername(sp.getString("username", "我先矛顿"));
            self.setArtist(sp.getBoolean("isArtist", false));
        }

        if (singletonPlaylist == null) {
            singletonPlaylist = SingletonPlaylist.getInstance();
            local = singletonPlaylist.getLocal();
        }

        checkSongsPermission();
    }

    public void openDrawer() {
        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void bindView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer = findViewById(R.id.side_navigation_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        ViewGroup menuView = (ViewGroup) bottomNavigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View item = menuView.getChildAt(i);
            item.setOnLongClickListener(v -> true);
        } // 在这里要加入点击触发下拉刷新的逻辑

        sideNavigationContentContainer = drawer.findViewById(R.id.content_container);

        List<View> sideNavigationItems = new ArrayList<>();
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.message));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.shell));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.dressing_center));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.creator_center));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.recently_played));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.scheduled_playback));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.mall));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.ticket_office));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.recommend_songs));
        sideNavigationItems.add(sideNavigationContentContainer.findViewById(R.id.customer_service));
        sideNavigationItems.add(drawer.findViewById(R.id.setting_content));
        sideNavigationItems.add(drawer.findViewById(R.id.more_content));

        for (View item : sideNavigationItems) {
            item.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator down =  ObjectAnimator.ofFloat(v, "alpha", v.getAlpha(), 0.5f);
                        down.setDuration(50);
                        down.setInterpolator(new AccelerateInterpolator());
                        down.start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ObjectAnimator up =  ObjectAnimator.ofFloat(v, "alpha", v.getAlpha(), 1f);
                        up.setDuration(50);
                        up.setInterpolator(new DecelerateInterpolator());
                        up.start();
                        break;
                }
                return v.performClick();
            });

            item.setOnClickListener(v -> {});
        }

        mask = findViewById(R.id.mask);

        playOrPause = findViewById(R.id.player_window_play_or_pause);
        playOrPause.setOnClickListener(new View.OnClickListener() {
            boolean isPlay = true;

            @Override
            public void onClick(View v) {
                if (player != null) {
                    if (isPlay) {
                        playOrPause.setImageResource(R.drawable.player_window_pause);
                        if (!player.isPlaying()) player.play();
                    } else {
                        playOrPause.setImageResource(R.drawable.player_window_play);
                        if (player.isPlaying()) player.pause();
                    }
                    isPlay = !isPlay;
                }
            }
        });

        cover = findViewById(R.id.player_window_cover);

        rotationAnimator = ObjectAnimator.ofFloat(cover, "rotation", 0f, 360f);
        rotationAnimator.setDuration(20000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.start();

        currentSongInformation = findViewById(R.id.textView);

        circularProgressIndicator = findViewById(R.id.circular_progress_indicator);
    }

    private void initBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.home) {
                selectedFragment = HomeFragment.newInstance("", "");
            } else if (item.getItemId() == R.id.followed) {
                selectedFragment = FollowedFragment.newInstance("", "");
            } else if (item.getItemId() == R.id.mine) {
                selectedFragment = MineFragment.newInstance("", "");
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, Objects.requireNonNull(selectedFragment))
                    .commit();
            return true;
        });
    }

    /**
     * mask夹在drawer和ViewPager2中间以防止滑动drawer存在滑动冲突
     * 在drawer存在期间让mask吃掉ACTION_DOWN防止继续向下传播
     */
    private void initDrawerLayout() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (slideOffset > 0) {
                    mask.setVisibility(View.VISIBLE);
                } else {
                    mask.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mask.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mask.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    MainActivity.super.onBackPressed();
                }
            }
        }); // 设置返回键优先关闭抽屉
    }

    private void checkSongsPermission() {
        String[] permissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}
                : new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};

        boolean hasPermission = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }

        if (hasPermission) {
            startQuerySongs();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startQuerySongs();
            } else {
                runOnUiThread(() -> Toast.makeText(this, "需要权限以访问本地音频文件", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void startQuerySongs() {
        CompletableFuture<List<Song>> queryFuture = CompletableFuture.supplyAsync(() -> {
            List<Song> result = new ArrayList<>();

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
            };

            String selection = MediaStore.Audio.Media.DURATION + " > ?";
            String[] selectionArgs = {"1316"};

            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            Cursor cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );

            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                        String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(path);
                        byte[] art = retriever.getEmbeddedPicture();

                        Bitmap bitmap = null;
                        if (art != null) {
                            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                        }

                        Song song = new Song(id, path, title, author, duration, album, bitmap);
                        result.add(song);
                        idSongMap.put(id, song);
                    }
                } finally {
                    cursor.close();
                }
            }
            return result;
        }, executorPool);

        queryFuture.handle((list, throwable) -> {
            runOnUiThread(() -> {
                if (throwable != null) {
                    Toast.makeText(this, "查询失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    local.setList(list);
                    Log.d("fuck", list.toString());
                    onInitPlayer();
                }
            });
            return null;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (singleInstance == null) {
            singleInstance = SingletonUser.getInstance();
            self = singleInstance.getSelf();
        }

        if (singletonPlaylist == null) {
            singletonPlaylist = SingletonPlaylist.getInstance();
            local = singletonPlaylist.getLocal();
        }
    }

    private void onInitPlayer() {
        player = new ExoPlayer.Builder(this).build();
        dataSourceFactory = new DefaultDataSource.Factory(this);
        concatenatingMediaSource = new ConcatenatingMediaSource();

        for (Song song : local.getList()) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.parse("file://" + song.getPath()))
                    .setMediaId(String.valueOf(song.getId()))
                    .build();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            concatenatingMediaSource.addMediaSource(mediaSource);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    rotationAnimator.resume();
                } else {
                    rotationAnimator.pause();
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                cover.setRotation(0f);

                long id = Long.parseLong(mediaItem.mediaId);
                Song song = idSongMap.get(id);
                if (song.getCover() != null) {
                    cover.setImageBitmap(idSongMap.get(id).getCover());
                } else {
                    cover.setImageResource(R.drawable.user_avatar);
                }
                currentSongInformation.setText(song.getTitle() + " -" + song.getAuthor().getUsername());
            }
        });

        player.setRepeatMode(Player.REPEAT_MODE_ALL); // 列表循环播放
        player.setMediaSource(concatenatingMediaSource);
        player.prepare();

        int nextIndex = player.getNextMediaItemIndex();
        if (nextIndex != C.INDEX_UNSET) {
            MediaItem nextItem = player.getMediaItemAt(nextIndex);
            long id = Long.parseLong(nextItem.mediaId);
            if (idSongMap.containsKey(id) && idSongMap.get(id).getCover() != null) {
                cover.setImageBitmap(idSongMap.get(id).getCover());
            } else {
                cover.setImageResource(R.drawable.user_avatar);
            }
        }

        if (!player.isPlaying()) {
            cover.setRotation(0f);
            rotationAnimator.pause();
        }

        Runnable updateProgressAction = new Runnable() {
            @Override
            public void run() {
                long pos = player.getCurrentPosition();
                long dur = player.getDuration();

                if (circularProgressIndicator != null) {
                    int max = circularProgressIndicator.getMax();
                    int progress = (int)(pos * max / dur);
                    circularProgressIndicator.setProgress(progress, true);
                }

                handler.postDelayed(this, 16);
            }
        };

        handler.post(updateProgressAction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}