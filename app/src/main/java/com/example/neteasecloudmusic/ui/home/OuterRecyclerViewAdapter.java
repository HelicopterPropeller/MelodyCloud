package com.example.neteasecloudmusic.ui.home;

import static com.example.neteasecloudmusic.data.model.Recommend.*;

import android.graphics.Rect;
import android.util.Log;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Playlist;
import com.example.neteasecloudmusic.data.model.Recommend;
import com.example.neteasecloudmusic.ui.widget.DotView;
import com.example.neteasecloudmusic.ui.widget.InnerRecyclerView;
import com.example.neteasecloudmusic.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OuterRecyclerViewAdapter extends RecyclerView.Adapter<OuterRecyclerViewAdapter.Holder> {

    private final List<Recommend> list;

    public interface OnInnerItemClickListener {
        void onInnerItemClick(Playlist item);
    }
    private OnInnerItemClickListener innerItemClickListener;

    public void setOnInnerItemClickListener(OnInnerItemClickListener listener) {
        this.innerItemClickListener = listener;
    }

    public OuterRecyclerViewAdapter(List<Recommend> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getOuterType();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_outer_item, parent, false);

        switch (viewType) {
            case Recommend.RECOMMEND_TYPE_CAROUSEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_outer_carousel, parent, false);
                return new CarouselHolder(view);
            case RECOMMEND_TYPE_EXPAND:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_outer_expand, parent, false);
                return new ExpandHolder(view);
            case RECOMMEND_TYPE_PAGES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_outer_pages, parent, false);
                return new PagesHolder(view);
        }

        return new RecommendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Recommend recommend = list.get(position);
        holder.onBind(recommend, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private float progress = 0f;

    public void setExpandScrollProgress(float progress) {
        this.progress = progress;
    }

    public abstract class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
        public abstract void onBind(Recommend recommend, int position);
}

    public class RecommendHolder extends Holder {
        TextView introduce;
        ImageView expand;
        InnerRecyclerView content;

        public RecommendHolder(@NonNull View itemView) {
            super(itemView);
            introduce = itemView.findViewById(R.id.side_navigation_username);
            expand = itemView.findViewById(R.id.expand);
            content = itemView.findViewById(R.id.content);
        }

        @Override
        public void onBind(Recommend recommend, int position) {
            introduce.setText(recommend.getTitle());

            if (content.getAdapter() == null) {
                LinearLayoutManager lm = new LinearLayoutManager(itemView.getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                content.setLayoutManager(lm);
                content.setHasFixedSize(true);

                InnerRecyclerViewAdapter adapter = new InnerRecyclerViewAdapter(new ArrayList<>(), recommend.getInnerTypes());
                content.setAdapter(adapter);
            }

            ((InnerRecyclerViewAdapter)content.getAdapter()).setOnItemClickListener(item -> {
                if (innerItemClickListener != null) {
                    innerItemClickListener.onInnerItemClick(item);
                }
            });

            InnerRecyclerViewAdapter rva = (InnerRecyclerViewAdapter) content.getAdapter();
            rva.updateData(recommend.getPlaylists());

            if (recommend.isClear()) {
                introduce.setVisibility(View.GONE);
                expand.setVisibility(View.GONE);
            } else {
                introduce.setVisibility(View.VISIBLE);
                expand.setVisibility(View.VISIBLE);
            }

            if (recommend.getOuterType() == RECOMMEND_TYPE_SONGS && content.getOnFlingListener() == null) {
                PagerSnapHelper snapHelper = new PagerSnapHelper(); // 吸附
                snapHelper.attachToRecyclerView(content);
                if (content.getItemDecorationCount() == 0) {
                    content.addItemDecoration(new HorizontalItemDecoration(54, 54, Utils.dpToPx(itemView.getContext(), 15)));
                }
            } else {
                if (content.getItemDecorationCount() == 0) {
                    content.addItemDecoration(new HorizontalItemDecoration(54, 33, 0));
                }
            }
        }
    }

    public class CarouselHolder extends Holder {

        ViewPager2 carousel;
        DotView dotView;

        public CarouselHolder(@NonNull View itemView) {
            super(itemView);
            carousel = itemView.findViewById(R.id.carousel);

            carousel.getChildAt(0).setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });

            dotView = itemView.findViewById(R.id.dot_view);
        }

        @Override
        public void onBind(Recommend recommend, int position) {
            List<Playlist> banners = recommend.getPlaylists();

            CarouselAdapter adapter = new CarouselAdapter(banners);
            carousel.setAdapter(adapter);

            int startPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % banners.size());
            carousel.setCurrentItem(startPosition, false);
        }
    }

    public class ExpandHolder extends Holder {

        InnerRecyclerView content;
        int lastPosition = 0;

        public ExpandHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.expand_recycler_view);
        }

        @Override
        public void onBind(Recommend recommend, int position) {
            if (content.getAdapter() == null) {
                LinearLayoutManager lm = new LinearLayoutManager(itemView.getContext(),
                        LinearLayoutManager.HORIZONTAL, false);

                content.setLayoutManager(lm);
                content.setHasFixedSize(false);

                ExpandRecyclerViewAdapter adapter = new ExpandRecyclerViewAdapter();
                content.setAdapter(adapter);

                if (content.getOnFlingListener() == null) {
                    PagerSnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(content);
                }

                content.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    private int width = 0;
                    private int ini = Utils.dpToPx(itemView.getContext(), 90);
                    private int tar = Utils.dpToPx(itemView.getContext(), 270);
                    private int diff = tar - ini;

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            int visiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                            if (visiblePosition != -1) {
                                setFinalHeight(visiblePosition);
                                lastPosition = visiblePosition;
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if (layoutManager == null) return;

                        int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
                        if (firstVisiblePos < 0 || firstVisiblePos > 1) return;

                        View firstVisibleView = layoutManager.findViewByPosition(firstVisiblePos);
                        if (firstVisibleView == null) return;

                        if (width == 0) {
                            width = firstVisibleView.getWidth();
                            if (width == 0) return;
                        }

                        float progress = 0;
                        if (firstVisiblePos == 0) {
                            int left = firstVisibleView.getLeft();
                            progress = Math.abs(left) / (float) width;
                            progress = Math.min(progress, 1f);
                        } else if (firstVisiblePos == 1) {
                            int left = firstVisibleView.getLeft();
                            progress = 1 - (left / (float) width);
                            progress = Math.max(progress, 0f);
                        }

                        setExpandScrollProgress(progress);

                        int currentHeight = (int) (ini + diff * progress);
                        updateHeightInScrolling(currentHeight);
                    }

                    private void updateHeightInScrolling(int currentHeight) {
                        ViewGroup.LayoutParams params = itemView.getLayoutParams();
                        if (params.height == currentHeight) return;
                        params.height = currentHeight;
                        itemView.setLayoutParams(params);
                        itemView.requestLayout();
                    }

                    private void setFinalHeight(int targetPosition) {
                        int targetHeight = targetPosition == 0 ? ini : tar;
                        ViewGroup.LayoutParams params = itemView.getLayoutParams();
                        if (params.height != targetHeight) {
                            params.height = targetHeight;
                            itemView.setLayoutParams(params);
//                            itemView.requestLayout();
//                            if (itemView.getParent() != null) {
//                                itemView.getParent().requestLayout();
//                            }
                        }
                    }
                });
            }
        }
    }

    public class PagesHolder extends Holder {

        View page;
        public PagesHolder(@NonNull View itemView) {
            super(itemView);
            page = itemView.findViewById(R.id.page);

            Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    float translationX = progress * page.getWidth();
                    page.setTranslationX(translationX);

                    Choreographer.getInstance().postFrameCallback(this);
                }
            };

            Choreographer.getInstance().postFrameCallback(frameCallback);
        }

        @Override
        public void onBind(Recommend recommend, int position) {
        }
    }

    public static class HorizontalItemDecoration extends RecyclerView.ItemDecoration {
        private final int margin;
        private final int space;
        private final int endExtra;

        public HorizontalItemDecoration(int margin, int space, int endExtra) { // px
            this.margin = margin;
            this.space = space;
            this.endExtra = endExtra;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = margin;
            } else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.left = space;
                outRect.right = margin + endExtra;
            } else {
                outRect.left = space;
            }
        }
    }

    public static class VerticalItemDecoration extends RecyclerView.ItemDecoration {
        private final int margin;
        private final int space;

        public VerticalItemDecoration(int margin, int space) { // px
            this.margin = margin;
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = margin;
            } else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.top = space;
                outRect.bottom = margin;
            } else {
                outRect.top = space;
            }
        }
    }

    public static class MusicFixedItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 48;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 36;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
                outRect.bottom = 36;
            } else if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = 112;
            } else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = 48;
            }
        }
    }
}
