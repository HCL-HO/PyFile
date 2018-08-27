package com.hec.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hec.app.R;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.framework.adapter.BulletHolder;
import com.hec.app.framework.adapter.ItemClickListener;
import com.hec.app.util.TestUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxingjian on 16/1/25.
 */
public class BulletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM =0;
    private static final int TYPE_FOOTER = 1;
    private int haveclickposition = 1000;
    private boolean IF_NO_OLD_BULLETS = false;
    private LayoutInflater mInflater;
    ItemClickListener itemClickListener;
    List<BulletinInfo> list = new ArrayList();
    List<BulletinInfo> finallist = new ArrayList();
    List<Integer> init_ID_List = new ArrayList<>();
    Context context;
    public void addlist(List<BulletinInfo> l){
        if(l.size()==0){
            IF_NO_OLD_BULLETS = true;
        }else {
            finallist.addAll(l);
            for(BulletinInfo b : l ){
                init_ID_List.add(b.getBulletinID());
            }
        }
    }
    public void addLatestbulletins(List<BulletinInfo> l,Context context){
        this.context = context;
        if(l.size()==0){
            Toast.makeText(context,"网络不稳定,无法加载新数据!",Toast.LENGTH_SHORT).show();
        }else{
            finallist.clear();
            finallist.addAll(l);
        }
    }

    public List<BulletinInfo> sendfinallist(){
        return finallist;
    }

    public void addbacklist(List<BulletinInfo> l){
        list.clear();
        list.addAll(0, l);

    }
    public void sendhaveclickposition(int position){
        haveclickposition = position;
    }

    public BulletAdapter(Context context){
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType==TYPE_ITEM){
            final BulletHolder holder = new BulletHolder(viewGroup.getContext(), viewGroup);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getPosition();
                itemClickListener.onItemClick(v, position);
            }
        });
        return holder;
        }else if(viewType==TYPE_FOOTER){
            View foot_view=mInflater.inflate(R.layout.footer_view_item,viewGroup,false);
            FootViewHolder footViewHolder=new FootViewHolder(foot_view);
            return footViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean tag = true;
        if(holder instanceof BulletHolder) {
            for(int i=0;i<init_ID_List.size();i++){
                if(finallist.get(position).getBulletinID()==init_ID_List.get(i)){
                    ((BulletHolder) holder).bindData(finallist.get(position));
                    tag=false;
                    ((BulletHolder) holder).image_new.setImageResource(0);
                }
            }
            if(tag&&position!=haveclickposition){
                Toast.makeText(context,"您收到新的公告!",Toast.LENGTH_SHORT).show();
                ((BulletHolder) holder).bindnew();
                ((BulletHolder) holder).bindData(finallist.get(position));
            }else if(tag&&position==haveclickposition){
                ((BulletHolder) holder).image_new.setImageResource(0);
                init_ID_List.add(finallist.get(position).getBulletinID());
            }
        }else if(holder instanceof FootViewHolder&&!IF_NO_OLD_BULLETS){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            footViewHolder.foot_view_item_tv.setText("上拉加载");
            footViewHolder.mProgressBar.setVisibility(View.VISIBLE);
        }else if((holder instanceof  FootViewHolder)&&IF_NO_OLD_BULLETS){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            footViewHolder.foot_view_item_tv.setText("没有更多公告");
            footViewHolder.mProgressBar.setVisibility(View.GONE);
            IF_NO_OLD_BULLETS = false;
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return finallist.size()+1;
    }
    public void setOnItemClickListener(ItemClickListener listener){
        this.itemClickListener = listener;
    }
    public static class FootViewHolder extends RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        private ProgressBar mProgressBar;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.footer_item);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_loading);
        }
    }
}
