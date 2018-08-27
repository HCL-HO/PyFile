package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.List;

/**
 * Created by hec on 2015/10/30.
 */
public class BrowseResultInfo implements HasPageInfo, HasCollection<NewsInfo> {
    @SerializedName("NewsListItems")
    private List<NewsInfo> mNewsListItems;

    @SerializedName("NoResultItemList")
    private List<NewsInfo> mNoResultItems;

    @SerializedName("PageInfo")
    private PageInfo mPageInfo;


    public List<NewsInfo> geNewsListItems() {
        return mNewsListItems;
    }

    public void setNewsListItems(List<NewsInfo> productListItems) {
        mNewsListItems = productListItems;
    }

    @Override
    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        mPageInfo = pageInfo;
    }


    @Override
    public Collection<NewsInfo> getList() {
        return mNewsListItems;
    }

    public void setNoResultItems(List<NewsInfo> noResultItems) {
        mNoResultItems = noResultItems;
    }

    public List<NewsInfo> getNoResultItems() {
        return mNoResultItems;
    }
}
