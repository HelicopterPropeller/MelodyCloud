package com.example.neteasecloudmusic.ui.mine;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.neteasecloudmusic.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
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
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    TabLayout tabLayout;
    ViewPager2 outerViewPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        float density = view.getContext().getResources().getDisplayMetrics().density;

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.child_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.topMargin = (int)(density * 321 + 0.5f) + Math.abs(systemBars.top - systemBars.bottom);
            v.setLayoutParams(layoutParams);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.information_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.topMargin = Math.abs(systemBars.top - systemBars.bottom);
            v.setLayoutParams(layoutParams);
            return insets;
        });

        view.findViewById(R.id.information_container).invalidate();
        view.findViewById(R.id.child_container).invalidate();

        outerViewPager = view.findViewById(R.id.mine_view_pager);
        outerViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return MineMusicFragment.newInstance("", "");
                    case 1: return MineAudiobooksFragment.newInstance("", "");
                    case 2: return MineNoteFragment.newInstance("", "");
                }
                return MineMusicFragment.newInstance("", "");
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        tabLayout = view.findViewById(R.id.mine_tab_layout);

        new TabLayoutMediator(tabLayout, outerViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("音乐");
                    break;
                case 1:
                    tab.setText("博客");
                    break;
                case 2:
                    tab.setText("笔记");
                    break;
            }
        }).attach();

        Window window = requireActivity().getWindow();

        // 设置状态栏文字颜色为 false 浅色
        View decorView = window.getDecorView();
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(window, decorView);
        controller.setAppearanceLightStatusBars(false);
    }
}