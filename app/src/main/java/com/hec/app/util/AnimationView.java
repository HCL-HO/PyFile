package com.hec.app.util;

/**
 * Created by Isaac on 18/5/2016.
 */
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.lang.reflect.Field;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;

import com.hec.app.R;

public class AnimationView extends ImageView {


    private Movie mMovie;

    /**
     * 开始播放按钮图片
     */
    private Bitmap mStartButton;

    /**
     * 记录动画开始的时间
     */
    private long mMovieStart;

    /**
     * GIF图片的宽度
     */
    private int mImageWidth;

    /**
     * GIF图片的高度
     */
    private int mImageHeight;

    /**
     * 图片是否正在播放
     */
    private boolean isPlaying;

    /**
     * 是否允许自动播放
     */
    private boolean isAutoPlay;

    /**
     * PowerImageView构造函数。
     *
     * @param context
     */
    public AnimationView(Context context) {
        super(context);
    }

    /**
     * PowerImageView构造函数。
     *
     * @param context
     */
    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * PowerImageView构造函数，在这里完成所有必要的初始化操作。
     *
     * @param context
     */

    public int screenwidth ;
    public int screenheight ;
    public AnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PowerImageView);
        int resourceId = getResourceId(a, context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenwidth = size.x;
        screenheight = size.y;
        if (resourceId != 0) {
            // 当资源id不等于0时，就去获取该资源的流

            InputStream is = getResources().openRawResource(resourceId);

            // 使用Movie类对流进行解码
            mMovie = Movie.decodeStream(is);


            if (mMovie != null) {
                TestUtil.print("HERE");
                // 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                TestUtil.print("HERE2");
                try {
                    isAutoPlay = true;
                    mImageWidth = bitmap.getWidth();
                    mImageHeight = bitmap.getHeight();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    bitmap.recycle();
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie == null) {
            // mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
            super.onDraw(canvas);
        } else {
            // mMovie不等于null，说明是张GIF图片
            if (isAutoPlay) {
                // 如果允许自动播放，就调用playMovie()方法播放GIF动画
                playMovie(canvas);
                invalidate();
            } else {
                // 不允许自动播放时，判断当前图片是否正在播放
                if (isPlaying) {
                    // 正在播放就继续调用playMovie()方法，一直到动画播放结束为止
                    if (playMovie(canvas)) {
                        isPlaying = false;
                    }
                    invalidate();
                } else {
                    // 还没开始播放就只绘制GIF图片的第一帧，并绘制一个开始按钮
                    mMovie.setTime(0);
                    mMovie.draw(canvas, 0, 0);
                    int offsetW = (mImageWidth - mStartButton.getWidth()) / 2;
                    int offsetH = (mImageHeight - mStartButton.getHeight()) / 2;
                    canvas.drawBitmap(mStartButton, offsetW, offsetH, null);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            setMeasuredDimension(mImageWidth , mImageHeight);
        }
    }


    private boolean playMovie(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int duration = mMovie.duration();
        if (duration == 0) {
            duration = 1000;
        }
        int relTime = (int) ((now - mMovieStart) % duration);
        mMovie.setTime(relTime);
        mMovie.draw(canvas, 0, 0);
        if ((now - mMovieStart) >= duration) {
            mMovieStart = 0;
            return true;
        }

        return false;
    }

    /**
     * 通过Java反射，获取到src指定图片资源所对应的id。
     *
     * @param a
     * @param context
     * @param attrs
     * @return 返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0。
     */
    private int getResourceId(TypedArray a, Context context, AttributeSet attrs) {
        try {
            Field field = TypedArray.class.getDeclaredField("mValue");
            field.setAccessible(true);
            TypedValue typedValueObject = (TypedValue) field.get(a);
            return typedValueObject.resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        return 0;
    }
}
