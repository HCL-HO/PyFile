package com.hec.app.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.framework.adapter.CommonViewHolder;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.LogcatHelper;

import java.util.ArrayList;

/**
 * Created by wangxingjian on 2017/8/11.
 */

public class SlotLogPopupWindow extends PopupWindow {

    private Context context;
    private RecyclerView slot_log;
    private TextView tv_clear, tv_error, tv_debug;
    private int errorCount, debugCount;
    private SlotAdapter adapter;
    private ArrayList<String> list = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            loop();
        }
    };
    public SlotLogPopupWindow(Context context,ArrayList<String> list) {
        this.context = context;
        this.list = list;
        init();
    }

    private void init(){
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.item_slot_log, null);
        slot_log = (RecyclerView) contentView.findViewById(R.id.rv_slot_log);
        tv_clear = (TextView) contentView.findViewById(R.id.btn_slot_clear);
        tv_error = (TextView) contentView.findViewById(R.id.tv_error);
        tv_debug = (TextView) contentView.findViewById(R.id.tv_debug);
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                adapter.notifyDataSetChanged();
                errorCount = 0;
                debugCount = 0;
                tv_error.setText("错误数: " + errorCount);
                tv_debug.setText("警告数: " + debugCount);
            }
        });
        tv_clear.setVisibility(View.GONE);
        adapter = new SlotAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.scrollToPosition(list.size()-1);
        slot_log.setAdapter(adapter);
        slot_log.setLayoutManager(manager);
        loop();
        this.setContentView(contentView);
        this.setWidth(DisplayUtil.getPxByDp(context, 400));
        this.setHeight(DisplayUtil.getPxByDp(context, 180));

        this.setFocusable(true);
        //this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
    }

    private void loop(){
        adapter.notifyDataSetChanged();
        errorCount = 0;
        debugCount = 0;
        for(String s : list){
            if(s.contains("E Unity")){
                errorCount++;
            }
            if(s.contains("D Unity")){
                debugCount++;
            }
        }
        tv_error.setText("错误数: " + errorCount);
        tv_debug.setText("警告数: " + debugCount);
        handler.sendEmptyMessageDelayed(0,2000);
    }

    private class SlotAdapter extends RecyclerView.Adapter{

        private ArrayList<String> list = new ArrayList<>();
        public SlotAdapter(ArrayList<String> list){
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SlotHolder(parent.getContext(),parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((SlotHolder)holder).bindData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class SlotHolder extends CommonViewHolder<String>{

        private Context context;
        private TextView tv_slot_log;

        public SlotHolder(Context context, ViewGroup root) {
            super(context, root, R.layout.item_slot_text);
            this.context = context;
        }

        @Override
        public void bindData(String s) {
            tv_slot_log.setTextColor(0);
            tv_slot_log.setText(s);
            if(s.contains("E Unity"))
                tv_slot_log.setTextColor(context.getResources().getColor(R.color.red));
            if(s.contains("D Unity"))
                tv_slot_log.setTextColor(context.getResources().getColor(R.color.yellow));
            if(s.contains("I Unity") || s.contains("I Unity"))
                tv_slot_log.setTextColor(context.getResources().getColor(R.color.white));
        }

        @Override
        protected void initView() {
            tv_slot_log = (TextView) itemView.findViewById(R.id.tv_slot_log);
        }
    }
}
