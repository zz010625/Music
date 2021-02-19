package com.example.music.presenter;

import android.content.Intent;
import android.util.Log;

import com.example.music.model.Music;
import com.example.music.view.activity.PlayActivity;
import com.example.music.view.service.PlayMusicService;

import java.util.ArrayList;

public class PlayPresenter {
    private PlayActivity playActivity;
    private Music music;
    private ArrayList playerMusicArrayList;
    private int position = 0;
    private String jumpFrom = "";

    public PlayPresenter(PlayActivity playActivity) {
        this.playActivity = playActivity;
    }

    //点开音乐得到需要播放的音乐对象
    public void getMusic(Intent intent) {
        music = (Music) intent.getSerializableExtra("playingMusic");
    }

    //得到最近播放的Music对象List
    public void getPlayerMusicArrayList(Intent intent) {
        if (playerMusicArrayList == null) {
            playerMusicArrayList = new ArrayList();
            //取出最开始需要播放的音乐在list中的位置
            position = intent.getIntExtra("position", 0);
            if (intent.getStringExtra("jumpFrom").equals("SearchActivity")) {
                jumpFrom = "SearchActivity";
                playerMusicArrayList.add(intent.getSerializableExtra("playingMusic"));
            }
            for (int i = 0; i < intent.getIntExtra("size", 0); i++) {
                playerMusicArrayList.add(intent.getSerializableExtra("music" + i));
            }
        }
    }

    //点播放界面的下一首歌得到需要的音乐对象
    public void getNextMusic(Intent intent) {
        getPlayerMusicArrayList(intent);
        if (position + 1 <= playerMusicArrayList.size() - 1) {
            music = (Music) playerMusicArrayList.get(position + 1);
            position += 1;
        } else {
            music = (Music) playerMusicArrayList.get(0);
            position = 0;
        }
    }

    //点播放界面的上一首歌得到需要的音乐对象
    public void getPreviousMusic(Intent intent) {
        getPlayerMusicArrayList(intent);
        if (position - 1 >= 0) {
            music = (Music) playerMusicArrayList.get(position - 1);
            position -= 1;
        } else {
            music = (Music) playerMusicArrayList.get(playerMusicArrayList.size() - 1);
            position = playerMusicArrayList.size() - 1;
        }
    }

    //跳转至PlayMusicService
    public void jumpToPlayMusicService(Intent getData) {
        Intent intent = new Intent(playActivity, PlayMusicService.class);
        intent.putExtra("playingMusic", music);
        ArrayList musicArrayList=new ArrayList();
        for (int i = 0; i <getData.getIntExtra("size",0); i++) {
            musicArrayList.add(getData.getSerializableExtra("music"+i));
        }
        intent.putExtra("size",getData.getIntExtra("size",0));
        intent.putExtra("position",getData.getIntExtra("position",0));
        intent.putExtra("jumpFrom",getData.getStringExtra("jumpFrom"));
        for (int i = 0; i <musicArrayList.size(); i++) {
            intent.putExtra("music"+i,(Music)musicArrayList.get(i));
        }
        playActivity.startService(intent);
    }

    //调用PlayActivity中接口方法加载音乐图片
    public void initMusicPic() {
        playActivity.initMusicPic(music);
    }

    //调用PlayActivity中接口方法加载音乐的歌名
    public void initMusicName() {
        playActivity.initMusicName(music);
    }

}
