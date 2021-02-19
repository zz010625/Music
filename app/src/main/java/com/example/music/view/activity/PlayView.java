package com.example.music.view.activity;

import android.media.MediaPlayer;
import android.widget.ImageView;

import com.example.music.model.Music;

public interface PlayView {
    void initMusicPic(Music music);

    void initMusicName(Music music);
}
