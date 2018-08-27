package com.hec.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.BulletDetailActivity;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.BulletAdapter;
import com.hec.app.entity.BizException;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.framework.adapter.BulletHolder;
import com.hec.app.framework.adapter.ItemClickListener;
import com.hec.app.framework.adapter.RecyclerOnScrollListener;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A placeholder fragment containing a simple view.
 */
public class AnnouncementFragment extends Fragment {
    private boolean mIsError;
    RecyclerView mRecyclerView;
        BulletAdapter mBulletAdapter;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private int lastVisibleItem;
    private int PAGE_INDEX=0;
    private final int PAGE_SIZE = 6;
    private final int DOWM_REFRESH = 0;
    private final int UP_REFRESH = 1;
    private ProgressDialog progressDialog;
    private List<BulletinInfo> finallist =new ArrayList<>();
    private View v;
    public AnnouncementFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_announcement, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.bullet_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mBulletAdapter = new BulletAdapter(getContext());
        getLatestNews(PAGE_INDEX,PAGE_SIZE,UP_REFRESH);
        mBulletAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BulletDetailActivity.class);
                finallist = mBulletAdapter.sendfinallist();
                mBulletAdapter.sendhaveclickposition(position);
                intent.putExtra("iteminfo", finallist.get(position));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mBulletAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PAGE_INDEX = 0;
                getLatestNews(PAGE_INDEX, PAGE_SIZE, DOWM_REFRESH);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mBulletAdapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mBulletAdapter.getItemCount()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PAGE_INDEX = PAGE_INDEX + PAGE_SIZE;
                                getLatestNews(PAGE_INDEX, PAGE_SIZE, UP_REFRESH);
                                if (mBulletAdapter != null)
                                    mBulletAdapter.notifyDataSetChanged();
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mBulletAdapter.notifyDataSetChanged();
    }

    private void getLatestNews(final int pageindex, final int pagesize, final int operatetype) {
        showProgressDialog("正在加载公告");
        mIsError = false;
        MyAsyncTask<List<BulletinInfo>> task = new MyAsyncTask<List<BulletinInfo>>(getActivity()) {
            @Override
            public List<BulletinInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new HomeService().getBulletins(pageindex,pagesize);
            }
            @Override
            public void onLoaded(List<BulletinInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                if (!mIsError) {
                   switch (operatetype) {
                       case UP_REFRESH:
                           mBulletAdapter.addlist(result) ;
                           mBulletAdapter.notifyDataSetChanged();
                           break;
                       case DOWM_REFRESH:
                           mBulletAdapter.addLatestbulletins(result, getActivity());
                           mBulletAdapter.notifyDataSetChanged();
                           break;
                   }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getLatestNews(pageindex, pagesize, operatetype);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
                closeProgressDialog();
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                Log.i("wxj","setOnError");
                closeProgressDialog();
                mIsError = true;
            }
        });
        task.executeTask();

    }

    public void onDestroyView() {
        super.onDestroyView();

        mBulletAdapter = null;
    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(getContext(), loadingMessage);
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

}
