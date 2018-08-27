package com.hec.app.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hec.app.R;

public class AddSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_success);
        String name = getIntent().getStringExtra("name");
        String pw = getIntent().getStringExtra("password");

        ((TextView) findViewById(R.id.add_new_name)).setText(" : " + name);
        ((TextView) findViewById(R.id.add_new_password)).setText(" : " + pw);
    }

    public void backClick (View v){
        finish();
    }

}
