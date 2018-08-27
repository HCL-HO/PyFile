package com.hec.app.webservice;

import com.hec.app.entity.LotteryHistoryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hec on 2015/11/11.
 */
public class PolyGameService extends BaseService {


    public List<LotteryHistoryInfo> getLatestLotteryResult() {
        int issueNo = 151113062;
        List<LotteryHistoryInfo> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LotteryHistoryInfo info = new LotteryHistoryInfo(String.valueOf(issueNo++), "02 03 05 06", "组三", "组六");
            result.add(info);
        }
        return result;
    }
}
