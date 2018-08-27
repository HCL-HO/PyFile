package com.hec.app.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.entity.ArticleCategoryInfo;
import com.hec.app.entity.BizException;
import com.hec.app.fragment.ArticalFragment;
import com.hec.app.fragment.LaohujiFragment;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.ArticleService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArticalActivity extends BaseActivityWithMenu {

    private ArticalFragment articalFragment;
    private ImageView imgPerson;
    private Spinner title;
    private ImageView back,write_article;
    private ResideMenu resideMenu;
    private TitleItemSelectedListener titleItemSelectedListener;
    private boolean mIsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artical);
        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
        back = (ImageView) findViewById(R.id.imgBack);
        write_article = (ImageView) findViewById(R.id.img_wirte_article);
        write_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.redirectToNextNewActivity(ArticalActivity.this,WriteArticleActivity.class);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = (Spinner) findViewById(R.id.activity_title);

        getArticleCategory();
        FragmentManager fm = getSupportFragmentManager();
        articalFragment = (ArticalFragment) fm.findFragmentById(R.id.id_fragment_container1);
        if(articalFragment == null ) {
            articalFragment = new ArticalFragment();
            fm.beginTransaction().add(R.id.id_fragment_container1, articalFragment).commit();
        }
    }

    private void getArticleCategory(){
        mIsError = false;
        MyAsyncTask<List<ArticleCategoryInfo>> task = new MyAsyncTask<List<ArticleCategoryInfo>>(ArticalActivity.this) {
            @Override
            public List<ArticleCategoryInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().getArticleCategory();
            }

            @Override
            public void onLoaded(List<ArticleCategoryInfo> results) throws Exception {
                    Log.i("act","size: " + results.size());
                    List<String> list = new ArrayList<>();
                    for(ArticleCategoryInfo a : results){
                        list.add(a.getName());
                    }
                    list.add("我的文章");
                    list.add("我的收藏");
                    list.add("热门文章");
                    title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            titleItemSelectedListener.click(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ArticalActivity.this,R.layout.title_select_layout,list);
                    title.setAdapter(arrayAdapter);

            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                Log.i("act","error");
                mIsError = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public interface TitleItemSelectedListener{
        void click(int position);
    }

    public void setOnTitleItemSelectedListener(TitleItemSelectedListener titleItemSelectedListener){
        this.titleItemSelectedListener = titleItemSelectedListener;
    }

}
