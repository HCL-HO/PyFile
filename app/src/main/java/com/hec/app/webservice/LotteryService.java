package com.hec.app.webservice;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.AllPlayConfig;
import com.hec.app.entity.BallRebates;
import com.hec.app.entity.BasicDataInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.ChattingRoomInfo;
import com.hec.app.entity.CurrentLotteryInfo;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.LayoutInfo;
import com.hec.app.entity.LotteryDrawResultInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.entity.MMCInfo;
import com.hec.app.entity.NewRebateQueryCriteria;
import com.hec.app.entity.NextIssueInfoNew;
import com.hec.app.entity.PlaceOrderInfo;
import com.hec.app.entity.PlayConfig;
import com.hec.app.entity.PlayTypeInfo;
import com.hec.app.entity.PlayTypeNumInfo;
import com.hec.app.entity.PlayTypeRadioInfo;
import com.hec.app.entity.RebateQueryCriteria;
import com.hec.app.entity.Response;
import com.hec.app.entity.SelectListItem;
import com.hec.app.entity.SysSettings;
import com.hec.app.entity.TrendHistoryInfo;
import com.hec.app.framework.cache.MyFileCache;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.StringUtil;
import com.hec.app.util.TestUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hec on 2015/11/17.
 */
public class LotteryService extends BaseService {
    private static String OFTEN_PLAY_KEY = "HEC_OFEN_PLAY";
    public static List<BasicDataInfo> basicDataInfo;
    public static AllPlayConfig allPlayConfigInfo;
    private int defaultSize = 50;
    private int LOCAL_LOTTERY_NO = 29;//this indicates the level that UI can handle the new lotteries

    public List<BasicDataInfo> getBasicData(String hashCode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/ChongQing/basicdatacache");
        builder.appendQueryParameter("lotteryid", "" + LOCAL_LOTTERY_NO);
        builder.appendQueryParameter("playmode", "2");
        builder.appendQueryParameter("hashCode", hashCode);

        String url = builder.build().toString();
        String html = read(url);

        try {
            if (html.contains("重庆时时彩")) {
                MyFileCache.getInstance().put(CommonConfig.KEY_BASICDATA_EXPERT, html);
                Log.i("wxj", "korea " + html);
            }
        } catch (OutOfMemoryError OOM) {
            //TODO:nothing.
        }

        Type messageType = new TypeToken<Response<List<BasicDataInfo>>>() {
        }.getType();
        basicDataInfo = getResult(html, messageType);

        return basicDataInfo;
    }

    // hashCode值為“”或不傳就會拿到PlayConfig
    public AllPlayConfig getAllPlayConfig(String hashCode) throws IOException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/home/GetAllPlayConfig");
        builder.appendQueryParameter("hashCode", hashCode);

        String url = builder.build().toString();
        String html = read(url);

        try {
            if (html.contains("五星")) {
                MyFileCache.getInstance().put(CommonConfig.KEY_ALLPLAYCONFIG, html);
            }
        } catch (OutOfMemoryError OOM) {
            //TODO:nothing.
        }

        Type messageType = new TypeToken<Response<AllPlayConfig>>() {
        }.getType();
        AllPlayConfig allPlayConfig = getResult(html, messageType);

        if (allPlayConfig.getSSC() != null && !allPlayConfig.getSSC().isEmpty() ||
            allPlayConfig.getSelectFive() != null && !allPlayConfig.getSelectFive().isEmpty() ||
            allPlayConfig.getPK10() != null && !allPlayConfig.getPK10().isEmpty() ||
            allPlayConfig.getWelfare3D() != null && !allPlayConfig.getWelfare3D().isEmpty() ||
            allPlayConfig.getKuaiSan() != null && !allPlayConfig.getKuaiSan().isEmpty()) {
            allPlayConfigInfo = getCachedAllPlayConfigInfo();
        } else {
            allPlayConfigInfo = getResult(html, messageType);
        }

