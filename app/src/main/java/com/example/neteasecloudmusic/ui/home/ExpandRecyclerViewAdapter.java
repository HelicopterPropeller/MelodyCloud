package com.example.neteasecloudmusic.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasecloudmusic.R;

public class ExpandRecyclerViewAdapter extends RecyclerView.Adapter<ExpandRecyclerViewAdapter.Holder> {

    public ExpandRecyclerViewAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ExpandRecyclerViewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_first, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_second, parent, false);
        }
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
//        View itemView = holder.itemView;
//
//        if (position == 0) {
//            itemView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    Utils.dpToPx(holder.itemView.getContext(), 109)));
//        } else {
//            itemView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    Utils.dpToPx(holder.itemView.getContext(), 289)));
//        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
