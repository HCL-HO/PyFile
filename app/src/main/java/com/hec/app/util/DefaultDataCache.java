package com.hec.app.util;

import android.content.Context;
import android.util.Log;

import com.hec.app.lottery.LotteryConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jhezenhu on 2017/6/9.
 */

public class DefaultDataCache {

    public static final int  BASEICDATA_CACHE = 0;
    public static final int  ALLPLAYCONFIG_CACHE = 1;

    public static final Map<Integer, String> CACHE_DATA_NAME_MAP = new HashMap<Integer, String>(){{
        put(BASEICDATA_CACHE, "BasicDataCache.json");
        put(ALLPLAYCONFIG_CACHE, "AllPlayConfigCache.json");
    }};

    public static String getDataCache(Context context, int type) {
        String result = "";
        String fileName = "";

        if (CACHE_DATA_NAME_MAP.containsKey(type)) {
            fileName = CACHE_DATA_NAME_MAP.get(type);
        }
        else {
            return result;
        }

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            if (inputStream != null) {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();

                result = new String(bytes);
            }
            else {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
