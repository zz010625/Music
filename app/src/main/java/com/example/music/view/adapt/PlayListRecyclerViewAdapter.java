package com.example.music.view.adapt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.customView.MusicInformationView;
import com.example.music.model.Music;

import java.util.ArrayList;

public class PlayListRecyclerViewAdapter extends RecyclerView.Adapter<PlayListRecyclerViewAdapter.ViewHolder> {
    ArrayList musicArrayList;
    OnSongClickListener onSongClickListener;

    public PlayListRecyclerViewAdapter(ArrayList musicArrayList, OnSongClickListener onSongClickListener) {
        this.musicArrayList = musicArrayList;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public PlayListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.musicInformationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSongClickListener.clickSong(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListRecyclerViewAdapter.ViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.musicInformationView.setMusicInformation("歌曲","歌手","专辑");
                break;
            default:
                Music music = (Music) musicArrayList.get(position - 1);
                holder.musicInformationView.setMusicInformation(music.getName(),music.getArtist(),music.getAlbum());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return musicArrayList.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private MusicInformationView musicInformationView;

        public ViewHolder(View view) {
            super(view);
            musicInformationView =view.findViewById(R.id.tv_music_information);

        }
    }

    public interface OnSongClickListener {
        void clickSong(int position);
    }
}
