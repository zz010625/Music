package com.example.music.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.music.model.Music;
import com.example.music.tools.Tools;
import com.example.music.view.activity.PlayActivity;
import com.example.music.view.fragment.PlayListFragment;

import java.util.ArrayList;

public class PlayListPresenter {
    private PlayListFragment playListFragment;
    private ArrayList musicArrayList;
    private String ids="";
   private NetRequestPresenter netRequestPresenter;

    public PlayListPresenter(PlayListFragment playListFragment) {
        this.playListFragment = playListFragment;
    }
    //跳转到PlayActivity
    public void jumpToPlayActivity(Activity activity, ArrayList musicArrayList,Music music,int position){
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("playingMusic",music);
        intent.putExtra("size",musicArrayList.size());
        intent.putExtra("position",position-1);
        intent.putExtra("jumpFrom","PlayListFragment");
        for (int i = 0; i <musicArrayList.size(); i++) {
            intent.putExtra("music"+i,(Music)musicArrayList.get(i));
        }
        activity.startActivity(intent);
    }
    //从数据库中获取播放过的歌曲id等信息
    public void getPlayerMusic() {
        Tools tools=new Tools();
        musicArrayList=new ArrayList();
        ids= tools.getPlayerMusics(playListFragment.getContext().getApplicationContext(),musicArrayList);
        //获取最新的picUrl和playUrl(因为链接是临时的 所以数据库里链接等信息并没有存 需实时通过网络请求得到 )
        if (!ids.equals("")){
        netRequestPresenter=new NetRequestPresenter(musicArrayList);
        //获取音乐的picUrl
        netRequestPresenter.sendGetDetailRequest(ids,getPicUrl);}
    }
    //取得picUrl后
    Handler getPicUrl = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //获取音乐的playUrl
            netRequestPresenter.sendGetPlayUrlRequest(ids, getPlayUrl);
        }
    };
    //取得playUrl后
    Handler getPlayUrl = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ArrayList musicArrayList = (ArrayList) msg.obj;
            //调用PlayListFragment中接口方法初始化播放列表
            playListFragment.initPlayList(musicArrayList);
            ids="";
        }
    };
}
