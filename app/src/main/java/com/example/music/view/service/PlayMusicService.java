package com.example.music.view.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music.model.Music;
import com.example.music.presenter.PlayMusicPresenter;
import com.example.music.view.activity.PlayActivity;

import java.io.IOException;

public class PlayMusicService extends Service implements PlayMusicView {
    public static final String ACTION_CHANGE_PROGRESS = "action.changeProgress";
    public static final String ACTION_START_OR_PAUSE_MUSIC = "action.startOrPauseMusic";
    private PlayMusicPresenter playMusicPresenter;
    private MediaPlayer player;
    public ActionsBroadcastReceiver broadcastReceiver;

    private String notificationId = "serviceid";
    private String notificationName = "servicename";

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1, playMusicPresenter.getNotification(this, notificationId));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //调用playMusicPresenter中方法取到当前点击Music对象并进行播放
        playMusicPresenter.playMusic(intent, this, broadcastReceiver);
        //设置/更新通知
        showNotification();
        //设置播放完毕后的操作
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                sendBroadcast(PlayActivity.ACTION_PLAY_NEXT_MUSIC);
            }
        });
        //发送updateUI广播
        sendBroadcast(PlayActivity.ACTION_UPDATE_UI);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        playMusicPresenter = new PlayMusicPresenter(this);
        super.onCreate();
    }

    //广播内容
    public class ActionsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            switch (action) {
                case "action.changeProgress":
                    player.seekTo(intent.getIntExtra("progress", 0));
                    break;
                case "action.startOrPauseMusic":
                    startOrPause();
                    break;
            }
        }
    }

    //动态注册广播
    public void registerBroadcast(String action) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        broadcastReceiver = new ActionsBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    //发送广播让activity进行UI刷新
    public void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        switch (action) {
            case PlayActivity.ACTION_UPDATE_UI:
                Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        intent.putExtra("currentPosition", player.getCurrentPosition());
                        intent.putExtra("duration", player.getDuration());
                        intent.putExtra("action", PlayActivity.ACTION_UPDATE_UI);
                        LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(intent);
                        handler.postDelayed(this, 100);
                    }
                }).start();
                break;
            case PlayActivity.ACTION_PLAY_NEXT_MUSIC:
                intent.putExtra("action", PlayActivity.ACTION_PLAY_NEXT_MUSIC);
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(intent);
                break;
        }

    }

    public void startOrPause() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }


    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void playMusic(Music music, boolean isUnRegister) {
        if (isUnRegister) {
            //注册广播
            registerBroadcast(ACTION_CHANGE_PROGRESS);
            registerBroadcast(ACTION_START_OR_PAUSE_MUSIC);
        }
        //在切换歌曲时先暂停当前播放歌曲 之后清空原MediaPlayer对象
        if (player != null) {
            player.pause();
            player = null;
        }
        player = new MediaPlayer();
        //准备资源
        try {
            player.setDataSource(music.getPlayUrl());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startOrPause();
    }
}