package com.hec.app.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.hec.app.entity.BizException;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

/**
 * Created by wangxingjian on 2016/12/19.
 */

public abstract class PingFirst extends MyAsyncTask {

    public PingFirst(Context mContext){
        this.mContext = mContext;
    }
    @Override
    public Object callService() throws IOException, JsonParseException, BizException, ServiceException {
        Log.i("speed","ping call service");
        return new HomeService().testURLSpeed(BaseService.getRestfulServiceHost());
    }

    @Override
    public void onLoaded(Object paramT) throws Exception {
        Log.i("speed","in onloaded");
        afterPing();
    }

    public abstract void afterPing();
}
