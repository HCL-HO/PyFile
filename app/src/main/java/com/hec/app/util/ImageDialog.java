package com.hec.app.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hec.app.R;
import com.hec.app.customer_service.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private ImageView image;
    private ImageView exitBtn;
    private View rootView;

    public ImageDialog(Context context, int imageId) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;

        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null);
        setContentView(rootView);

        image = (ImageView) rootView.findViewById(R.id.image);
        image.setImageResource(imageId);


        exitBtn = (ImageView) rootView.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_btn:
                this.dismiss();
                break;

            default:
                break;
        }

    }
}
