package com.example.music.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.presenter.PlayPresenter;
import com.example.music.view.service.PlayMusicService;

import java.text.DecimalFormat;

public class PlayActivity extends AppCompatActivity implements PlayView {
    public static final String ACTION_UPDATE_UI = "action.updateUI";
    public static final String ACTION_PLAY_NEXT_MUSIC = "action.playNextMusic";
    private boolean isPlay = true;
    private ImageView musicPic, back, startOrPause, next, previous;
    private TextView name, playTime, totalTime;
    private PlayPresenter playPresenter;
    private SeekBar seekBar;
    private UpdateUIBroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        musicPic = findViewById(R.id.iv_music);
        back = findViewById(R.id.iv_back);
        startOrPause = findViewById(R.id.iv_start_or_pause);
        next = findViewById(R.id.iv_next);
        previous = findViewById(R.id.iv_previous);
        seekBar = findViewById(R.id.sb_play);
        name = findViewById(R.id.tv_music);
        playTime = findViewById(R.id.tv_playTime);
        totalTime = findViewById(R.id.tv_totalTime);
        //设置点击事件
        back.setOnClickListener(myOnClickListener);
        startOrPause.setOnClickListener(myOnClickListener);
        next.setOnClickListener(myOnClickListener);
        previous.setOnClickListener(myOnClickListener);
        //调用playPresenter中方法完成音乐的播放 图片 歌名的加载 进度条的实时更新与拖动更新
        playPresenter = new PlayPresenter(this);
        playPresenter.getMusic(getIntent());
        playPresenter.jumpToPlayMusicService();
        playPresenter.initMusicPic();
        playPresenter.initMusicName();
        //注册广播用于实时刷新音乐播放时间
        registerBroadcast(ACTION_UPDATE_UI);
        changeSeekBar();
        //注册广播用于当前音乐播放完后自动播放下一首
        registerBroadcast(ACTION_PLAY_NEXT_MUSIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    //设置点击事件
    View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    //返回
                    finish();
                    break;
                case R.id.iv_start_or_pause:
                    //暂停或开始音乐
                    if (isPlay) {
                        sendBroadcast(PlayMusicService.ACTION_START_OR_PAUSE_MUSIC, 0);
                        startOrPause.setBackgroundResource(R.mipmap.ic_play);
                        isPlay = false;
                    } else {
                        sendBroadcast(PlayMusicService.ACTION_START_OR_PAUSE_MUSIC, 0);
                        startOrPause.setBackgroundResource(R.mipmap.ic_pause);
                        isPlay = true;
                    }
                    break;
                case R.id.iv_next:
                    playPresenter.getNextMusic(getIntent());
                    //调用playPresenter中方法完成音乐的播放 图片 歌名的加载 进度条的实时更新与拖动更新
                    playPresenter.initMusicPic();
                    playPresenter.initMusicName();
                    playPresenter.jumpToPlayMusicService();
                    break;
                case R.id.iv_previous:
                    playPresenter.getPreviousMusic(getIntent());
                    //调用playPresenter中方法完成音乐的播放 图片 歌名的加载 进度条的实时更新与拖动更新
                    playPresenter.initMusicPic();
                    playPresenter.initMusicName();
                    playPresenter.jumpToPlayMusicService();
                    break;

            }
        }
    };

    //广播内容
    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            switch (action) {
                case "action.updateUI":
                    int currentPosition = intent.getIntExtra("currentPosition", 0);
                    seekBar.setProgress(currentPosition);
                    //设置播放音乐总时长
                    DecimalFormat df = new DecimalFormat("00");
                    int playMinute = currentPosition / 60000;
                    int playSecond = (currentPosition - playMinute * 60000) / 1000;
                    playTime.setText(df.format(playMinute) + ":" + df.format(playSecond));
                    int duration = intent.getIntExtra("duration", 0);
                    if (seekBar.getMax() != duration) {
                        //设置播放音乐总时长
                        seekBar.setMax(duration);
                        int minute = duration / 60000;
                        int second = (duration - minute * 60000) / 1000;
                        totalTime.setText(df.format(minute) + ":" + df.format(second));
                    }
                    break;
                case "action.playNextMusic":
                    next.performClick();
            }

        }
    }

    //动态注册广播
    public void registerBroadcast(String action) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }


    //监听SeekBar
    public void changeSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean isChanging = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isChanging) {
                    sendBroadcast(PlayMusicService.ACTION_CHANGE_PROGRESS, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging = true;
                //期望是执行pause
                sendBroadcast(PlayMusicService.ACTION_START_OR_PAUSE_MUSIC, 0);
                startOrPause.setBackgroundResource(R.mipmap.ic_pause);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isChanging = false;
                //期望是执行start
                sendBroadcast(PlayMusicService.ACTION_START_OR_PAUSE_MUSIC, 0);

                /*下面这里看上去不符合逻辑 原因是我为了少写一个广播 将暂停和开始操作放在一起了(在service中判断player是否start 然后再决定是播放还是暂停)
                  但事实上在进度条的拖动过程中onStartTrackingTouch onStopTrackingTouch分别会触发很多次 导致我无法仅根据isPlay的值来做到对图片的更替
                  最后实验了很多次发现这样能恰好达到效果
                 */
                if (isPlay) {
                    startOrPause.setBackgroundResource(R.mipmap.ic_pause);
                } else {
                    startOrPause.setBackgroundResource(R.mipmap.ic_play);
                }
            }
        });
    }

    //发送广播
    public void sendBroadcast(String action, int progress) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("action", action);
        switch (action) {
            case PlayMusicService.ACTION_CHANGE_PROGRESS:
                intent.putExtra("progress", progress);
                break;
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //初始化当前播放音乐的图片
    @Override
    public void initMusicPic(Music music) {
        Glide.with(this).load(music.getPicUrl()).into(musicPic);
    }

    //初始化当前播放音乐的歌名
    @Override
    public void initMusicName(Music music) {
        name.setText(music.getName());
    }
}