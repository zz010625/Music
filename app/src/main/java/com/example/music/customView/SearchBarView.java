package com.example.music.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.music.R;

public class SearchBarView extends View {
    private Paint paint;
    public SearchBarView(Context context) {
        super(context);
    }

    public SearchBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawCircle(30,30,22,paint);
        canvas.drawLine(47,47,65,65,paint);
        paint.setStrokeWidth(0);
        canvas.drawRoundRect(90,5,getWidth()-5,65,50,50,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=900;
        int height=70;
        setMeasuredDimension(width,height);
    }
}
