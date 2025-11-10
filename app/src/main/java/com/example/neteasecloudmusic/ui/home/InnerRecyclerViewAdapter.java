package com.example.neteasecloudmusic.ui.home;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.*;

import java.util.List;

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.Holder> {

    private final List<Playlist> list;
    private final int type;

    public interface OnItemClickListener {
        void onItemClick(Playlist item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public InnerRecyclerViewAdapter(List<Playlist> list, int type) {
        this.list = list;
        this.type = type;
    }

    public void updateData(List<Playlist> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (type) {
            case Recommend.COLORED_TYPE_ICON:
            case Recommend.COLORED_TYPE_CLEAR:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.inner_item_colored, parent, false);
                return new ColoredHolder(view);
            case Recommend.COMMENT_TYPE_TIMES:
            case Recommend.COMMENT_TYPE_CLEAR:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.inner_item_comment, parent, false);
                return new CommentHolder(view);
            case Recommend.MINI_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.inner_item_mini, parent, false);
                return new MiniHolder(view);
            case Recommend.SONGS_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.inner_item_song, parent, false);
                return new SongHolder(view);
            case Recommend.MUSIC_TYPE_TIMES:
            case Recommend.MUSIC_TYPE_CLEAR:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.inner_item_music, parent, false);
        }
        return new MusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Playlist playlist = list.get(position);
        holder.onBind(playlist);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(playlist);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public abstract class Holder extends RecyclerView.ViewHolder {

        final Runnable runnable = () -> {
            if (itemView.isPressed()) {
                Animator press = AnimatorInflater.loadAnimator(itemView.getContext(), R.animator.home_item_press);
                press.setTarget(itemView);
                press.start();
            }
        };
        Animator recover = AnimatorInflater.loadAnimator(itemView.getContext(), R.animator.home_item_recover);

        public Holder(@NonNull View itemView) {
            super(itemView);

            if (type != Recommend.SONGS_TYPE) {
                itemView.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.postDelayed(runnable, 200);
                            v.setPressed(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setPressed(false);
                            v.removeCallbacks(runnable);
                            recover.setTarget(v);
                            recover.start();
                            break;
                    }
                    return false;
                });
            }
        }
        public abstract void onBind(Playlist playlist);
    }

    private class ColoredHolder extends Holder {

        ImageView cover;
        TextView introduce;
        ImageView icon;
        TextView title;

        public ColoredHolder(@NonNull View itemView) {
            super(itemView);

            cover = itemView.findViewById(R.id.colored_cover);
            introduce = itemView.findViewById(R.id.colored_introduce);
            icon = itemView.findViewById(R.id.colored_icon);
            title = itemView.findViewById(R.id.colored_title);
        }

        @Override
        public void onBind(Playlist playlist) {
            cover.setImageResource(playlist.getCoverId());
            introduce.setText(playlist.getIntroduce());
            title.setText(playlist.getTitle());

            if (type == Recommend.COLORED_TYPE_ICON) {
                icon.setVisibility(View.VISIBLE);
                switch (playlist.getTitle()) {
                    case "每日推荐":
                        icon.setImageResource(R.drawable.recommend_everyday);
                        break;
                    case "漫游":
                        icon.setImageResource(R.drawable.recommend_roaming);
                        break;
                    case "心动模式":
                        icon.setImageResource(R.drawable.recommend_heartbeat);
                        break;
                }
            } else {
                icon.setVisibility(View.GONE);
            } // 硬解码
        }
    }

    private class CommentHolder extends Holder {

        ImageView cover;
        ImageView icon;
        TextView val;
        TextView title;
        TextView introduce;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.comment_cover);
            icon = itemView.findViewById(R.id.music_times_icon);
            val = itemView.findViewById(R.id.music_times_val);
            title = itemView.findViewById(R.id.comment_title);
            introduce = itemView.findViewById(R.id.comment_introduce);
        }

        @Override
        public void onBind(Playlist playlist) {
            cover.setImageResource(playlist.getCoverId());
            title.setText(playlist.getTitle());
            introduce.setText(playlist.getIntroduce());

            if (type == Recommend.COMMENT_TYPE_TIMES) {
                icon.setVisibility(View.VISIBLE);
                val.setVisibility(View.VISIBLE);
                initCommentTimes(playlist.getTimes());
            } else {
                icon.setVisibility(View.GONE);
                val.setVisibility(View.GONE);
            }
        }

        private void initCommentTimes(long times) {
            if (times / 100_000_000L >= 1) {
                double valuation = Math.round((double) times / 100_000_000L * 10.0) / 10.0;
                this.val.setText(valuation + " 亿");
            } else if (times / 10_000L >= 1) {
                double valuation = Math.round((double) times / 10_000L * 10.0) / 10.0;
                this.val.setText(valuation + " 万");
            } else {
                this.val.setText(Long.toString(times));
            }
        }
    }

    private class MiniHolder extends Holder {

        ImageView cover;
        TextView title;
        TextView type;

        public MiniHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.mini_cover);
            title = itemView.findViewById(R.id.mini_title);
            type = itemView.findViewById(R.id.mini_type);
        }

        @Override
        public void onBind(Playlist playlist) {
            cover.setImageResource(playlist.getCoverId());
            title.setText(playlist.getTitle());

            if (playlist.isAlbum()) {
                type.setText("专辑");
            } else {
                type.setText("歌单");
            }
        }
    }

    private class SongHolder extends Holder {

        private final ImageView[] covers = new ImageView[3];
        private final TextView[] names = new TextView[3];
        private final ImageView[] likes = new ImageView[3];
        private final TextView[] signs = new TextView[3];
        private final TextView[] authors = new TextView[3];

        public SongHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnTouchListener(null);

            for (int i = 0; i < 3; ++i) {
                ViewGroup song = itemView.findViewById(getSongLayoutId(i));

                Runnable runnable = () -> {
                    if (song.isPressed()) {
                        Animator press = AnimatorInflater.loadAnimator(song.getContext(), R.animator.home_item_press);
                        press.setTarget(song);
                        press.start();
                    }
                };
                Animator recover = AnimatorInflater.loadAnimator(song.getContext(), R.animator.home_item_recover);

                song.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.postDelayed(runnable, 300);
                            v.setPressed(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setPressed(false);
                            v.removeCallbacks(runnable);
                            recover.setTarget(v);
                            recover.start();
                            break;
                    }
                    return true;
                });

                covers[i] = song.findViewById(R.id.song_cover);
                names[i] = song.findViewById(R.id.song_name);
                likes[i] = song.findViewById(R.id.song_user_like);
                signs[i] = song.findViewById(R.id.song_sign);
                authors[i] = song.findViewById(R.id.song_author);
            }
        }

        private int getSongLayoutId(int index) {
            switch (index) {
                case 0: return R.id.song_1;
                case 1: return R.id.song_2;
                case 2: return R.id.song_3;
                default: throw new IllegalArgumentException("Invalid song index: " + index);
            }
        }

        @Override
        public void onBind(Playlist playlist) { // 只有 3 首歌的歌单
            for (int i = 0; i < 3; ++i) {
                Song song = playlist.getList().get(i);

                covers[i].setImageResource(song.getCoverId());
                names[i].setText(song.getTitle());
                if (!song.isLike()) likes[i].setVisibility(View.GONE);
                if (!song.isVip()) signs[i].setVisibility(View.GONE);
                authors[i].setText(song.getAuthor().getUsername());
            }
        }
    }

    private class MusicHolder extends Holder {

        ImageView cover;
        TextView title;
        ImageView icon;
        TextView val;

        public MusicHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.music_cover);
            title = itemView.findViewById(R.id.music_title);
            icon = itemView.findViewById(R.id.music_times_icon);
            val = itemView.findViewById(R.id.music_times_val);
        }

        @Override
        public void onBind(Playlist playlist) {
            cover.setImageResource(playlist.getCoverId());
            title.setText(playlist.getTitle());

            if (type == Recommend.MUSIC_TYPE_CLEAR) {
                icon.setVisibility(View.GONE);
                val.setVisibility(View.GONE);
            } else {
                icon.setVisibility(View.VISIBLE);
                val.setVisibility(View.VISIBLE);
                initMusicTimes(playlist.getTimes());
            }
        }

        private void initMusicTimes(long times) {
            if (times / 100_000_000L >= 1) {
                double valuation = Math.round((double) times / 100_000_000L * 10.0) / 10.0;
                this.val.setText(valuation + " 亿");
            } else if (times / 10_000L >= 1) {
                double valuation = Math.round((double) times / 10_000L * 10.0) / 10.0;
                this.val.setText(valuation + " 万");
            } else {
                this.val.setText(Long.toString(times));
            }
        }
    }
}
