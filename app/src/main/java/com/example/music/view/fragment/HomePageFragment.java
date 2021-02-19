package com.example.music.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music.R;
import com.example.music.customView.SearchBarView;
import com.example.music.view.activity.MusicSearchActivity;

public class HomePageFragment extends Fragment {
    private View homePageFragmentView;
    private SearchBarView searchBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homePageFragmentView = inflater.inflate(R.layout.fragment_home_page, container, false);
        searchBarView = homePageFragmentView.findViewById(R.id.sbv_search);
        OnClickSearchMusic();
        return homePageFragmentView;

    }

    public void OnClickSearchMusic() {
        searchBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), MusicSearchActivity.class);
                startActivity(intent);
            }
        });
    }
}