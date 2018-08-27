package com.hec.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.entity.BizException;
import com.hec.app.entity.SlotInfo;
import com.hec.app.util.DateTransmit;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.DetailLotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

public class SlotRecordFragment extends Fragment {

    private TextView slot_number, slot_win, slot_game_type, slot_result, slot_amount, slot_time, slot_amount_valid, slot_status;
    private long id;
    private boolean mIsError = false;
    private View view;

    public static final String ARGUMENT = "argument";
    public SlotRecordFragment() {
        // Required empty public constructor
    }

    public static SlotRecordFragment newInstance(String argument)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        SlotRecordFragment contentFragment = new SlotRecordFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getActivity().getIntent().getLongExtra("id", 0);
        getSlotDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_slot_record, container, false);
        return view;
    }

    private void  initView(){
        slot_amount = (TextView) view.findViewById(R.id.slot_amount);
        slot_amount_valid = (TextView) view.findViewById(R.id.slot_amount_valid);
        slot_game_type = (TextView) view.findViewById(R.id.slot_game_type);
        slot_number = (TextView) view.findViewById(R.id.slot_header_lottery_number);
        slot_result = (TextView) view.findViewById(R.id.slot_result);
        slot_time = (TextView) view.findViewById(R.id.slot_time);
        slot_win = (TextView) view.findViewById(R.id.slot_header_win);
        slot_status = (TextView) view.findViewById(R.id.slot_header_lottery_status);
    }

    private void getSlotDetail(){
        MyAsyncTask<SlotInfo> task = new MyAsyncTask<SlotInfo>(getActivity()) {
            @Override
            public SlotInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().getSlotDetailInfo(id);
            }

            @Override
            public void onLoaded(SlotInfo slotInfo) throws Exception {
                Log.i("wxj", "slot success " + id);
                if(!mIsError){
                    Log.i("wxj", "slot no error");
                    initView();
                    slot_win.setText(String.valueOf(slotInfo.getWinMoney()));
                    slot_game_type.setText(slotInfo.getPlayTypeName());
                    slot_amount.setText(String.valueOf(slotInfo.getNoteMoney()));
                    slot_result.setText(slotInfo.getMemo());
                    slot_time.setText(DateTransmit.dateTransmits(slotInfo.getNoteTime()));
                    slot_amount_valid.setText(String.valueOf(slotInfo.getValidMoney()));
                    switch (slotInfo.getOrderState()){
                        case 1:
                            slot_status.setBackgroundResource(R.mipmap.icon_drawn);
                            slot_status.setText("已中奖");
                            break;
                        case 2:
                            slot_status.setBackgroundResource(R.mipmap.icon_undrawn);
                            slot_status.setText("未中奖");
                            break;
                        case 4:
                            slot_status.setBackgroundResource(R.mipmap.resize_padding);
                            slot_status.setText("未开奖");
                            break;
                        case 0:
                            slot_status.setBackgroundResource(R.mipmap.resize_padding);
                            slot_status.setText("已取消");
                            break;
                        default:
                            slot_status.setBackgroundResource(R.mipmap.resize_padding);
                            slot_status.setText("已撤单");
                            break;
                    }
                }else{
                    Log.i("wxj",getErrorMessage());
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

}
