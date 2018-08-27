package com.hec.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.LotterySettleActivity;
import com.hec.app.entity.BetSettleInfo;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.StringUtil;

import java.util.List;

/**
 * Created by hec on 2015/12/25.
 */
public class BetSettleAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    // 屏幕宽度,由于我们用的是HorizontalScrollView,所以按钮选项应该在屏幕外
    private int mScreentWidth;
    private View view;
    private List<BetSettleInfo> list;

    public BetSettleAdapter(Context context, int screenWidth, List<BetSettleInfo> list) {
        this.mContext = context;
        this.mScreentWidth = screenWidth;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BetSettleInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_settle, parent, false);

            holder = new ViewHolder();
            holder.hSView = (HorizontalScrollView) convertView.findViewById(R.id.hsv);

            holder.action = convertView.findViewById(R.id.ll_action);
            holder.btnDelete = (Button) convertView.findViewById(R.id.button1);

            holder.tvSelectedNums = (TextView) convertView.findViewById(R.id.tvSelectedNums);
            holder.tvPlayTypeName = (TextView) convertView.findViewById(R.id.tvPlayTypeName);
            holder.tvSingleSummary = (TextView) convertView.findViewById(R.id.tvSingleSummary);

            // 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
            holder.content = convertView.findViewById(R.id.ll_content);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.content.getLayoutParams();
            lp.width = mScreentWidth;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btnDelete.setTag(position);

        // 设置监听事件
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (view != null) {
                            ViewHolder viewHolder1 = (ViewHolder) view.getTag();
                            viewHolder1.hSView.smoothScrollTo(0, 0);
                        }
                    case MotionEvent.ACTION_UP:
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        view = v;
                        int scrollX = viewHolder.hSView.getScrollX();
                        // 获得操作区域的长度
                        int actionW = viewHolder.action.getWidth();

                        // 注意使用smoothScrollTo,这样效果看起来比较圆滑,不生硬
                        // 如果水平方向的移动值<操作区域的长度的一半,就复原
                        if (scrollX < actionW / 2) {
                            viewHolder.hSView.smoothScrollTo(0, 0);
                        } else// 否则的话显示操作区域
                        {
                            viewHolder.hSView.smoothScrollTo(actionW, 0);
                        }
                        return true;
                }
                return false;
            }
        });

        // 这里防止删除一条item后,ListView处于操作状态,直接还原
        if (holder.hSView.getScrollX() != 0) {
            holder.hSView.scrollTo(0, 0);
        }

        // 设置背景颜色,设置填充内容.
        com.hec.app.entity.BetSettleInfo b = list.get(position);

        holder.tvPlayTypeName.setText(b.getPlayTypeName() + "-" + b.getPlayTypeRadioName());
        if (b.getPlayTypeName().equals("任二") || b.getPlayTypeName().equals("任三") || b.getPlayTypeName().equals("任四") || b.getPlayTypeName().equals("任选")) {
            holder.tvSelectedNums.setText(b.getSelectedNums());
        }
        else {
            String selectnum;
            if (b.getSelectedNums().substring(0, 1).equals(",") && !b.getPlayTypeName().contains("定位胆")) {
                selectnum = b.getSelectedNums().substring(1, b.getSelectedNums().length());
            } else {
                selectnum = b.getSelectedNums();
            }
            Log.i("hec", "betsettle:" + selectnum + "Iamhere");

            if (selectnum.contains(", ,")) {
                holder.tvSelectedNums.setText(selectnum.replace(", ,", "|"));
            } else if (selectnum.contains(",,")) {
                holder.tvSelectedNums.setText(selectnum.replace(",,", "|"));
            } else if (selectnum.contains(",;,")) {
                holder.tvSelectedNums.setText(selectnum.replace(",;,", "|"));
            } else if (selectnum.contains(",  ,")) {
                holder.tvSelectedNums.setText(selectnum.replace(",  ,", "|"));
            } else {
                holder.tvSelectedNums.setText(selectnum);
            }
        }

        double total = LotteryUtil.getTotalAmount(b.getAmount(), b.getPrice());

        holder.tvSingleSummary.setText(String.format("%1$s注 X %2$s = %3$s元", b.getAmount(), b.getPrice(), StringUtil.formatDoubleWith4Point(total)));

        // 设置监听事件
        holder.btnDelete.setOnClickListener(this);

        return convertView;
    }

    class ViewHolder {
        public HorizontalScrollView hSView;
        public View content;
        public TextView tvSelectedNums;
        public TextView tvPlayTypeName;
        public TextView tvSingleSummary;
        public View action;
        public Button btnDelete;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        if(position < list.size())
            list.remove(position);
        notifyDataSetChanged();

        ((LotterySettleActivity) mContext).removeOneBet(position);
        ((LotterySettleActivity) mContext).calculateTotalAmount();
    }
}
