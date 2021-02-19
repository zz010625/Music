package com.example.music.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.music.model.Music;
import com.example.music.tools.Tools;
import com.example.music.view.activity.MusicSearchActivity;
import com.example.music.view.activity.PlayActivity;

import java.util.ArrayList;

public class SearchPresenter {
    private NetRequestPresenter netRequestPresenter = new NetRequestPresenter();
    private MusicSearchActivity musicSearchActivity;
    private String ids = "";
    private String playerIds="";
    private ArrayList playerMusicArrayList;

    public void setIds(String ids) {
        this.ids = ids;
    }

    public SearchPresenter(MusicSearchActivity musicSearchActivity) {
        this.musicSearchActivity = musicSearchActivity;
    }

    /*以下Handler是网络请求搜索音乐后用于获取相关音乐的信息*/
    //取得ids后
    Handler getIds = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String ids = "";
            ArrayList musicArrayList = (ArrayList) msg.obj;
            for (int i = 0; i < musicArrayList.size(); i++) {
                Music music = (Music) musicArrayList.get(i);
                if (i < musicArrayList.size() - 1) {
                    ids += music.getId() + ",";
                } else {
                    ids += music.getId();
                }
            }
            setIds(ids);
            //获取音乐picUrl
            netRequestPresenter.sendGetDetailRequest(ids, getPicUrl);
        }
    };

    //取得picUrl后
    Handler getPicUrl = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //获取音乐playUrl
            netRequestPresenter.sendGetPlayUrlRequest(ids, getPlayUrl);
        }
    };
    //取得playUrl后
    Handler getPlayUrl = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ArrayList musicArrayList = (ArrayList) msg.obj;
            //调用接口将搜索结果初始化到界面上
            if (musicSearchActivity!= null) {
                musicSearchActivity.initRecyclerViewAdapt(musicArrayList,playerMusicArrayList);
            }
        }
    };

    //get搜索结果
    public void getSearchResult(String name) {
        //调用netRequestPresenter中方法进行网络请求 取得ids
        netRequestPresenter.sendSearchRequest(name, getIds);
    }

    //跳转到PlayActivity
    public void jumpToPlayActivity(Activity activity, Music music,ArrayList playerMusicArrayList) {
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("playingMusic",music);
        intent.putExtra("size",playerMusicArrayList.size());
        intent.putExtra("position",0);
        intent.putExtra("jumpFrom","SearchActivity");
        for (int i = 0; i <playerMusicArrayList.size(); i++) {
            intent.putExtra("music"+i,(Music)playerMusicArrayList.get(i));
        }
        activity.startActivity(intent);
    }

    /*以下方法及Handler是为了获取一个arrayList包含播放历史中的Music对象
    因为播放链接并非永久有效而是临时的 所以得再次进行网络请求获取*/

    //从数据库中获取播放过的歌曲id等信息
    public void getPlayerMusic() {
        Tools tools=new Tools();
        playerMusicArrayList=new ArrayList();
        playerIds= tools.getPlayerMusics(musicSearchActivity,playerMusicArrayList);
        //获取最新的picUrl和playUrl(因为链接是临时的 所以数据库里链接等信息并没有存 需实时通过网络请求得到 )
        if (!playerIds.equals("")){
            netRequestPresenter=new NetRequestPresenter(playerMusicArrayList);
            //获取音乐的picUrl
            netRequestPresenter.sendGetDetailRequest(playerIds,getPlayerMusicPicUrl);}
    }
    //取得picUrl后
    Handler getPlayerMusicPicUrl = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //获取音乐的playUrl
            netRequestPresenter.sendGetPlayUrlRequest(playerIds, getPlayerMusicPlayUrl);
        }
    };
    //取得playUrl后
    Handler getPlayerMusicPlayUrl = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            playerMusicArrayList = (ArrayList) msg.obj;
            playerIds="";
        }
    };
}
