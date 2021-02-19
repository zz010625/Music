package com.example.music.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MusicInformationView extends View {
    private Paint paint;
    private String name="";
    private String artist="";
    private String album="";
    public MusicInformationView(Context context) {
        super(context);
    }

    public MusicInformationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        paint=new Paint();
        paint.setColor(Color.parseColor("#000000"));
        paint.setTextSize(50);
        canvas.drawText(name,20,50,paint);
        paint.setTextSize(30);
        paint.setColor(Color.parseColor("#4c545d"));
        canvas.drawText(artist,20,100,paint);
        canvas.drawText(album,60+paint.measureText(artist),100,paint);
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawLine(0,122,getMeasuredWidth(),122,paint);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    public void setMusicInformation(String name,String artist,String album){
        this.name=name;
        this.artist=artist;
        this.album=album;
        postInvalidate();
    }
}
