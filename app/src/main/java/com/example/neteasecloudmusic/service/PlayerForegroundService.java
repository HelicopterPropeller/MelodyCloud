// java
package com.example.neteasecloudmusic.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Playlist;
import com.example.neteasecloudmusic.data.model.SingletonPlaylist;
import com.example.neteasecloudmusic.data.model.Song;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerForegroundService extends Service {

    public interface PlayerCallback {
        void onIsPlayingChanged(boolean isPlaying);
        void onMediaItemTransition(@Nullable MediaItem mediaItem);
        void onProgress(long pos, long dur);
        void onMediaItemRemoved(int index);
    }

    private final IBinder binder = new LocalBinder();
    private ExoPlayer player;
    private final List<PlayerCallback> callbacks = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Executor executorPool = Executors.newSingleThreadExecutor();
    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            if (player != null) {
                long pos = player.getCurrentPosition();
                long dur = player.getDuration();
                for (PlayerCallback cb : callbacks) {
                    cb.onProgress(pos, dur);
                }
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d("PlayerService", "Starting foreground service");
        startForeground(createNotificationId(), buildNotification());

        player = new ExoPlayer.Builder(this).build();
        player.addListener(new com.google.android.exoplayer2.Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                for (PlayerCallback cb : callbacks) cb.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                for (PlayerCallback cb : callbacks) cb.onMediaItemTransition(mediaItem);
            }
        });
        player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ALL);

        executorPool.execute(() -> {
            Playlist local = SingletonPlaylist.getInstance().getLocal();
            final List<MediaItem> items = new ArrayList<>();
            if (local != null && local.getList() != null) {
                for (Song song : local.getList()) {
                    MediaItem mediaItem = new MediaItem.Builder()
                            .setUri(Uri.parse("file://" + song.getPath()))
                            .setMediaId(String.valueOf(song.getId()))
                            .build();
                    items.add(mediaItem);
                }
            }
            handler.post(() -> {
                if (!items.isEmpty()) {
                    player.setMediaItems(items);
                    player.prepare();
                }
                handler.post(updateProgressAction);
            });
        });
    }

    public void play() {
        if (player != null && !player.isPlaying()) player.play();
    }

    public void pause() {
        if (player != null && player.isPlaying()) player.pause();
    }

    public void togglePlay() {
        if (player == null) return;
        if (player.isPlaying()) player.pause(); else player.play();
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void registerCallback(PlayerCallback cb) {
        if (cb != null && !callbacks.contains(cb)) callbacks.add(cb);
    }

    public void unregisterCallback(PlayerCallback cb) {
        if (cb == null) return;
        callbacks.remove(cb);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateProgressAction);
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public PlayerForegroundService getService() {
            return PlayerForegroundService.this;
        }
    }

    private static final String CHANNEL_ID = "player_service_channel";
    private static final int NOTIFICATION_ID = 0x1001;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Player Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("服务运行中")
                .setSmallIcon(R.drawable.ic_launcher_netease)
                .setOngoing(true)
                .build();
    }

    private int createNotificationId() {
        return NOTIFICATION_ID;
    }

    public void skipToNext() {
        if (player == null) return;
        int count = player.getMediaItemCount();
        if (count == 0) return;

        int current = player.getCurrentMediaItemIndex();
        int next = current + 1;
        if (next >= count) {
            next = 0;
        }
        player.seekTo(next, 0);
        player.play();
    }

    public void skipToPrevious() {
        if (player == null) return;
        int count = player.getMediaItemCount();
        if (count == 0) return;

        int current = player.getCurrentMediaItemIndex();
        long pos = player.getCurrentPosition();
        int prev = current - 1;
        if (prev < 0) {
            prev = count - 1;
        }
        player.seekTo(prev, 0);
        player.play();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && "request remove".equals(action)) {
                int index = intent.getIntExtra("remove_index", -1);

                if (player != null && index >= 0 && index < player.getMediaItemCount()) {
                    int currentIndex = player.getCurrentMediaItemIndex();
                    player.removeMediaItem(index);
                    if (index < currentIndex) {
                        player.seekTo(currentIndex - 1, player.getCurrentPosition());
                    }
                }

                for (PlayerCallback cb : callbacks) {
                    cb.onMediaItemRemoved(index);
                }
            }
        }
        return START_NOT_STICKY;
    }
}
