package com.example.neteasecloudmusic.ui.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Song;
import com.example.neteasecloudmusic.service.PlayerForegroundService;
import com.example.neteasecloudmusic.ui.main.MainActivity;
import com.example.neteasecloudmusic.util.Utils;
import com.google.android.exoplayer2.MediaItem;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) { // 或你的 Activity 名称
            MainActivity activity = (MainActivity) context;
            playerService = activity.getPlayerService();
            idSongMap = activity.getIdSongMap();
        }
    }

    private PlayerForegroundService playerService;
    private Map<Long, Song> idSongMap;
    private boolean serviceBound = false;

    ViewGroup main;
    TextView heart_num;
    TextView recommend_num;
    TextView name;
    TextView author;
    TextView title;
    ImageView stylus;
    ImageView cover;
    ImageView back;
    View progress;
    TextView now;
    TextView total;
    ImageView isPlay;
    ImageView previous;
    ImageView next;
    ImageView playerMode;
    ImageView playing;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindView(view);

        attachTransaction();
    }

    private void bindView(View view) {
        main = view.findViewById(R.id.main);
        heart_num = view.findViewById(R.id.heart_num);
        recommend_num = view.findViewById(R.id.recommend_num);
        name = view.findViewById(R.id.song_name);
        author = view.findViewById(R.id.song_author);
        title = view.findViewById(R.id.title);
        stylus = view.findViewById(R.id.stylus);
        cover = view.findViewById(R.id.cover);
        back = view.findViewById(R.id.back);
        progress = view.findViewById(R.id.progress);
        now = view.findViewById(R.id.now);
        total = view.findViewById(R.id.total);
        isPlay = view.findViewById(R.id.play_or_pause);
        previous = view.findViewById(R.id.previous_song);
        next = view.findViewById(R.id.next_song);
        playerMode = view.findViewById(R.id.player_mode);
        playing = view.findViewById(R.id.playing);
    }

    private void attachTransaction() {
        name.post(() -> name.setSelected(true)); /* 歌曲名的走马灯焦点 */

        isPlay.setOnClickListener(v -> {
            if (playerService.getPlayer().isPlaying()) {
                isPlay.setImageResource(R.drawable.player_window_pause);
                playerService.togglePlay();
            } else {
                isPlay.setImageResource(R.drawable.recommend_play);
                playerService.togglePlay();
            }
        });

        back.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack() );

        previous.setOnClickListener(v -> playerService.skipToPrevious());
        next.setOnClickListener(v -> playerService.skipToNext());

        ObjectAnimator animator = ObjectAnimator.ofFloat(cover, "rotation", 0f, 360f);
        animator.setDuration(20000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
        animator.pause();

        playerService.registerCallback(new PlayerForegroundService.PlayerCallback() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    isPlay.setImageResource(R.drawable.player_window_pause);
                    animator.resume();
                } else {
                    isPlay.setImageResource(R.drawable.recommend_play);
                    animator.pause();
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem) {
                if (mediaItem == null) return;
                long id = Long.parseLong(mediaItem.mediaId);
                Song song = idSongMap.get(id);
                name.setText(song.getTitle());
                author.setText(song.getAuthor().getUsername());
                now.setText(Utils.formatTime(0));
                total.setText(Utils.formatTime(song.getDuration()));
                if (song.getCover() != null) {
                    Glide.with(requireContext())
                            .load(song.getCover())
                            .circleCrop()
                            .into(cover);
                }
                animator.start();
            }

            @Override
            public void onProgress(long pos, long dur) {

            }

            @Override
            public void onMediaItemRemoved(int index) {
            }
        });
    }
}