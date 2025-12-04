package com.example.neteasecloudmusic.ui.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.SingletonPlaylist;
import com.example.neteasecloudmusic.data.model.Song;
import com.example.neteasecloudmusic.service.PlayerForegroundService;
import com.example.neteasecloudmusic.ui.main.MainActivity;
import com.example.neteasecloudmusic.util.Utils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.List;
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

    private Song song;

    public static PlayerFragment newInstanceWithCurrentSong(Song song) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.song = song;
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

        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            playerService = activity.getPlayerService();
            idSongMap = activity.getIdSongMap();
        }
    }

    private PlayerForegroundService playerService;
    private Map<Long, Song> idSongMap;

    private PlayerForegroundService.PlayerCallback playerCallback;

    ViewGroup main;
    TextView heart_num;
    TextView recommend_num;
    TextView name;
    TextView author;
    TextView title;
    ImageView stylus;
    ViewPager2 viewPager;
    ImageView back;
    SeekBar progress;
    TextView now;
    TextView total;
    ImageView isPlay;
    ImageView previous;
    ImageView next;
    ImageView playerMode;
    ImageView playing;

    private CoverAdapter coverAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialSharedAxis enter = new MaterialSharedAxis(MaterialSharedAxis.Y, true);
        enter.setDuration(500);
        MaterialSharedAxis returnTrans = new MaterialSharedAxis(MaterialSharedAxis.Y, false);
        returnTrans.setDuration(500);

        setEnterTransition(enter);
        setReturnTransition(returnTrans);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindView(view);

        initializeWithoutPlayer();

        initializeViewPager();

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
        viewPager = view.findViewById(R.id.view_pager);
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

    ObjectAnimator stylusPlay;
    ObjectAnimator stylusPause;

    private void initializeWithoutPlayer() {
        progress.setProgress(0);

        if (song != null) {
            name.setText(song.getTitle());
            author.setText(song.getAuthor().getUsername());
            now.setText(Utils.formatTime(playerService.getPlayer().getCurrentPosition()));
            total.setText(Utils.formatTime(song.getDuration()));
            if (playerService.getPlayer().isPlaying()) {
                isPlay.setImageResource(R.drawable.player_window_pause);
            } else {
                isPlay.setImageResource(R.drawable.recommend_play);
            }
            progress.setMax((int)song.getDuration());
        }

        stylusPlay = ObjectAnimator.ofFloat(stylus, "rotation", -30f, 0f);
        stylusPlay.setDuration(300);

        stylusPause = ObjectAnimator.ofFloat(stylus, "rotation", 0f, -30f);
        stylusPause.setDuration(300);

        stylus.post(() -> {
            stylus.setPivotX(stylus.getWidth() / 2);
            stylus.setPivotY(stylus.getHeight() / 6);
            stylus.setRotation(-30f);
        }); /* 唱针圆心位置和角度初始化 */

        name.post(() -> name.setSelected(true)); /* 歌曲名的走马灯焦点 */

        back.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack() ); /* 返回键回到 Activity */

        playing.setOnClickListener(v -> {
            PlayingFragment fragment = PlayingFragment.newInstance("", "");
            fragment.show(requireActivity().getSupportFragmentManager(), "PlayingFragment");
        });
    }

    private boolean isUserSeeking = false;

    private void initializeViewPager() {
        List<Song> songs = SingletonPlaylist.getInstance().getLocal().getList();
        coverAdapter = new CoverAdapter(songs);
        viewPager.setAdapter(coverAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private int target = -1;
            private boolean dragging = false;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (isInitial) {
                    isInitial = false;
                    return;
                }
                if (playerService != null && !isUserSeeking) {
                    playerService.getPlayer().seekTo(position, 0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager2.SCROLL_STATE_DRAGGING ||
                        state == ViewPager2.SCROLL_STATE_SETTLING) {
                    dragging = true;
                    if (!stylusPause.isStarted() && playerService.getPlayer().isPlaying()) stylusPause.start();
                }

                if (state == ViewPager2.SCROLL_STATE_IDLE && dragging) {
                    dragging = false;

                    if (target >= 0) {
                        if (!stylusPlay.isStarted()) stylusPlay.start();
                    }
                }
            }
        });

        int index = playerService.getPlayer().getCurrentMediaItemIndex();
        viewPager.setCurrentItem(index, false);

        if (playerService.getPlayer().isPlaying()) {
            ((CoverAdapter)viewPager.getAdapter()).resumeAnimation();
        }
    }

    private boolean isInitial = true;

    private void attachTransaction() {

        isPlay.setOnClickListener(v -> {
            if (playerService.getPlayer().isPlaying()) {
                isPlay.setImageResource(R.drawable.player_window_pause);
                playerService.togglePlay();
            } else {
                isPlay.setImageResource(R.drawable.recommend_play);
                playerService.togglePlay();
            }
        });

        previous.setOnClickListener(v -> {
            playerService.skipToPrevious();
        });

        next.setOnClickListener(v -> {
            playerService.skipToNext();
        });

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerService != null) {
                    long duration = playerService.getPlayer().getDuration();
                    long newPosition = (duration * progress) / seekBar.getMax();
                    now.setText(Utils.formatTime(newPosition));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                if (playerService != null) {
                    long duration = playerService.getPlayer().getDuration();
                    long newPosition = (duration * seekBar.getProgress()) / seekBar.getMax();
                    playerService.getPlayer().seekTo(newPosition);
                    now.setText(Utils.formatTime(newPosition));
                }
            }
        });

        playerCallback  = new PlayerForegroundService.PlayerCallback() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    isPlay.setImageResource(R.drawable.player_window_pause);
                    ((CoverAdapter)viewPager.getAdapter()).resumeAnimation();
                    if (!stylusPlay.isStarted()) stylusPlay.start();
                } else {
                    isPlay.setImageResource(R.drawable.recommend_play);
                    ((CoverAdapter)viewPager.getAdapter()).pauseAnimation();
                    if (!stylusPause.isStarted()) stylusPause.start();
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

                // 同步 ViewPager2 的位置
                int index = playerService.getPlayer().getCurrentMediaItemIndex();
                viewPager.setCurrentItem(index, true);
            }

            @Override
            public void onProgress(long pos, long dur) {
                now.setText(Utils.formatTime(pos));
                if (!isUserSeeking) {
                    int progressValue = dur > 0 ? (int) ((pos * progress.getMax()) / dur) : 0;
                    progress.setProgress(progressValue);
                }
            }

            @Override
            public void onMediaItemRemoved(int index) {
            }
        };

        playerService.registerCallback(playerCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        playerService.unregisterCallback(playerCallback);
    }

    public class CoverAdapter extends RecyclerView.Adapter<CoverAdapter.VH> {

        private final List<Song> images;

        public CoverAdapter(List<Song> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Song song = images.get(position);
            Glide.with(requireContext())
                    .load(song.getCover())
                    .circleCrop()
                    .into(holder.cover);
        }

        @Override
        public int getItemCount() {
            return images != null ? images.size() : 0;
        }

        public void resumeAnimation() {
            VH holder = getCurrentViewHolder();
            if (holder == null) return;

            if (holder.animator.isPaused()) {
                holder.animator.resume();
            }
        }

        public void pauseAnimation() {
            VH holder = getCurrentViewHolder();
            if (holder == null) return;

            if (!holder.animator.isPaused()) {
                holder.animator.pause();
            }
        }

        private CoverAdapter.VH getCurrentViewHolder() {
            RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
            if (recyclerView == null) return null;

            int position = viewPager.getCurrentItem();
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);

            if (holder instanceof CoverAdapter.VH) {
                return (CoverAdapter.VH) holder;
            }
            return null;
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView record;
            ImageView cover;
            ObjectAnimator animator;

            VH(@NonNull View itemView) {
                super(itemView);

                record = itemView.findViewById(R.id.record);
                cover = itemView.findViewById(R.id.cover);

                animator = ObjectAnimator.ofFloat(cover, "rotation", 0f, 360f);
                animator.setDuration(20000);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.start();
                animator.pause();
            }
        }
    }
}

