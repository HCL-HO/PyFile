package com.hec.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.NewsAdapter;
import com.hec.app.entity.BizException;
import com.hec.app.entity.BrowseResultInfo;
import com.hec.app.entity.HasCollection;
import com.hec.app.entity.NewsInfo;
import com.hec.app.framework.adapter.MyDecoratedAdapter;
import com.hec.app.framework.content.CBCollectionResolver;
import com.hec.app.framework.content.CollectionStateObserver;
import com.hec.app.framework.widget.ImageCycleView;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.PullToRefreshView;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.NewsService;
import com.hec.app.webservice.ServiceException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class HeadlinesFragment extends ListFragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    public interface OnArticleSelectedListener {
        void onArticleSelected(int articleID);
    }

    private OnArticleSelectedListener listener;
    private String TAG = HeadlinesFragment.class.getName();
    private List<NewsInfo> mData;
    private NewsAdapter adapter;
    private LinearLayout slide;
    private ListView listview;
    private ImageCycleView mAdView;
    private ArrayList<String> mImageUrl = null;
    private PullToRefreshView mPullToRefreshView;
    private boolean mIsError = false;
    private Handler mHandler;
    private MyNewsListAdapter mAdapter;
    private CBCollectionResolver<NewsInfo> mHeaderResolver;
    private CBCollectionResolver<NewsInfo> mFooterResolver;
    private CollectionStateObserver mObserver;


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        Log.i(TAG, "下拉刷新开始");

        //getMoreNews();

        footerRefresh();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        Log.i(TAG, "下拉加载刷新开始");

        //getLatestNews(true);
        headerRefresh();
    }

    @SuppressLint("HandlerLeak")
    private void setHandler() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 10:
                        mData = (List<NewsInfo>) msg.obj;
                        adapter = new NewsAdapter(getActivity(), R.layout.list_news, mData);
                        setListAdapter(adapter);
                        Log.i(TAG, "初次加载完成");
                        break;
                    case 100:
                        List<NewsInfo> list = (List<NewsInfo>) msg.obj;
                        mData.addAll(list);
                        adapter.notifyDataSetChanged();
                        mPullToRefreshView.onFooterRefreshComplete();
                        mPullToRefreshView.onHeaderRefreshComplete();
                        Log.i(TAG, "下拉刷新完成");
                        break;
                    case 200:
                        List<NewsInfo> more = (List<NewsInfo>) msg.obj;
                        mData.addAll(more);
                        adapter.notifyDataSetChanged();
                        mPullToRefreshView.onFooterRefreshComplete();
                        mPullToRefreshView.onHeaderRefreshComplete();
                        Log.i(TAG, "上拉加载完成");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.headline, container, false);
        findView(view);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHandler();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        NewsInfo newsInfo = (NewsInfo) getListAdapter().getItem(position - 1);

        if (listener != null) {
            listener.onArticleSelected(newsInfo.getArticleID());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listview = getListView();
        listview.addHeaderView(slide);

        getBanners();

        getData();

        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnArticleSelectedListener) {
            listener = (OnArticleSelectedListener) activity;
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdView.startImageCycle();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdView.pushImageCycle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdView.pushImageCycle();
    }

    private void getLatestNews(final boolean isRefresh) {
        mIsError = false;
        MyAsyncTask<List<NewsInfo>> task = new MyAsyncTask<List<NewsInfo>>(getActivity()) {

            @Override
            public List<NewsInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                Log.i(TAG, "调用服务开始");
                return new NewsService().getLatestNews();
            }

            @Override
            public void onLoaded(List<NewsInfo> result) throws Exception {
                if (!mIsError) {
                    Log.i(TAG, "调用服务结束");
                    if (isRefresh)
                        sendMessage(100, result);
                    else
                        sendMessage(10, result);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getLatestNews(isRefresh);
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

    private void getMoreNews() {
        mIsError = false;
        MyAsyncTask<List<NewsInfo>> task = new MyAsyncTask<List<NewsInfo>>(getActivity()) {

            @Override
            public List<NewsInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new NewsService().getMoreNews();
            }

            @Override
            public void onLoaded(List<NewsInfo> result) throws Exception {
                if (!mIsError) {
                    sendMessage(200, result);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getMoreNews();
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

    private void sendMessage(int meg, Object object) {
        Message message = new Message();
        message.what = meg;
        message.obj = object;
        mHandler.sendMessage(message);
    }

    private void getBanners() {
        mIsError = false;
        MyAsyncTask<List<String>> task = new MyAsyncTask<List<String>>(getActivity()) {

            @Override
            public List<String> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new NewsService().getBanners();
            }

            @Override
            public void onLoaded(List<String> result) throws Exception {
                if (!mIsError) {
                    mImageUrl = new ArrayList<>(result);
                    mAdView.setImageResources(mImageUrl, mAdCycleViewListener);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBanners();
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

    private void findView(View view) {
        slide = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.banner, null, false);
        mAdView = (ImageCycleView) slide.findViewById(R.id.adCycle);
        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.rl_modulename_refresh);
    }

    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {
            Toast.makeText(getActivity(), "position->" + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView);
        }
    };


    /*重构后*/
    private void getData() {
        mHeaderResolver = new CBCollectionResolver<NewsInfo>() {
            @Override
            public HasCollection<NewsInfo> query()
                    throws IOException, ServiceException, BizException {

                BrowseResultInfo info = new BrowseResultInfo();
                info.setNewsListItems(new NewsService().getLatestNews());

                Message msg = new Message();
                msg.what = 500;
                msg.obj = info;
                mHandler.sendMessage(msg);
                return info;
            }
        };
        mFooterResolver = new CBCollectionResolver<NewsInfo>() {
            @Override
            public HasCollection<NewsInfo> query()
                    throws IOException, ServiceException, BizException {

                BrowseResultInfo info = new BrowseResultInfo();
                info.setNewsListItems(new NewsService().getLatestNews());

                Message msg = new Message();
                msg.what = 500;
                msg.obj = info;
                mHandler.sendMessage(msg);
                return info;
            }
        };

        mObserver = new CollectionStateObserver();
        mObserver.setActivity(getActivity());
        mAdapter = new MyNewsListAdapter(getActivity(),R.layout.list_news);
        mAdapter.setVisible(true);
        setListAdapter(mAdapter);
        mObserver.setAdapters(mAdapter);
        mObserver.showContent();
        mObserver.setPullToRefreshView(mPullToRefreshView);

        headerRefresh();
    }

    private void headerRefresh() {
        mAdapter.startQuery(mHeaderResolver, true);
    }

    private void footerRefresh() {
        mAdapter.startQuery(mFooterResolver);
    }

    public class MyNewsListAdapter extends
            MyDecoratedAdapter<NewsInfo> {
        int resource;

        public MyNewsListAdapter(Context context, int _resource) {
            super(context);
            this.mContext = context;
            this.inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource=_resource;
        }

        private LayoutInflater inflater;
        private Context mContext;

        @Override
        protected View newErrorView(Context context, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_item_error, parent,
                    false);
            view.findViewById(R.id.retry).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            retry();
                        }
                    });

            return view;
        }

        @Override
        protected View newLoadingView(Context context, ViewGroup parent) {
            return inflater.inflate(R.layout.list_item_loading, parent, false);
        }

        @Override
        protected View newNormalView(int position, View convertView,
                                     ViewGroup parent) {
            LinearLayout newView;
            NewsInfo news = getItem(position);
            String title = news.getTitle();
            String summary = news.getSummary();
            if(convertView == null){
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater li;
                li = (LayoutInflater)getContext().getSystemService(inflater);
                li.inflate(resource, newView, true);
            }
            else{
                newView = (LinearLayout)convertView;
            }

            TextView titleView = (TextView)newView.findViewById(R.id.titleTextView);
            TextView contentView = (TextView)newView.findViewById(R.id.contentTextView);

            titleView.setText(title);
            contentView.setText(summary);

            return newView;
        }
    }
}
