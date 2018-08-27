package com.hec.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;

public class AliNavigateActivity extends BaseActivity {
    private boolean[] itemShowArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_navigate);
        itemShowArray = getIntent().getBooleanArrayExtra("itemShowArray");
        if (itemShowArray == null) {
            itemShowArray = new boolean[] {true, true};
        }
        initView();
    }

    private void initView(){
        LinearLayout alitobank = (LinearLayout) findViewById(R.id.ll_alitobank2);
        LinearLayout aliali = (LinearLayout) findViewById(R.id.ll_aliali2);
        alitobank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AliNavigateActivity.this,RechargeAlipayActivity.class);
                startActivity(intent);
                finish();
            }
        });
        aliali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AliNavigateActivity.this,RechargeAlipayActivity.class);
                intent.putExtra("tag",1);
                startActivity(intent);
            }
        });
        LinearLayout block = (LinearLayout) findViewById(R.id.ll_alitobank);
        if (!itemShowArray[0]) {
            alitobank.setVisibility(View.GONE);
            block.setVisibility(View.INVISIBLE);
        } else if (!itemShowArray[1]) {
            aliali.setVisibility(View.GONE);
            block.setVisibility(View.INVISIBLE);
        }
    }

    public void backClick (View v){
        finish();
    }
}
