package com.example.music.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.music.MySQLiteOPenHelper;
import com.example.music.model.Music;
import com.example.music.view.service.PlayMusicService;

import java.util.ArrayList;

public class Tools {

    //向数据库添加Music对象
    public void addMusicToSQLite(Music music, PlayMusicService playMusicService) {
        MySQLiteOPenHelper sqLiteOPenHelper = new
                MySQLiteOPenHelper(playMusicService, "PlayList.db", null, 1);
        sqLiteOPenHelper.getWritableDatabase();
        SQLiteDatabase db = sqLiteOPenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", music.getName());
        values.put("artist", music.getArtist());
        values.put("album", music.getAlbum());
        values.put("id", music.getId());
        db.insert("Music", null, values);
    }
    //从数据库中获取播放过的歌曲
    public String getPlayerMusics(Context context, ArrayList musicArrayList) {
        MySQLiteOPenHelper sqLiteOPenHelper = new
                MySQLiteOPenHelper(context, "PlayList.db", null, 1);
        sqLiteOPenHelper.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase = sqLiteOPenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("Music",null,null,null,null,null,null);
        if(cursor.moveToLast()){
            do{
                Music music=new Music();
                music.setName(cursor.getString(cursor.getColumnIndex("name")));
                music.setArtist(cursor.getString((cursor.getColumnIndex("artist"))));
                music.setAlbum(cursor.getString((cursor.getColumnIndex("album"))));
                music.setId(cursor.getString((cursor.getColumnIndex("id"))));
                musicArrayList.add(music);
            }while (cursor.moveToPrevious());
        }
        cursor.close();
        String ids="";
        for (int i = 0; i < musicArrayList.size(); i++) {
            Music music = (Music) musicArrayList.get(i);
            if (i < musicArrayList.size() - 1) {
                ids += music.getId() + ",";
            } else {
                ids += music.getId();
            }
        }
        return ids;
    }

}
