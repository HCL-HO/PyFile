package com.hec.app.customer_service;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hec.app.R;
import com.hec.app.util.ScaleImage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageViewer extends Dialog implements View.OnClickListener {
    private Context context;
    private ScaleImage imageViewer;
    private ImageView exitBtn;
    private ImageView saveBtn;
    private Bitmap bitmap;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmm");
    private View rootView;
    private boolean downloadAble;

    public ImageViewer(Context context, boolean downloadAble) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        this.context = context;
        this.downloadAble = downloadAble;

        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_image_viewer, null);
        setContentView(rootView);

        imageViewer = (ScaleImage) rootView.findViewById(R.id.image_viewer);
        exitBtn = (ImageView) rootView.findViewById(R.id.exit_btn);
        saveBtn = (ImageView) rootView.findViewById(R.id.save_btn);

        exitBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        if (!downloadAble) {
            saveBtn.setVisibility(View.GONE);
        }
    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        imageViewer.setImageBitmap(bitmap);
        TouchImageView touchImageView = new TouchImageView(context);
        touchImageView.setImageBitmap(bitmap);
        ((ViewGroup)rootView).addView(touchImageView, 0);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        touchImageView.setLayoutParams(lp);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_btn:
                this.dismiss();
                break;
            case R.id.save_btn:
                String filename = "Image_" + dateFormat.format(new Date()) + ".jpg";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(context, "图片储存成功", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "图片储存失败", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }

    }
}
