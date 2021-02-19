package com.example.music.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.presenter.PlayListPresenter;
import com.example.music.view.adapt.PlayListRecyclerViewAdapter;

import java.util.ArrayList;

public class PlayListFragment extends Fragment implements PlayListView {
    private Context playListFragmentContext;
    private View playListFragmentView;
    private RecyclerView recyclerView;
    private PlayListRecyclerViewAdapter playListRecyclerViewAdapter;
    private PlayListPresenter playListPresenter = new PlayListPresenter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        playListFragmentContext = getContext().getApplicationContext();
        playListFragmentView = inflater.inflate(R.layout.fragment_play_list, container, false);
        recyclerView = playListFragmentView.findViewById(R.id.rv_playList);
        return playListFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //调用playListPresenter中方法得到播放过的音乐
        playListPresenter.getPlayerMusic();
    }

    //初始化播放列表
    @Override
    public void initPlayList(ArrayList musicArrayList) {
        playListRecyclerViewAdapter = new PlayListRecyclerViewAdapter(musicArrayList, new PlayListRecyclerViewAdapter.OnSongClickListener() {
            @Override
            //设置点击事件
            public void clickSong(int position) {
                if (position != 0) {
                    Music music = (Music) musicArrayList.get(position - 1);
                    //调用PlayListPresenter中方法打开PlayActivity
                    playListPresenter.jumpToPlayActivity(PlayListFragment.this.getActivity(),musicArrayList, music,position);
                }
            }
        });
        recyclerView.setAdapter(playListRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(playListFragmentContext, LinearLayoutManager.VERTICAL, false));
        playListRecyclerViewAdapter.notifyDataSetChanged();
    }
}