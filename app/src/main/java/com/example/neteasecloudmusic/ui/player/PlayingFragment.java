package com.example.neteasecloudmusic.ui.player;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.neteasecloudmusic.R;

import com.example.neteasecloudmusic.data.model.SingletonPlaylist;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class PlayingFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public PlayingFragment() {
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
    public static PlayingFragment newInstance(String param1, String param2) {
        PlayingFragment fragment = new PlayingFragment();
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
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.playing_tab_layout);
        viewPager = view.findViewById(R.id.playing_view_pager);

        viewPager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return PlayingCurrentFragment.newInstance("", "");
                    case 1: return PlayingHistoryFragment.newInstance("", "");
                }
                return PlayingHistoryFragment.newInstance("", "");
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        viewPager.setCurrentItem(0, false);

        List<String> types = Arrays.asList("当前播放", "历史播放");
        List<String> nums = Arrays.asList(Integer.toString(SingletonPlaylist.getInstance().getLocal().getList().size()), "null");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ValueAnimator animator = ValueAnimator.ofFloat(16f, 16.5f);
                animator.setDuration(50);
                animator.setInterpolator(new LinearInterpolator());

                TextView type = tab.getCustomView().findViewById(R.id.type);
                if (type != null) {
                    animator.addUpdateListener(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        type.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                    });
                    animator.start();
                    type.setTextColor(Color.parseColor("#0D0F1D"));
                    type.setTypeface(null, Typeface.BOLD);
                }

                TextView num = tab.getCustomView().findViewById(R.id.num);
                if (num != null) {
                    num.setTextColor(Color.parseColor("#0D0F1D"));
                    num.setTypeface(null, Typeface.BOLD);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ValueAnimator animator = ValueAnimator.ofFloat(16.5f, 16f);
                animator.setDuration(50);
                animator.setInterpolator(new LinearInterpolator());

                TextView type = tab.getCustomView().findViewById(R.id.type);
                if (type != null) {
                    animator.addUpdateListener(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        type.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                    });
                    animator.start();
                    type.setTextColor(Color.parseColor("#7E8386"));
                    type.setTypeface(null, Typeface.NORMAL);
                }

                TextView num = tab.getCustomView().findViewById(R.id.num);
                if (num != null) {
                    num.setTextColor(Color.parseColor("#7E8386"));
                    num.setTypeface(null, Typeface.NORMAL);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (tab != null) {
                View custom = LayoutInflater.from(requireContext()).inflate(R.layout.playing_tab_item, null);

                TextView type = custom.findViewById(R.id.type);
                type.setText(types.get(position));

                TextView num = custom.findViewById(R.id.num);
                if (nums.get(position).equals("null")) {
                    num.setText("");
                } else {
                    num.setText(nums.get(position));
                }

                tab.setCustomView(custom);
            }
        }).attach();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) d;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            }
        }
    }

    public void onPlaylistChanged() {
        int size = SingletonPlaylist.getInstance().getLocal().getList().size();
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null && tab.getCustomView() != null) {
            TextView num = tab.getCustomView().findViewById(R.id.num);
            if (num != null) {
                num.setText(size > 0 ? Integer.toString(size) : "");
            }
        }
    }
}