        return allPlayConfigInfo;
    }

    public BasicDataInfo getCachedBasicDataInfo(int playMode) {
        if (basicDataInfo == null) {
            return getBasicDataInfo(playMode);
        } else {
            if (basicDataInfo.size() <= 0) {
                return getBasicDataInfo(playMode);
            }

            for (BasicDataInfo info : basicDataInfo) {
                if (info.getPlayMode() == playMode) {
                    return info;
                }
            }
        }

        return null;
    }

    public BasicDataInfo getBasicDataInfo(int playMode) {
        Gson gson = new Gson();
        String basicData = MyFileCache.getInstance().get(CommonConfig.KEY_BASICDATA_EXPERT);

        if (!StringUtil.isEmpty(basicData)) {
            Type messageType = new TypeToken<Response<List<BasicDataInfo>>>() {
            }.getType();
            Response<List<BasicDataInfo>> response = gson.fromJson(basicData, messageType);

            basicDataInfo = response.getData();
            for (BasicDataInfo info : basicDataInfo) {
                if (info.getPlayMode() == playMode) {
                    return info;
                }
            }
        }

        return null;
    }

    public AllPlayConfig getCachedAllPlayConfigInfo() {
        if (allPlayConfigInfo == null) {
            return getAllPlayConfigInfo();
        }

        return allPlayConfigInfo;
    }

    public AllPlayConfig getAllPlayConfigInfo() {
        Gson gson = new Gson();
        String allPlayConfig = MyFileCache.getInstance().get(CommonConfig.KEY_ALLPLAYCONFIG);

        if (!StringUtil.isEmpty(allPlayConfig)) {
            Type messageType = new TypeToken<Response<AllPlayConfig>>() {
            }.getType();
            Response<AllPlayConfig> response = gson.fromJson(allPlayConfig, messageType);

            allPlayConfigInfo = response.getData();
            return allPlayConfigInfo;
        }

        return null;
    }

    public List<PlayConfig> getPlayConfigInfo(int lotteryId) {
        if (allPlayConfigInfo == null) {
            return null;
        }

        switch (lotteryId) {
            case LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME:
            case LotteryConfig.LOTTERY_ID.HS_REALTIME:
            case LotteryConfig.LOTTERY_ID.XINJIANG_REALTIME:
            case LotteryConfig.LOTTERY_ID.HS_SF_REAMTIME:
            case LotteryConfig.LOTTERY_ID.HS_MMC:
            case LotteryConfig.LOTTERY_ID.TIANJIN_REALTIME:
            case LotteryConfig.LOTTERY_ID.TAIWAN_WF_REALTIME:
            case LotteryConfig.LOTTERY_ID.BEIJING_KENO:
            case LotteryConfig.LOTTERY_ID.KOREA_WF_REALTIME:
            case LotteryConfig.LOTTERY_ID.QQ_REAMTIME:
            case LotteryConfig.LOTTERY_ID.ITALY_REAMTIME:
                return allPlayConfigInfo.getSSC();
            case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
            case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
            case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                return allPlayConfigInfo.getSelectFive();
            case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
            case LotteryConfig.LOTTERY_ID.HS_PK10:
            case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
            case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
            case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                return allPlayConfigInfo.getPK10();
            case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
            case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                return allPlayConfigInfo.getWelfare3D();
            case LotteryConfig.LOTTERY_ID.JIANG_SU_KUAI_SAN:
                return allPlayConfigInfo.getKuaiSan();
        }

        return null;
    }

    public SysSettings getSysSettings(int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        if (data == null)
            return null;
        return data.getSysSettings();
    }

    public List<LotteryInfo> getLotteryInfo(int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        if (data == null)
            return null;
        return data.getLotteryInfos();
    }

    public LotteryInfo getLotteryInfo(int lotteryID, int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        if (data == null)
            return null;
        List<LotteryInfo> list = data.getLotteryInfos();
        if (list != null) {
            for (LotteryInfo l :
                    list) {
                if (lotteryID == l.getLotteryID()) {
                    return l;
                }
            }
        }
        TestUtil.print("list is null");
        return null;
    }

    public List<PlayTypeInfo> getPlayTypesInfo(int lotteryID, int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        if (data == null)
            return null;
        List<PlayTypeInfo> list = new ArrayList<>();
        for (PlayTypeInfo p :
                data.getPlayTypes()) {
            if (p.getLotteryID() == lotteryID) {
                list.add(p);
            }
        }
        return list;
    }

    public boolean isClassicPlayTypesInfo(int lotteryID) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(LotteryConfig.PLAY_MODE.CLASSIC);
        if (data == null) {
            return false;
        }

        List<PlayTypeInfo> list = new ArrayList<>();
        for (PlayTypeInfo p : data.getPlayTypes()) {
            if (p.getLotteryID() == lotteryID) {
                list.add(p);
            }
        }

        if (list.size() <= 0) {
            return false;
        }

        return true;
    }

    public PlayTypeInfo getPlayTypeInfo(int playTypeID, int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        if (data == null)
            return null;
        List<PlayTypeInfo> list = data.getPlayTypes();
        if (list != null) {
            for (PlayTypeInfo l :
                    list) {
                if (l.getPlayTypeID() == playTypeID) {
                    return l;
                }
            }
        }
        return null;
    }

    public List<PlayTypeRadioInfo> getPlayTypeRadiosInfo(int playTypeID, int playMode) throws
            IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        List<PlayTypeRadioInfo> list = new ArrayList<>();

        if (playMode == LotteryConfig.PLAY_MODE.CLASSIC) {
            for (PlayTypeRadioInfo p : data.getPlayTypeRadios()) {
                if (p.getPlayTypeID() == playTypeID && !p.getPlayTypeRadioName().contains("单式")) {
                    list.add(p);
                }
            }
        } else {
            for (PlayTypeRadioInfo p : data.getPlayTypeRadios()) {
                if (p.getPlayTypeID() == playTypeID) {
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<PlayTypeRadioInfo> getPlayTypeRadiosInfoALL(int playTypeID, int playMode) throws
            IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        List<PlayTypeRadioInfo> list = new ArrayList<>();
        for (PlayTypeRadioInfo p :
                data.getPlayTypeRadios()) {
            if (p.getPlayTypeID() == playTypeID) {
                list.add(p);
            }
        }
        return list;
    }

    public PlayTypeRadioInfo getPlayTypeRadioInfo(int playTypeRadioID, int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        if (data == null)
            return null;
        List<PlayTypeRadioInfo> list = data.getPlayTypeRadios();
        if (list != null) {
            for (PlayTypeRadioInfo l :
                    list) {
                if (l.getPlayTypeRadioID() == playTypeRadioID) {
                    return l;
                }
            }
        }
        return null;
    }

    public List<PlayTypeNumInfo> getPlayTypeNumsInfo(int playTypeRadioID, int playMode) throws IOException, JsonParseException, ServiceException {
        BasicDataInfo data = getCachedBasicDataInfo(playMode);
        List<PlayTypeNumInfo> list = new ArrayList<>();
        for (PlayTypeNumInfo p : data.getPlayTypeNums()) {
            if (p.getPlayTypeRadioId() == playTypeRadioID) {
                list.add(p);
            }
        }
        return list;
    }

    public LayoutInfo getLayoutInfo(int lotteryID, int playTypeID, int playTypeRadioID, int playMode) throws IOException, JsonParseException, ServiceException {
        LotteryInfo lotteryInfo = getLotteryInfo(lotteryID, playMode);
        Log.i("receiverok", lotteryInfo.getLotteryType() + " in layout");
        if (lotteryInfo == null) {
            return null;
        }

        List<PlayTypeInfo> playTypeInfos = getPlayTypesInfo(lotteryID, playMode);
        List<PlayTypeRadioInfo> playTypeRadioInfos = null;
        List<PlayTypeNumInfo> playTypeNumInfos = null;
        PlayTypeInfo playTypeInfo = null;
        PlayTypeRadioInfo playTypeRadioInfo = null;

        if (playTypeInfos != null && playTypeInfos.size() > 0) {
            if (playTypeID > 0) {
                playTypeInfo = getPlayTypeInfo(playTypeID, playMode);
            } else {
                for (PlayTypeInfo pt : playTypeInfos) {
                    if (pt.getPlayTypeName().contains("三星")) {
                        playTypeInfo = pt;
                        break;
                    }
                }
                if (playTypeInfo == null) {
                    playTypeInfo = playTypeInfos.get(0);
                }
            }

            playTypeRadioInfos = getPlayTypeRadiosInfo(playTypeInfo.getPlayTypeID(), playMode);
            if (playTypeRadioInfos != null && playTypeRadioInfos.size() > 0) {
                if (playTypeRadioID > 0) {
                    playTypeRadioInfo = getPlayTypeRadioInfo(playTypeRadioID, playMode);
                } else {
                    playTypeRadioInfo = playTypeRadioInfos.get(0);
                }

                playTypeNumInfos = getPlayTypeNumsInfo(playTypeRadioInfo.getPlayTypeRadioID(), playMode);
            } else {
                for (PlayTypeInfo pt : playTypeInfos) {
                    playTypeRadioInfos = getPlayTypeRadiosInfo(pt.getPlayTypeID(), playMode);
                    if (playTypeRadioInfos != null && playTypeRadioInfos.size() > 0) {
                        if (playTypeRadioID > 0) {
                            playTypeRadioInfo = getPlayTypeRadioInfo(playTypeRadioID, playMode);
                        } else {
                            playTypeRadioInfo = playTypeRadioInfos.get(0);
                        }

                        playTypeNumInfos = getPlayTypeNumsInfo(playTypeRadioInfo.getPlayTypeRadioID(), playMode);
                        playTypeInfo = pt;
                        break;
                    }
                }
            }
        }

        int minNumber = 0;
        int maxNumber = 9;
        if (lotteryInfo.getLotteryType().contains("十一选五")) {
            minNumber = 1;
            maxNumber = 11;
        } else if (lotteryInfo.getLotteryType().contains("PK")) {
            minNumber = 1;
            maxNumber = 10;
        } else if (lotteryInfo.getLotteryType().contains("快三")) {
            minNumber = 1;
            maxNumber = 6;
        }

        LayoutInfo info = new LayoutInfo(minNumber, maxNumber, playTypeNumInfos, lotteryInfo, playTypeInfo, playTypeRadioInfo);
        return info;
    }

    public LotteryDrawResultInfo getNextIssueNo(int lotteryID, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(lotteryID, playMode);
        b.path("/" + controller + "/nextissueno");
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<LotteryDrawResultInfo>>() {
        }.getType();
        LotteryDrawResultInfo info = getResult(html, messageType);
        Log.i("start", "nextissue");
        return info;
    }

    private String getController(int lotteryID, int playMode) throws IOException, JsonParseException, ServiceException {
        List<LotteryInfo> list = getLotteryInfo(playMode);
        if (list == null) {
            return "";
        }
        String controller = "";
        for (LotteryInfo l :
                list) {
            if (l.getLotteryID() == lotteryID) {
                controller = l.getTypeUrl();
                break;
            }
        }
        return controller;
    }

    // 快三和值彩球的賠率
    public List<BallRebates> getBallRebateList(RebateQueryCriteria criteria, int lotteryID) throws IOException, JsonParseException, ServiceException, BizException {
        String controller = getController(lotteryID, LotteryConfig.PLAY_MODE.CLASSIC);

        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + controller + "/PlayBallShow");
        b.appendQueryParameter("playTypeID", "" + criteria.getPlayTypeID());
        b.appendQueryParameter("playTypeName", criteria.getPlayTypeName());
        b.appendQueryParameter("playTypeRadio", criteria.getPlayTypeRadioName());
        b.appendQueryParameter("lotteryTypeID", String.valueOf(lotteryID));

        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BallRebates>>>() {
        }.getType();

        return getResult(html, messageType);
    }

    // 經典模式
    public List<SelectListItem> getRebateSelectList(RebateQueryCriteria criteria, int lotteryID) throws IOException, JsonParseException, ServiceException, BizException {
        String controller = getController(lotteryID, LotteryConfig.PLAY_MODE.CLASSIC);

        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + controller + "/rebates");
        b.appendQueryParameter("playTypeID", "" + criteria.getPlayTypeID());
        b.appendQueryParameter("playTypeName", criteria.getPlayTypeName());
        b.appendQueryParameter("playTypeRadio", criteria.getPlayTypeRadioName());
        b.appendQueryParameter("lotteryTypeID", String.valueOf(lotteryID));

        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<SelectListItem>>>() {
        }.getType();

        return getResult(html, messageType);
    }

    // 專家模式
    public List<SelectListItem> getNewRebateSelectList(NewRebateQueryCriteria criteria, int lotteryID) throws IOException, JsonParseException, ServiceException, BizException {
        String controller = getController(lotteryID, LotteryConfig.PLAY_MODE.EXPERT);

        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + controller + "/newrebates");
        b.appendQueryParameter("playTypeID", "" + criteria.getPlayTypeID());
        b.appendQueryParameter("PlayTypeRadioID", "" + criteria.getPlayTypeRadioID());
        b.appendQueryParameter("playTypeName", criteria.getPlayTypeName());
        b.appendQueryParameter("playTypeRadio", criteria.getPlayTypeRadioName());

        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<SelectListItem>>>() {
        }.getType();

        return getResult(html, messageType);
    }

    public Response<?> placeOrder(PlaceOrderInfo orderInfo, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(orderInfo.getLotteryID(), playMode);
        b.path("/" + controller + "/PlaceOrder");
        b.appendQueryParameter("v", String.valueOf(System.currentTimeMillis()));
        String body = g.toJson(orderInfo);
        TestUtil.print("order body :" + body);
        String url = b.build().toString();
        String html = create(url, body);
        Type messageType = new TypeToken<Response<?>>() {
        }.getType();
        Response<?> r = g.fromJson(html, messageType);
        return r;
    }

    public List<LotteryInfo> getOftenPlayLotteryInfo(int playMode) throws IOException, JsonParseException, ServiceException {
        Gson g = new Gson();
        String oftenPlay = MyFileCache.getInstance().get(OFTEN_PLAY_KEY);
        List<LotteryInfo> list = getLotteryInfo(playMode);

        if (list == null) {
            return null;
        }

        if (!StringUtil.isEmpty(oftenPlay)) {
            Type messageType = new TypeToken<List<LotteryInfo>>() {
            }.getType();
            List<LotteryInfo> res = g.fromJson(oftenPlay, messageType);
            List<LotteryInfo> res_filtered = new ArrayList<>();

            for (LotteryInfo lot : res) {
                Boolean has = false;
                for (LotteryInfo l : list) {
                    if (l.getLotteryID() == lot.getLotteryID() ||
                            lot.getLotteryID() == LotteryConfig.LOTTERY_ID.SLOT ||
                             lot.getLotteryID() == LotteryConfig.LOTTERY_ID.BAIJIALE) {
                        has = true;
                        break;
                    }
                }

                if (has) {
                    res_filtered.add(lot);
                }
            }
            return res_filtered;
        }

        return null;
    }

    public void setOftenPlayLotteryInfo(List<LotteryInfo> list) {
        if (list != null) {
            Gson g = new Gson();
            String json = g.toJson(list);
            MyFileCache.getInstance().put(OFTEN_PLAY_KEY, json);
        }
    }

