package com.example.music;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLiteOPenHelper extends SQLiteOpenHelper {
    private Context context;
    //因为链接是临时的 所以这里只保存部分数据 剩下的链接等数据再次通过网络请求重新得到 保存的数据用于在播放列表(播放历史)里展示并可点击播放
    public static final String CREATR_MUSIC = "create table Music("
            + "name text,"
            + "artist text,"
            + "album text,"
            + "id text)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATR_MUSIC);
    }

    public MySQLiteOPenHelper(@Nullable Context context, @Nullable String name,
                              @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Music");
        onCreate(db);

    }
}
