package com.hec.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.framework.adapter.CommonViewHolder;
import com.hec.app.util.LotteryUtil;

import java.util.ArrayList;

/**
 * Created by wangxingjian on 2017/7/27.
 */

public class BJLAdapter extends RecyclerView.Adapter {

    private ArrayList<LotteryInfo> lotteryInfos;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public BJLAdapter(ArrayList<LotteryInfo> lotteryInfos, Context context){
        this.lotteryInfos = lotteryInfos;
        this.context  =context;
        Log.i("wxj","bjl " + lotteryInfos.size());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BJLHolder(parent.getContext(),parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((BJLHolder)holder).bindData(lotteryInfos.get(position));
        ((BJLHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onclick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lotteryInfos.size();
    }

    public class BJLHolder extends CommonViewHolder<LotteryInfo>{

        private TextView tvLotteryType;
        private ImageView img;
        private TextView tvLotteryDescription;
        private ImageView iconHotNew;
        public BJLHolder(Context context, ViewGroup root) {
            super(context, root, R.layout.item_bjl);
        }

        @Override
        public void bindData(LotteryInfo lotteryInfo) {
            img.setImageResource(LotteryUtil.getLotteryIcon(lotteryInfo.getLotteryType()));
            tvLotteryDescription.setText(LotteryUtil.getLotteryMessage(lotteryInfo.getLotteryID()));
            tvLotteryType.setText(lotteryInfo.getLotteryType().replace("和盛","聚星"));
            iconHotNew.setImageResource(R.mipmap.icon_hot_lottery);
            iconHotNew.setVisibility(View.VISIBLE);
        }

        @Override
        protected void initView() {
            tvLotteryType = (TextView) itemView.findViewById(R.id.tvLotteryType);
            img = (ImageView) itemView.findViewById(R.id.imgLotteryIcon);
            tvLotteryDescription = (TextView)itemView.findViewById(R.id.tvLotteryDescription);
            iconHotNew = (ImageView) itemView.findViewById(R.id.icon_hot_new);
        }
    }

    public interface OnItemClickListener{
        void onclick(int position);
    }
}
