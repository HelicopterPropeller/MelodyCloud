package com.example.neteasecloudmusic.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.neteasecloudmusic.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    TabLayout tabLayout;
    ViewPager2 viewPager;
    TextView searchFrameContent;
    ImageView menu;

    List<String> prompts;
    Runnable runnable;

    public interface OnOpenDrawerListener {
        void openDrawer();
    }
    private OnOpenDrawerListener mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = requireActivity().getWindow();

        // 设置状态栏文字颜色为 true 深色
        View decorView = window.getDecorView();
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(window, decorView);
        controller.setAppearanceLightStatusBars(true);

        viewPager = view.findViewById(R.id.home_view_pager);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return HomeRecommendFragment.newInstance("", "");
                    case 1: return HomeMusicFragment.newInstance("", "");
                    case 2: return HomePodcastFragment.newInstance("", "");
                    case 3: return HomeAudiobooksFragment.newInstance("", "");
                }
                return HomeRecommendFragment.newInstance("", "");
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });

        tabLayout = view.findViewById(R.id.home_tab_layout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("推荐");
                    break;
                case 1:
                    tab.setText("音乐");
                    break;
                case 2:
                    tab.setText("博客");
                    break;
                case 3:
                    tab.setText("听书");
                    break;
            }
        }).attach();

        searchFrameContent = view.findViewById(R.id.search_frame_content);

        if (prompts == null || prompts.isEmpty()) {
            prompts = new ArrayList<>();
            prompts.add("深夜学习lofi");
            prompts.add("Pink Floyd");
            prompts.add("宁静优雅古典乐");
            prompts.add("安静钢琴曲");
            prompts.add("岛屿心情 新歌发布");
            prompts.add("方大同 你的10月听歌榜首");
        }

        AtomicInteger index = new AtomicInteger();

        Handler handler = new Handler(Looper.getMainLooper());

        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    searchFrameContent.setText(prompts.get(index.getAndIncrement() % prompts.size()));
                    searchFrameContent.invalidate();
                    handler.postDelayed(this, 3000);
                }
            };
            handler.post(runnable);
        }

        menu = view.findViewById(R.id.menu);
        menu.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.openDrawer();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnOpenDrawerListener) {
            mListener = (OnOpenDrawerListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnOpenDrawerListener");
        }
    }
}