/*    public List<CurrentLotteryInfo> GetLotteryHistory(int lotteryID) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(lotteryID);
        b.path("/" + controller + "/GetLotteryHistory");

        String url = b.build().toString();
        String html = read(url);

        Type messageType = new TypeToken<Response<List<CurrentLotteryInfo>>>() {
        }.getType();
        return getResult(html, messageType);
    }*/

    public List<TrendHistoryInfo> GetTrendHistory(int lotteryID, int searchType, int page, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(lotteryID, playMode);
        b.path("/" + controller + "/trendnew");
        b.appendQueryParameter("type", Integer.toString(searchType + 1));
        b.appendQueryParameter("offset", Integer.toString(getOffset(page, defaultSize)));
        b.appendQueryParameter("limit", Integer.toString(defaultSize));
        String url = b.build().toString();
        String html = read(url);
        Log.i("wxj", "korea " + html);
        if (lotteryID != 19 && lotteryID != 20 && page == 0 && searchType == 1 && defaultSize == 10) {
            if (CustomerAccountManager.getInstance().getCustomer() != null) {
                MyFileCache.getInstance().put(String.valueOf(lotteryID + CustomerAccountManager.getInstance().getCustomer().getUserName()), html);
            }
        }
        Type messageType = new TypeToken<Response<List<TrendHistoryInfo>>>() {
        }.getType();
        List<TrendHistoryInfo> list = getResult(html, messageType);
        return list;
    }

    public List<TrendHistoryInfo> getTrendFromLocal(int lotteryID) {
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer == null || customer.getUserName() == null) {
            return null;
        }

        Gson g = new Gson();
        String trendinfo = MyFileCache.getInstance().get(String.valueOf(lotteryID) + customer.getUserName());
        if (!StringUtil.isEmpty(trendinfo)) {
            Type messageType = new TypeToken<Response<List<TrendHistoryInfo>>>() {
            }.getType();
            try {
                Response<List<TrendHistoryInfo>> r = g.fromJson(trendinfo, messageType);
                return r.getData();
            } catch (Exception e) {
                MyFileCache.getInstance().put(String.valueOf(lotteryID) + customer.getUserName(), null);
            }
            return null;
        } else {
            return null;
        }
    }

    public int getOffset(int pageNum, int size) {
        return pageNum * size;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int val) {
        defaultSize = val;
    }

    public Response<?> afterBet(PlaceOrderInfo orderInfo, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(orderInfo.getLotteryID(), playMode);
        b.path("/" + controller + "/placeafter");
        b.appendQueryParameter("v", String.valueOf(System.currentTimeMillis()));
        String body = g.toJson(orderInfo);
        String url = b.build().toString();
        String html = create(url, body);
        Log.i("wxj", "korea " + body);
        Type messageType = new TypeToken<Response<?>>() {
        }.getType();
        Response<?> r = g.fromJson(html, messageType);
        return r;
    }

    public List<CurrentLotteryInfo> GetLotteryHistory(int lotteryID, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(lotteryID, playMode);
        b.path("/" + controller + "/history");

        String url = b.build().toString();
        String html = read(url);

        Type messageType = new TypeToken<Response<List<CurrentLotteryInfo>>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public Response<Map<String, String>> getAfterTotalAmount(PlaceOrderInfo orderInfo, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String controller = getController(orderInfo.getLotteryID(), playMode);
        b.path("/" + controller + "/computeafter");
        String body = g.toJson(orderInfo);
        String url = b.build().toString();
        String html = create(url, body);
        Type messageType = new TypeToken<Response<Map<String, String>>>() {
        }.getType();
        Response<Map<String, String>> r = g.fromJson(html, messageType);
        return r;
    }

    public Response openMMCLottery(int lotteryID, String issueNo, int iType, int multiple, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response>() {
        }.getType();
        String controller = getController(lotteryID, playMode);
        b.path("/" + controller + "/openmmc");
        String url = b.build().toString();
        Gson g = new Gson();
        MMCInfo info = new MMCInfo(issueNo, iType, multiple);
        String html = create(url, g.toJson(info));
        Log.d("html", iType + " " + issueNo + " " + multiple);
        return (new Gson()).fromJson(html, messageType);
    }

    public MMCInfo checkMMCLottery(int lotteryID, String issueNo, int playMode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response<MMCInfo>>() {
        }.getType();
        String controller = getController(lotteryID, playMode);
        b.path("/" + controller + "/checkmmc");
        b.appendQueryParameter("issueno", issueNo);
        String url = b.build().toString();
        String html = read(url);
        return getResult(html, messageType);
    }

    public List<NextIssueInfoNew> getCommonNextIssue() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response<List<NextIssueInfoNew>>>() {
        }.getType();
        b.path("Common/NextIssueCache/");
        String url = b.build().toString();
        String html = read(url);
        Log.i("start", html);
        return getResult(html, messageType);
    }

    public NextIssueInfoNew getSingleCommonNextIssue(int lotteryid) throws IOException, JsonParseException, ServiceException, BizException {
        List<NextIssueInfoNew> list = getCommonNextIssue();
        if (list == null) {
            return null;
        }
        for (NextIssueInfoNew nextIssueInfoNew : list) {
            if (nextIssueInfoNew.getLotteryID() == lotteryid) {
                Log.i("start", "lotteryid" + lotteryid);
                return nextIssueInfoNew;
            }
        }
        return null;
    }

    public String gotoChattingRoom(String username, String ticket, String playtype) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse("http://192.168.0.111:8888/").buildUpon();
        b.path("/login/");
        String url = b.build().toString();
        Gson g = new Gson();
        String html = create(url, g.toJson(new ChattingRoomInfo("chongqing", username, "magic")));
        Log.i("wxj", "chat room " + html);
        return html;
    }
}

