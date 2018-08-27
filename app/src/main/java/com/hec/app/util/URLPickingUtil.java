package com.hec.app.util;

/**
 * Created by wangxingjian on 2016/10/17.
 */

public class URLPickingUtil {
    private static URLPickingUtil instance = null;
    private String vipMqUrl;

    public static URLPickingUtil getInstance(){
        if(instance == null){
            instance = new URLPickingUtil();
        }
        return instance;
    }

    public String getVipMqUrl() {
        return vipMqUrl;
    }

    public void setVipMqUrl(String vipMqUrl) {
        this.vipMqUrl = vipMqUrl;
    }
}
