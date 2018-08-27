package com.hec.app.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hec.app.R;

public class TestDragActivity extends AppCompatActivity implements View.OnDragListener {

    //把图片拖拽到哪里（可以拖拽到的区域）
    private LinearLayout dragdropRegin;
    // 拖动到图像
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_drag);


        dragdropRegin = (LinearLayout) findViewById(R.id.fl_dragdrop_region);
        imageView = (ImageView) findViewById(R.id.imageview);

        //给可以拖拽到的区域添加Drag监听器
        dragdropRegin.setOnDragListener(this);
        // 为目标设置拖动监听器
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                View.DragShadowBuilder mysBuilder = new MyDragShadowBuilder(
                        imageView);
                // 开始拖动，方法中第一参数是ClipData类型的对象。用于传递剪切板数据，可以为null
                v.startDrag(null, mysBuilder, null, 0);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        switch (action) {
            // 开始拖动
            case DragEvent.ACTION_DRAG_STARTED:
                Toast.makeText(TestDragActivity.this, "开始拖动", Toast.LENGTH_LONG).show();
                break;
            // 进入目标区域
            case DragEvent.ACTION_DRAG_ENTERED:
                Toast.makeText(TestDragActivity.this, "进入目标区域", Toast.LENGTH_LONG).show();
                break;
            // 在目标区域移动
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.e("aa", "drag location x=" + event.getX() + " y =" + event.getY());
                break;
            // 离开目标区域
            case DragEvent.ACTION_DRAG_EXITED:
                Toast.makeText(TestDragActivity.this, "离开目标区域", Toast.LENGTH_LONG).show();
                break;
            // 在目标区域放下ImageView控件
            case DragEvent.ACTION_DROP:
                Toast.makeText(TestDragActivity.this, "在目标区域放下ImageView控件", Toast.LENGTH_LONG).show();
                ImageView imageView = (ImageView) getLayoutInflater().inflate(
                        R.layout.image, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

//                layoutParams.leftMargin = (int) event.getX()-100;
//                layoutParams.topMargin = (int) event.getY()-100;
                // 添加到视图中，完成复制
                dragdropRegin.addView(imageView, layoutParams);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Toast.makeText(TestDragActivity.this, "完成拖拽", Toast.LENGTH_LONG).show();
            default:
                return false;
        }
        return true;
    }

}

class MyDragShadowBuilder extends View.DragShadowBuilder {

    // 拖动阴影的区域
    private static Drawable shadow;
    // 储存绘制的拖动阴影图像
    private static Bitmap newBitmap;

    public MyDragShadowBuilder(View arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
        shadow = new ColorDrawable(Color.LTGRAY);// 浅灰色
    }

    // 在该方法中绘制拖动阴影图像 实例化newBitmap变量
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        // TODO Auto-generated method stub
        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        int width, heigth;
        // 设置拖动阴影的宽度/高度为原宽/高度的1.5倍
        width = (int) (getView().getWidth() * 1.5);
        heigth = (int) (getView().getHeight() * 1.5);
        // 设置拖动图像的绘制 区域
        shadow.setBounds(0, 0, width, heigth);
        // 设置拖动阴影图像的宽度和高度
        shadowSize.set(width, heigth);
        // 设置手指在拖动图像的位置 设置为中点
        shadowTouchPoint.set(width / 2, heigth / 2);

        if (getView() instanceof ImageView) {
            // getView()方法返回的值就是构造方法传入的arg0 参数
            ImageView imageView = (ImageView) getView();
            // 获取drawable对象
            Drawable drawable = imageView.getDrawable();
            // 获取imageview的bitmap
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            // 创建一个新的bitmap
            newBitmap = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(newBitmap);
            // 将图像绘制在画布上，但现在还没有正式将图像绘制在阴影图像上，目前只是将bitmap放大并绘制在newbitmap上
            canvas.drawBitmap(newBitmap, new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()), new Rect(0, 0, width, heigth), null);
        }

    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDrawShadow(canvas);
        // 将图像正式绘制在阴影图像上
        canvas.drawBitmap(newBitmap, 0, 0, new Paint());
    }

}
