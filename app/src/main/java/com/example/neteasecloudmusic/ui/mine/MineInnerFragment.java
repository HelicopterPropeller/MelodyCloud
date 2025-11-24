package com.example.neteasecloudmusic.ui.mine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neteasecloudmusic.R;
import com.example.neteasecloudmusic.data.model.Playlist;
import com.example.neteasecloudmusic.data.model.SingletonUser;
import com.example.neteasecloudmusic.data.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineInnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineInnerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MineInnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineInnerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineInnerFragment newInstance(String param1, String param2) {
        MineInnerFragment fragment = new MineInnerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private SingletonUser singleInstance;
    private User self;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (singleInstance == null) {
            singleInstance = SingletonUser.getInstance();
            self = singleInstance.getSelf();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mine_inner, container, false);
    }

    List<Playlist> list;
    RecyclerView recyclerView;

    /**
     * 数据类的逻辑是所有和User相关的Playlist集合
     * 近期: Sort by time
     * 创建: !isCover && Creator.equals(self)
     * 收藏: !isCover && !Creator.equals(self) && isCollect
     * 专辑: isCover && isCollect
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.mine_inner_recycler_view);

        Log.d("fuck", self.getUsername());
        initDataSource();

        MineRecyclerViewAdapter adapter = new MineRecyclerViewAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public void initDataSource() {
        if (list == null) {
            List<Playlist> data = Playlist.minePageDataSimulation();
            switch (mParam2) {
                case "0":
                    // 近期要在Playlist加时间字段
                    Log.d("fuck", "come here 0");
                    break;
                case "1":
                    for (int i = data.size() - 1; i >= 0; --i) {
                        if (data.get(i).isAlbum() || data.get(i).getCreator().getUsername() != self.getUsername()) {
                            data.remove(i);
                        }
                    }
                    Log.d("fuck", "come here 1");
                    break;
                case "2":
                    for (int i = data.size() - 1; i >= 0; --i) {
                        if (data.get(i).isAlbum() || data.get(i).getCreator().getUsername() == self.getUsername()) {
                            data.remove(i);
                        }
                    }
                    Log.d("fuck", "come here 2");
                    break;
                case "3":
                    for (int i = data.size() - 1; i >= 0; --i) {
                        if (!data.get(i).isAlbum()) {
                            data.remove(i);
                        }
                    }
                    Log.d("fuck", "come here 3");
                    break;
            }
            this.list = data;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (singleInstance == null) {
            singleInstance = SingletonUser.getInstance();
            self = singleInstance.getSelf();
        }
    }
}