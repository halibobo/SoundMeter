package me.daei.soundmeter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;

import me.daei.soundmeter.R;
import me.daei.soundmeter.ScreenUtil;
import me.daei.soundmeter.World;

/**
 * Created by su on 2016/6/4.
 */
public class SoundDiscView extends ImageView {

    private float scaleWidth, scaleHeight;
    private int newWidth, newHeight;
    private Matrix mMatrix = new Matrix();
    private Bitmap indicatorBitmap;
    private Paint paint = new Paint();
    static final long  ANIMATION_INTERVAL = 20;


    public SoundDiscView(Context context) {
        super(context);
    }

    public SoundDiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_index);
        int bitmapWidth = myBitmap.getWidth();
        int bitmapHeight = myBitmap.getHeight();
        newWidth = getWidth();
        newHeight = getHeight();
        scaleWidth = ((float) newWidth) /(float) bitmapWidth;  // 获取缩放比例
        scaleHeight = ((float) newHeight) /(float) bitmapHeight;  //获取缩放比例
        mMatrix.postScale(scaleWidth, scaleHeight);   //设置mMatrix的缩放比例
        indicatorBitmap = Bitmap.createBitmap(myBitmap, 0, 0, bitmapWidth, bitmapHeight, mMatrix,true);  //获取同等和背景宽高的指针图的bitmap

        paint = new Paint();
        paint.setTextSize(22* ScreenUtil.getDensity(getContext()));
        paint.setAntiAlias(true);  //抗锯齿
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
    }

    public void refresh() {
        postInvalidateDelayed(ANIMATION_INTERVAL); //子线程刷新view
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (indicatorBitmap == null) {
            init();
        }
        mMatrix.setRotate(getAngle(World.dbCount), newWidth / 2, newHeight * 215 / 460);   //片相对位置
        canvas.drawBitmap(indicatorBitmap, mMatrix, paint);
        canvas.drawText((int)World.dbCount+" DB", newWidth/2,newHeight*36/46, paint); //图片相对位置
    }

    private float getAngle(float db){
        return(db-85)*5/3;  //说多了都是泪，网上找的图片。。自己不会改图，代码计算下
    }

}
