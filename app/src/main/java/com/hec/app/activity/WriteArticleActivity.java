package com.hec.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.entity.BizException;
import com.hec.app.entity.PosrArticleInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.ArticleService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

public class WriteArticleActivity extends BaseActivityWithMenu {

    private ImageView back;
    private Button btn_post;
    private EditText et_title, et_content;
    private boolean mIsError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        back = (ImageView) findViewById(R.id.imgBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initView();
    }

    private void initView(){
        btn_post = (Button) findViewById(R.id.btn_post_article);
        et_title = (EditText) findViewById(R.id.et_write_article_title);
        et_content = (EditText) findViewById(R.id.et_write_article_content);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postArticle(et_title.getText().toString(),et_content.getText().toString());
            }
        });
    }

    private void postArticle(final String title,final String content){
        mIsError = false;
        MyAsyncTask<PosrArticleInfo> task = new MyAsyncTask<PosrArticleInfo>(this) {
            @Override
            public PosrArticleInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().postArticle(title,content);
            }
            @Override
            public void onLoaded(PosrArticleInfo result) throws Exception {
                if(!mIsError){
                    if(result.isSuccess()){
                        MyToast.show(WriteArticleActivity.this,"发布成功");
                        WriteArticleActivity.this.finish();
                    }else{
                        MyToast.show(WriteArticleActivity.this,result.getMessage());
                    }
                }else{
                    MyToast.show(WriteArticleActivity.this,getErrorMessage());
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
