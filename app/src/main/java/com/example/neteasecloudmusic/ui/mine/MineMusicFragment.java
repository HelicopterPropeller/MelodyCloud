package com.example.neteasecloudmusic.ui.mine;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.neteasecloudmusic.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineMusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MineMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineMusicFragment newInstance(String param1, String param2) {
        MineMusicFragment fragment = new MineMusicFragment();
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
        return inflater.inflate(R.layout.mine_music, container, false);
    }

    TabLayout tabLayout;
    ViewPager2 innerViewPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tab_layout);

        innerViewPager = view.findViewById(R.id.mine_music_view_pager);
        innerViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return MineInnerFragment.newInstance("position", Integer.toString(position));
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });

        List<String> types = Arrays.asList("近期", "创建", "收藏", "专辑");
        List<String> nums = Arrays.asList("lock", "13", "33", "81");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ValueAnimator animator = ValueAnimator.ofFloat(12f, 12.5f);
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
                ValueAnimator animator = ValueAnimator.ofFloat(12.5f, 12f);
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

        new TabLayoutMediator(tabLayout, innerViewPager, (tab, position) -> {
            if (tab != null) {
                View custom = LayoutInflater.from(requireContext()).inflate(R.layout.mine_tab_item, null);

                TextView type = custom.findViewById(R.id.type);
                type.setText(types.get(position));

                TextView num = custom.findViewById(R.id.num);
                if (nums.get(position).equals("lock")) {
                    num.setText("");
                } else {
                    num.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    num.setText(nums.get(position));
                }

                tab.setCustomView(custom);
            }
        }).attach();

        innerViewPager.setCurrentItem(0, false);
    }
}