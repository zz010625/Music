package com.example.music.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.music.R;
import com.example.music.view.adapt.MusicFragmentStateAdapter;
import com.example.music.view.fragment.HomePageFragment;
import com.example.music.view.fragment.PlayListFragment;
import com.example.music.view.fragment.UserCenterFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MusicHomepageActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager2 viewPager2;
    private MusicFragmentStateAdapter musicFragmentStateAdapter;
    private TabLayout menu;
    private HomePageFragment homePageFragment = new HomePageFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_homepage);

        viewPager2 = findViewById(R.id.vp_music);
        menu = findViewById(R.id.tl_menu);

        fragments.add(homePageFragment);
        fragments.add(new PlayListFragment());
        fragments.add(new UserCenterFragment());
        musicFragmentStateAdapter = new MusicFragmentStateAdapter(this, fragments);
        viewPager2.setAdapter(musicFragmentStateAdapter);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        //设置所以页面预加载
        viewPager2.setOffscreenPageLimit(fragments.size());
        //设置tab标题
        new TabLayoutMediator(menu, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("首页");
                        break;
                    case 1:
                        tab.setText("最近播放");
                        break;
                    case 2:
                        tab.setText("个人中心");
                        break;
                }
            }
        }).attach();
    }
}
