package com.hec.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.R;
import com.hec.app.activity.FormActivity;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.SuccessActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BankInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.framework.adapter.CommonAdapter;
import com.hec.app.framework.adapter.ViewHolder;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.ServiceException;
import com.hec.app.webservice.WithdrawService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;


/**
 * Created by Joshua on 2016/1/14.
 */
public class WithdrawFragment extends Fragment{

    private GridView myCards;
    private CommonAdapter adapter;
    private List<BankInfo> cards;
    private int colorIndex;
    public final static int[] cardBgs = {R.mipmap.icon_bankcard_blue, R.mipmap.icon_bankcard_green,
            R.mipmap.icon_bankcard_orange, R.mipmap.icon_bankcard_red, R.mipmap.icon_bankcard_grey};
    private TextView notice;
    private LinearLayout bottom_ll;
    private Boolean manageMode;
    private ImageView imgManage;
    private boolean notAtStart;
    private SharedPreferences mySharedPreferences;
    private boolean mIsError;
    private boolean firstIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        firstIn = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(notAtStart) {
            colorIndex = 0;
            cards.clear();
            getData();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("WithdrawFragment", "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Log.e("WithdrawFragment", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        manageMode = false;
        cards = new ArrayList<BankInfo>();
        getData();

        myCards = (GridView) getActivity().findViewById(R.id.gridviewCards);

        notice = (TextView) getActivity().findViewById(R.id.withdraw_notice);
        notice.setText(Html.fromHtml("<font color=#cc0029>*</font> " + getString(R.string.withdraw_notice)));

        bottom_ll = (LinearLayout) getActivity().findViewById(R.id.withdraw_bottom_ll);
        imgManage = (ImageView)getActivity().findViewById(R.id.imgManage);
        imgManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageMode = !manageMode;
                adapter.notifyDataSetChanged();
            }
        });

        if(cards.size() > 1){
            bottom_ll.setVisibility(View.GONE);
        }

    }

    public void getData(){
        mIsError = false;
        MyAsyncTask<List<BankInfo>> task = new MyAsyncTask<List<BankInfo>>(getActivity()) {
            @Override
            public List<BankInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getBankCard();
            }
            @Override
            public void onLoaded(List<BankInfo> data) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                if(!mIsError) {
                    for (BankInfo info : data) {
                        info.setColorIndex(colorIndex++);
                        cards.add(info);
                    }
                    firstIn = false;
                    BankInfo dummy = new BankInfo();
                    dummy.setBankCard("");
                    cards.add(dummy);
                    saveObjectToShared(WithdrawFragment.this.cards);
                    bindData();
                    notAtStart = true;
                    if (cards.size() > 1) {
                        bottom_ll.setVisibility(View.GONE);
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getData();
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    public void saveObjectToShared(List<BankInfo> cardList){

        if(mySharedPreferences == null){
            mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        Gson gson = new Gson();
        String json = gson.toJson(cardList);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("cards", json);
        editor.apply();
    }

    private List<BankInfo> readObjectFromShared(){

        if(mySharedPreferences == null){
            mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        Gson gson = new Gson();
        String json = mySharedPreferences.getString("cards", null);

        Type type = new TypeToken<ArrayList<BankInfo>>() {}.getType();
        return gson.fromJson(json, type);

    }

    public void bindData(){
        adapter = new CommonAdapter<BankInfo>(getActivity(), cards, R.layout.gridview_item_card) {
            @Override
            public void convert(ViewHolder helper, BankInfo item, final int position) {

                final BankInfo item_copy = item;
                RelativeLayout cardView = helper.getView(R.id.cardView);
                ImageView cardBg = helper.getView(R.id.imgBankcard);
                ImageView cardDel = helper.getView(R.id.imgBankcardDel);
                TextView cardBanker = helper.getView(R.id.cardBanker);
                TextView cardHolder = helper.getView(R.id.cardHolder);
                TextView cardNumber = helper.getView(R.id.cardNumber);

                if(item.getBankCard().compareTo("") == 0){

                    cardBg.setImageResource(R.mipmap.icon_bankcard_add);
                    cardHolder.setText(R.string.withdraw_add);
                    cardHolder.setTextColor(getResources().getColor(R.color.gray));
                    cardNumber.setVisibility(View.INVISIBLE);
                    cardBanker.setVisibility(View.INVISIBLE);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent next = new Intent(getActivity(), FormActivity.class);
                            next.putExtra("tag", 3);
                            next.putExtra("btn_type", 1);
                            startActivity(next);
                        }
                    });
                    cardDel.setVisibility(View.GONE);
                }else{
                    cardBg.setImageResource(cardBgs[Integer.valueOf(item.getColorIndex())%5]);
                    cardHolder.setText(item.getCardUser());
                    cardNumber.setText(item.getBankCard());
                    cardBanker.setText(item.getBankName());

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent next = new Intent(getActivity(), FormActivity.class);
                            next.putExtra("tag", 4);
                            next.putExtra("name", item_copy.getCardUser());
                            next.putExtra("bank", item_copy.getBankName());
                            next.putExtra("cardNo", item_copy.getBankCard());
                            next.putExtra("bankId", item_copy.getBankId());
                            next.putExtra("bankbranch", item_copy.getBankBranch());
                            next.putExtra("bankcity", item_copy.getBankCity());
                            next.putExtra("phone", item_copy.getPhone());
                            next.putExtra("bankprovince", item_copy.getBankProvince());
                            next.putExtra("banktypeid",item_copy.getBankTypeId());
                            Log.i("transfer","lala" + item_copy.getBankTypeId());
                            startActivity(next);
                        }
                    });
                }

                if(manageMode && item.getBankCard().compareTo("") != 0)
                    cardDel.setVisibility(View.VISIBLE);
                else
                    cardDel.setVisibility(View.GONE);

                cardDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cards.remove(position);
                        adapter.notifyDataSetChanged();
                        saveObjectToShared(cards);
                    }
                });
            }

        };
        myCards.setAdapter(adapter);

    }

}
