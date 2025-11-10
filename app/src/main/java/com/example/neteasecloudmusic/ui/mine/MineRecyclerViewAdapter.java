package com.example.neteasecloudmusic.ui.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Playlist;

import java.util.List;
import java.util.StringJoiner;

public class MineRecyclerViewAdapter extends RecyclerView.Adapter<MineRecyclerViewAdapter.Holder> {

    private List<Playlist> list;

    public MineRecyclerViewAdapter(List<Playlist> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mine_inner_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView name;
        TextView information;
        LinearLayout favoriteButton;
        ImageView sideButton;
        ImageView record;
        ImageView specialIcon;

        public Holder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            name = itemView.findViewById(R.id.name);
            information = itemView.findViewById(R.id.information);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            sideButton = itemView.findViewById(R.id.inner_expand);
            record = itemView.findViewById(R.id.record);
            specialIcon = itemView.findViewById(R.id.special_icon);
        }

        public void onBind(Playlist playlist) {
            cover.setImageResource(playlist.getCoverId());
            name.setText(playlist.getTitle());

            StringJoiner sj = new StringJoiner("·");

            if (playlist.isAlbum()) {
                record.setVisibility(View.VISIBLE);
                sideButton.setVisibility(View.GONE);
                favoriteButton.setVisibility(View.GONE);
                specialIcon.setVisibility(View.GONE);
                sj.add("专辑");
            } else {
                record.setVisibility(View.GONE);
                sideButton.setVisibility(View.VISIBLE);
                favoriteButton.setVisibility(View.GONE);
                specialIcon.setVisibility(View.GONE);
                sj.add("歌单");
            }

            if (playlist.getTitle().equals("我喜欢的音乐")) {
                sideButton.setVisibility(View.GONE);
                favoriteButton.setVisibility(View.VISIBLE);
                specialIcon.setVisibility(View.VISIBLE);
                specialIcon.setImageResource(R.drawable.recommend_heart);
                sj = new StringJoiner("·");
                sj.add(playlist.getList().size() + "首").add(playlist.getCreator().getUsername());
                information.setText(sj.toString());
            } else if (playlist.getTitle().equals("听歌排行")) {
                specialIcon.setVisibility(View.VISIBLE);
                specialIcon.setImageResource(R.drawable.expand_rankings);
                sideButton.setImageResource(R.drawable.mine_fix);
                information.setText("累计听歌" + playlist.getTimes() + "首");
            } else {
                sj.add(playlist.getList().size() + "首").add(playlist.getCreator().getUsername());
                information.setText(sj.toString());
            }
        }
    }
}
