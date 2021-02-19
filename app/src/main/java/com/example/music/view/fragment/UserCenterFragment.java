package com.example.music.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music.R;


public class UserCenterFragment extends Fragment {
    private View userCenterFragmentView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userCenterFragmentView = inflater.inflate(R.layout.fragment_user_center, container, false);
        return userCenterFragmentView;
    }
}