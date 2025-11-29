package com.example.neteasecloudmusic.ui.main;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.example.neteasecloudmusic.service.PlayerForegroundService;
import com.example.neteasecloudmusic.ui.followed.FollowedFragment;
import com.example.neteasecloudmusic.ui.home.HomeFragment;
import com.example.neteasecloudmusic.ui.mine.MineFragment;
import com.google.android.exoplayer2.MediaItem;
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
    private CircularProgressIndicator circularProgressIndicator;
    private ViewFlipper songViewFlipper;

    private final Map<Long, Song> idSongMap = new HashMap<>();

    private PlayerForegroundService playerService;
    private boolean serviceBound = false;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerForegroundService.LocalBinder binder = (PlayerForegroundService.LocalBinder) service;
            playerService = binder.getService();
            serviceBound = true;

            playerService.registerCallback(new PlayerForegroundService.PlayerCallback() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    runOnUiThread(() -> {
                        if (isPlaying) rotationAnimator.resume();
                        else rotationAnimator.pause();
                    });
                }

                @Override
                public void onMediaItemTransition(@Nullable MediaItem mediaItem) {
                    runOnUiThread(() -> {
                        if (mediaItem == null) return;
                        long id = Long.parseLong(mediaItem.mediaId);
                        Song song = idSongMap.get(id);
                        cover.setRotation(0f);
                        if (song != null && song.getCover() != null) {
                            cover.setImageBitmap(song.getCover());
                        } else {
                            cover.setImageResource(R.drawable.user_avatar);
                        }

                        // 同步更新 ViewFlipper 显示内容
                        int index = playerService.getPlayer().getCurrentMediaItemIndex();
                        songViewFlipper.setDisplayedChild(index);
                    });
                }

                @Override
                public void onProgress(long pos, long dur) {
                    runOnUiThread(() -> {
                        if (circularProgressIndicator != null) {
                            int max = circularProgressIndicator.getMax();
                            int progress = 0;
                            if (dur > 0) {
                                progress = (int) (pos * max / dur);
                            }
                            circularProgressIndicator.setProgress(progress, true);
                        }
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            playerService = null;
        }
    };

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

    /**
     * 绑定视图并进行相关配置
     * 包括抽屉、底部导航栏、播放窗口等
     */
    private void bindView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer = findViewById(R.id.side_navigation_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        ViewGroup menuView = (ViewGroup) bottomNavigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View item = menuView.getChildAt(i);
            item.setOnLongClickListener(v -> true);
        }

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
                if (serviceBound && playerService != null) {
                    playerService.togglePlay();

                    if (isPlay) {
                        playOrPause.setImageResource(R.drawable.player_window_pause);
                    } else {
                        playOrPause.setImageResource(R.drawable.player_window_play);
                    }
                    isPlay = !isPlay;
                }
            }
        });

        cover = findViewById(R.id.player_window_cover);

        // 播放窗口歌曲封面旋转动画
        rotationAnimator = ObjectAnimator.ofFloat(cover, "rotation", 0f, 360f);
        rotationAnimator.setDuration(20000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.start();

        circularProgressIndicator = findViewById(R.id.circular_progress_indicator);

        songViewFlipper = findViewById(R.id.song_view_flipper);

        songViewFlipper.setInAnimation(null);
        songViewFlipper.setOutAnimation(null);

        songViewFlipper.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    isDragging = false;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX();
                    if (Math.abs(moveX - downX) > 50) {
                        isDragging = true;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (isDragging) {
                        float upX = event.getX();
                        if (downX - upX > 100) {
                            // 左滑切换下一首歌曲
                            songViewFlipper.setOutAnimation(this, R.anim.slide_out_left);
                            songViewFlipper.setInAnimation(this, R.anim.slide_in_right);
                            songViewFlipper.showNext();
                            playNextSong();
                        } else if (upX - downX > 100) {
                            // 右滑切换上一首歌曲
                            songViewFlipper.setOutAnimation(this, R.anim.slide_out_right);
                            songViewFlipper.setInAnimation(this, R.anim.slide_in_left);
                            songViewFlipper.showPrevious();
                            playPreviousSong();
                        }
                    }
                    return true;
            }
            return false;
        });
    }

    private float downX;
    private boolean isDragging = false;

    /**
     * 前台服务播放下一首歌曲
     */
    private void playNextSong() {
        if (playerService != null) {
            playerService.getPlayer().seekToNext();
        }
    }

    /**
     * 前台服务播放上一首歌曲
     */
    private void playPreviousSong() {
        if (playerService != null) {
            playerService.getPlayer().seekToPrevious();
        }
    }

    /**
     * 底部导航栏的初始化操作
     * 根据选中的 item 切换 Fragment
     */
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
     * 抽屉的初始化操作
     * 做的事情有回调遮罩状态和 back 键优先关闭抽屉
     * 遮罩在抽屉和 ViewPager2 中间来避免滑动抽屉存在滑动冲突
     * 抽屉覆盖期间遮罩吃掉 ACTION_DOWN 防止事件继续向下传播
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
        }); // 设置 back 键优先关闭抽屉
    }

    /**
     * 检查权限状态
     * 没有则请求权限；
     * 有则调用 startQuerySongs 方法开始查询本地歌曲
     */
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

    /**
     * 回调 READ_MEDIA_AUDIO 和 READ_EXTERNAL_STORAGE 权限请求结果
     */
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

    /**
     * 使用 CompletableFuture 异步查询本地音频文件
     * 结果返回主线程后给 ViewFlipper 添加歌曲信息
     */
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

                    // 动态添加歌曲信息到 ViewFlipper
                    if (local != null && local.getList() != null) {
                        for (Song song : local.getList()) {
                            TextView textView = new TextView(this);
                            textView.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            ));
                            textView.setGravity(Gravity.CENTER_VERTICAL);
                            textView.setText(song.getTitle() + " - " + song.getAuthor().getUsername());
                            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            textView.setMarqueeRepeatLimit(-1);
                            textView.setSingleLine(true);
                            textView.setFocusable(true);
                            textView.setFocusableInTouchMode(true);
                            textView.setSelected(true);
                            songViewFlipper.addView(textView);
                        }
                    }

                    Intent intent = new Intent(this, PlayerForegroundService.class);
                    ContextCompat.startForegroundService(this, intent);
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            });
            return null;
        });
    }

    /**
     * 配置发生改变时保存单例状态
     */
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

    /**
     * 解绑和停止前台服务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            if (playerService != null) {
                playerService.unregisterCallback(null);
            }
            unbindService(serviceConnection);
            serviceBound = false;
        }

        Intent intent = new Intent(this, PlayerForegroundService.class);
        stopService(intent);
    }
}
