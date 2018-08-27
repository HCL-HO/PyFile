package com.hec.app.framework.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.util.DateTransmit;

import java.util.List;

/**
 * Created by wangxingjian on 16/1/25.
 */
public class BulletHolder extends CommonViewHolder<BulletinInfo> {
    private TextView title,content,time;
    public ImageView image_annoucement,image_new;
    public BulletHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.list_item_bullet);
    }



    @Override
    public void bindData(BulletinInfo o) {
        BulletinInfo bulletinInfo = o;
        title.setText(bulletinInfo.getBulletinTitle());
        String s =bulletinInfo.getBulletinText();
        if(s.length()<30) {
            content.setText(s);
        }else{
            content.setText(s.substring(0,28)+"...");
        }
        String dateStr = DateTransmit.dateTransmits(bulletinInfo.getBulletinTime());
        time.setText(dateStr);
    }

    @Override
    protected void initView() {
        image_annoucement= (ImageView) itemView.findViewById(R.id.img_bullet);
        title = (TextView) itemView.findViewById(R.id.bullet_title);
        content = (TextView) itemView.findViewById(R.id.bullet_content);
        time = (TextView) itemView.findViewById(R.id.bullet_time);
        image_new = (ImageView) itemView.findViewById(R.id.img_new);
    }

    public void bindnew(){
        image_new.setImageResource(R.mipmap.icon_new);
    }

}
