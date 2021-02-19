package com.example.music.presenter;

import android.os.Handler;
import android.os.Message;

import com.example.music.model.Music;
import com.example.music.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NetRequestPresenter {
    private ArrayList musicArrayList;
    public NetRequestPresenter(){};
    public NetRequestPresenter(ArrayList arrayList){
        this.musicArrayList=arrayList;
    }
    //发送搜索请求 得到搜索结果
    public void sendSearchRequest(String name, Handler handler) {
        String mUrl="http://sandyz.ink:3000/search?keywords=" +name;
        GETNetRequest(mUrl,"sendSearchRequest",handler,null,null);
    }

    //发送获取音乐详细信息请求 得到歌曲图片Url
    public void sendGetDetailRequest(String ids, Handler handler) {
        String mUrl="http://sandyz.ink:3000/song/detail?ids=" +ids;
        GETNetRequest(mUrl,"sendGetDetailRequest",null,handler,null);
    }

    //发送获取音乐Url请求 得到playUrl
    public void sendGetPlayUrlRequest(String ids, Handler handler) {
        String mUrl="http://sandyz.ink:3000/song/url?br=320000&id=" +ids;
                GETNetRequest(mUrl,"sendGetPlayUrlRequest",null,null,handler);
    }
    //GET类型的网络请求
    public void GETNetRequest(String mUrl, String action, Handler searchRequest,Handler detailRequest,Handler playUrlRequest) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(mUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append('\n');
                        }
                        inputStream.close();
                        String resultJSON = sb.toString();
                        switch (action) {
                            case "sendSearchRequest":
                                musicArrayList = new ArrayList();
                                musicArrayList = analysisSearchResultJSON(resultJSON);

                                Message musicMessage = searchRequest.obtainMessage();
                                musicMessage.obj = musicArrayList;
                                searchRequest.sendMessage(musicMessage);
                                break;
                            case "sendGetDetailRequest":
                                analysisSongsDetailJSON(resultJSON);

                                Message detailMessage = detailRequest.obtainMessage();
                                detailMessage.obj = musicArrayList;
                                detailRequest.sendMessage(detailMessage);
                                break;
                            case "sendGetPlayUrlRequest":
                                analysisPlayUrlJSON(resultJSON);

                                Message playUrlMessage = playUrlRequest.obtainMessage();
                                playUrlMessage.obj = musicArrayList;
                                playUrlRequest.sendMessage(playUrlMessage);
                                break;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //对搜索结果JSON进行解析得到音乐名 id 歌手名 专辑名
    public ArrayList analysisSearchResultJSON(String searchResultJSON) {
        try {
            JSONObject JSON = new JSONObject(searchResultJSON);
            JSONObject result = JSON.getJSONObject("result");
            JSONArray songs = result.getJSONArray("songs");
            for (int i = 0; i < songs.length(); i++) {
                Music music = new Music();
                JSONObject song = songs.getJSONObject(i);
                music.setName(song.getString("name"));
                music.setId(song.getString("id"));
                JSONArray artists = song.getJSONArray("artists");
                for (int j = 0; j < artists.length(); j++) {
                    JSONObject artist = artists.getJSONObject(j);
                    if (artists.length() == 1) {
                        music.setArtist(artist.getString("name"));
                    } else {
                        if (j == 0) {
                            music.setArtist(artist.getString("name"));
                        } else {
                            music.setArtist(music.getArtist() + "/" + artist.getString("name"));
                        }
                    }
                }
                JSONObject album = song.getJSONObject("album");
                music.setAlbum(album.getString("name"));
                musicArrayList.add(music);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return musicArrayList;
    }

    //对音乐详细JSON进行解析 得到音乐图片Url
    public void analysisSongsDetailJSON(String songsDetailJSON) {
        try {
            JSONObject JSON = new JSONObject(songsDetailJSON);
            JSONArray songs = JSON.getJSONArray("songs");
            for (int i = 0; i < songs.length(); i++) {
                JSONObject song = songs.getJSONObject(i);
                JSONObject al = song.getJSONObject("al");
                Music music = (Music) musicArrayList.get(i);
                music.setPicUrl(al.getString("picUrl"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //对音乐播放链接JSON进行解析 得到音乐播放Url 以及音乐是否免费 试听开始结束时间
    public void analysisPlayUrlJSON(String playUrlJSON) {
        try {
            JSONObject JSON = new JSONObject(playUrlJSON);
            JSONArray data = JSON.getJSONArray("data");
            //获取播放链接时数组内数据的顺序和请求时的id顺序不相同 所以只能利用for遍历
            for (int i = 0; i < data.length(); i++) {
                JSONObject song = data.getJSONObject(i);
                for (int j = 0; j < musicArrayList.size(); j++) {
                    Music music = (Music) musicArrayList.get(j);
                    if (song.getString("id").equals(music.getId())) {
                        if (song.isNull("freeTrialInfo")) {
                            music.setFree(true);
                        } else {
                            music.setFree(false);
                            JSONObject freeTrialInfo = song.getJSONObject("freeTrialInfo");
                            music.setStart(freeTrialInfo.getString("start"));
                            music.setEnd(freeTrialInfo.getString("end"));
                        }
                        music.setPlayUrl(song.getString("url"));
                        j = musicArrayList.size();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
