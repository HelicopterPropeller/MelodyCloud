package com.example.neteasecloudmusic.ui.home;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Playlist;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.Holder> {

    private final List<Playlist> list;

    public CarouselAdapter(List<Playlist> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carousel_image, parent, false);
        return new Holder(view);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int realPosition = position % list.size();
        holder.imageView.setImageResource(list.get(realPosition).getCoverId());
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image);
        }
    }
}
