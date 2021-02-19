package com.example.music.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.presenter.SearchPresenter;
import com.example.music.view.adapt.MusicSearchRecyclerViewAdapter;

import java.util.ArrayList;

public class MusicSearchActivity extends AppCompatActivity implements MusicSearchView {
    private RecyclerView recyclerView;
    private MusicSearchRecyclerViewAdapter musicSearchRecyclerViewAdapter;
    private SearchView searchView;
    private SearchPresenter searchPresenter = new SearchPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_search);
        recyclerView = findViewById(R.id.rv_searchList);
        searchView = findViewById(R.id.sv_search_music);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("搜索");
        searchView.requestFocus();
        OnClickSearchMusic();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //调用searchPresenter中方法获得播放历史中的MusicList
        searchPresenter.getPlayerMusic();
    }

    //初始化搜索结果
    @Override
    public void initRecyclerViewAdapt(ArrayList musicArrayList, ArrayList playerMusicArrayList) {
        musicSearchRecyclerViewAdapter = new MusicSearchRecyclerViewAdapter(musicArrayList, new MusicSearchRecyclerViewAdapter.OnSongClickListener() {
            @Override
            public void clickSong(int position) {
                if (position != 0) {
                    Music music = (Music) musicArrayList.get(position - 1);
                    //调用searchPresenter中方法打开PlayActivity
                    searchPresenter.jumpToPlayActivity(MusicSearchActivity.this, music, playerMusicArrayList);
                    searchView.clearFocus();
                }
            }
        });
        recyclerView.setAdapter(musicSearchRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        musicSearchRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void OnClickSearchMusic() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //调用searchPresenter中方法获得搜索结果
                searchPresenter.getSearchResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}