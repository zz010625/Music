package com.example.music.presenter;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.music.MySQLiteOPenHelper;
import com.example.music.R;
import com.example.music.model.Music;
import com.example.music.tools.Tools;
import com.example.music.view.activity.PlayActivity;
import com.example.music.view.service.PlayMusicService;

import java.util.ArrayList;

public class PlayMusicPresenter {

    private PlayMusicService playMusicService;
    private Music music;

    public PlayMusicPresenter(PlayMusicService playMusicService) {
        this.playMusicService = playMusicService;
    }

    public Notification getNotification(Context playMusicService,String notificationId,Intent intent) {
        Intent notificationIntent = new Intent(playMusicService, PlayActivity.class);
        notificationIntent.putExtra("playingMusic", music);
        ArrayList musicArrayList=new ArrayList();
        for (int i = 0; i <intent.getIntExtra("size",0); i++) {
            musicArrayList.add(intent.getSerializableExtra("music"+i));
        }
        notificationIntent.putExtra("size",intent.getIntExtra("size",0));
        notificationIntent.putExtra("position",intent.getIntExtra("position",0));
        notificationIntent.putExtra("jumpFrom",intent.getStringExtra("jumpFrom"));
        for (int i = 0; i <musicArrayList.size(); i++) {
            intent.putExtra("music"+i,(Music)musicArrayList.get(i));
        }
        PendingIntent contentIntent = PendingIntent.getActivity(playMusicService, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(playMusicService)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("正在播放")
                .setContentIntent(contentIntent)
                .setContentText(music.getName()+" - "+music.getArtist());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }

    //播放音乐
    public void playMusic(Intent intent, Context context, PlayMusicService.ActionsBroadcastReceiver broadcastReceiver) {
        boolean isUnRegister = true;
        //取到当前选中Music对象 判断是否为第一次播放 若不是则比较两次播放是否为同一首歌
        if (music != null) {
            Music music1 = (Music) intent.getSerializableExtra("playingMusic");
            if (music.getId().equals(music1.getId())) {
                //若为同一首则不再进行播放音乐操作及添加到数据库操作 维持原音乐播放进度
            } else {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
                isUnRegister = true;
                music = (Music) intent.getSerializableExtra("playingMusic");
                //判断是否免费
                judgeFree(context, music);
                //判断该音乐是否已在播放列表中 若在则删除原数据
                deleteSameData(music);
                //将该音乐添加至播放列表中
                addMusicToSQLite(music);
                playMusicService.playMusic(music, isUnRegister);
            }
        } else {
            music = (Music) intent.getSerializableExtra("playingMusic");
            //判断是否免费
            judgeFree(context, music);
            //判断该音乐是否已在播放列表中 若在则删除原数据
            deleteSameData(music);
            //将该音乐添加至播放列表中
            addMusicToSQLite(music);
            //调用PlayMusicService中接口方法播放音乐
            playMusicService.playMusic(music, isUnRegister);
        }
    }

    //将本次播放的Music对象中的ID添加到数据库中
    public void addMusicToSQLite(Music music) {
        Tools tools = new Tools();
        tools.addMusicToSQLite(music, playMusicService);
    }

    //删除数据库中相同数据
    public void deleteSameData(Music music) {
        MySQLiteOPenHelper sqLiteOPenHelper = new
                MySQLiteOPenHelper(playMusicService, "PlayList.db", null, 1);
        sqLiteOPenHelper.getWritableDatabase();
        SQLiteDatabase db = sqLiteOPenHelper.getReadableDatabase();
        Cursor cursor = db.query("Music", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                if (id.equals(music.getId())) {
                    db.delete("Music", "id=?", new String[]{id});
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //判断搜索到的歌曲是否免费并Toast
    public void judgeFree(Context context, Music music) {
        if (!music.isFree()) {
            Toast.makeText(context, "此音乐非VIP仅提供第" + music.getStart() + "-" + music.getEnd() + "秒试听", Toast.LENGTH_LONG).show();
        }

    }
}
