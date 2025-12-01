package com.example.neteasecloudmusic.ui.player;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.SingletonPlaylist;
import com.example.neteasecloudmusic.data.model.Song;
import com.example.neteasecloudmusic.service.PlayerForegroundService;

import java.util.List;

public class PlayingCurrentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public PlayingCurrentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayingCurrentFragment newInstance(String param1, String param2) {
        PlayingCurrentFragment fragment = new PlayingCurrentFragment();
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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.playing_current, container, false);
    }

    RecyclerView songs;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songs = view.findViewById(R.id.current_songs);

        songs.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        songs.setAdapter(new SongAdapter(SingletonPlaylist.getInstance().getLocal().getList()
                , getParentFragment() != null ? getParentFragment() : this));
    }

    public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

        List<Song> list;
        Fragment parentFragment;

        public SongAdapter(List<Song> list, Fragment parentFragment) {
            this.list = list;
            this.parentFragment = parentFragment;
        }

        @NonNull
        @Override
        public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playing_song, parent, false);
            return new SongHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongHolder holder, int position) {
            Song song = list.get(position);
            if (song.isVip() != null && !song.isVip()) {
                holder.vip.setVisibility(View.GONE);
            }

            String text = song.getTitle() + " · " + song.getAuthor().getUsername();
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, song.getTitle().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics())),
                    0, song.getTitle().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#7E8386")), song.getTitle().length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.information.setText(spannableString);

            holder.delete.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                list.remove(pos);

                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, Math.max(0, list.size() - pos));

                Intent intent = new Intent(holder.itemView.getContext(), PlayerForegroundService.class);
                intent.setAction("request remove");
                intent.putExtra("remove_index", pos);
                holder.itemView.getContext().startService(intent);

                if (parentFragment instanceof PlayingFragment) {
                    ((PlayingFragment) parentFragment).onPlaylistChanged();
                } else {
                    Fragment p = parentFragment.getParentFragment();
                    if (p instanceof PlayingFragment) {
                        ((PlayingFragment) p).onPlaylistChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class SongHolder extends RecyclerView.ViewHolder {

            TextView vip;
            TextView information;
            ImageView delete;

            public SongHolder(@NonNull View itemView) {
                super(itemView);

                vip = itemView.findViewById(R.id.playing_vip);
                information = itemView.findViewById(R.id.playing_song);
                delete = itemView.findViewById(R.id.playing_delete);
            }
        }

    }
}