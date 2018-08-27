package com.hec.app.util;

import android.app.Activity;
import android.content.Context;

import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.SettleInfo;
import com.hec.app.lottery.LotteryConfig;

import java.util.List;

/**
 * Created by hec on 2015/12/21.
 */
public class LotteryUtil {
    private static Context context;

    public LotteryUtil(Activity context) {
        LotteryUtil.context = context;
    }

    public static void addSettleInfoToLocalCache(List<String> ballList, List<String> unitList, double manualBet, int bets, String playTypeName, String playRadioName) {
        String current = "";
        for (String s : ballList) {
            current += s;
        }

        String currentUnit = "";
        for (String s : unitList) {
            currentUnit += s;
        }

        for (SettleInfo info : BaseApp.allSettleInfo) {
            String single = "";
            for (String ball : info.getBallList()) {
                single += ball;
            }
            String oldUnit = "";
            for (String unit : info.getUnitList()) {
                oldUnit += unit;
            }

            if ((playTypeName.equals("任二") || playTypeName.equals("任三") || playTypeName.equals("任四")) && !playRadioName.equals("直选复式")) {
                if (single.contains(current) && currentUnit.equals(oldUnit)) {
                    return;
                }
            } else {
                if (single.equals(current)) {
                    return;
                }
            }
        }

        SettleInfo info = new SettleInfo();
        info.setBallList(ballList);
        info.setUnitList(unitList);
        info.setManualBet(manualBet);
        info.setBets(bets);
        BaseApp.allSettleInfo.add(info);
    }

    // nums-幾注; priceABet-單注金額
    public static double getTotalAmount(double nums, double priceABet) {
        // 20180727 與IOS確認算法為注數＊單注金額
        return nums * priceABet;
    }

    public static void removeBetFromLocalCache(int position) {
        try {
            BaseApp.allSettleInfo.remove(position);
        } catch (Exception e) {
            TestUtil.print(e.toString());
        }
    }

    public static int getLotteryIcon(String item) {
        if (item == null) {
            return R.mipmap.icon_chongqing_ssc;
        }

        if (item.equals("Add")) {
            return R.mipmap.icon_add;
        }

        if (item.equals("Brand")) {
            return R.mipmap.icon_stargirl;
        }

        if (LotteryConfig.LOTTERY_ID.LOTTERY_ID_MAP.containsKey(item)) {
            switch (LotteryConfig.LOTTERY_ID.LOTTERY_ID_MAP.get(item)) {
                case LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME:
                    return R.mipmap.icon_chongqing_ssc;
                case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
                    return R.mipmap.icon_3d;
                case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                    return R.mipmap.icon_sports;
                case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
                    return R.mipmap.icon_guangdong_selectfive;
                case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
                    return R.mipmap.icon_shandong_selectfive;
                case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
                    return R.mipmap.icon_pk10;
                case LotteryConfig.LOTTERY_ID.HS_REALTIME:
                    return R.mipmap.icon_hec_ssc;
                case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                    return R.mipmap.icon_hec_selectfive;
                case LotteryConfig.LOTTERY_ID.XINJIANG_REALTIME:
                    return R.mipmap.icon_xinjiang_ssc;
                case LotteryConfig.LOTTERY_ID.HS_SF_REAMTIME:
                    return R.mipmap.icon_hec_sfc;
                case LotteryConfig.LOTTERY_ID.HS_PK10:
                    return R.mipmap.icon_hec_pk10;
                case LotteryConfig.LOTTERY_ID.JIANG_SU_KUAI_SAN:
                    return R.mipmap.icon_jsks;
                case LotteryConfig.LOTTERY_ID.HS_MMC:
                    return R.mipmap.icon_hesheng_mmc;
                case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
                    return R.mipmap.pk10_hesheng_mmc;
                case LotteryConfig.LOTTERY_ID.TIANJIN_REALTIME:
                    return R.mipmap.icon_tingin_ssc;
                case LotteryConfig.LOTTERY_ID.TAIWAN_WF_REALTIME:
                    return R.mipmap.icon_tw5fc;
                case LotteryConfig.LOTTERY_ID.BEIJING_KENO:
                    return R.mipmap.icon_bjkl8;
                case LotteryConfig.LOTTERY_ID.KOREA_WF_REALTIME:
                    return R.mipmap.icon_hgwfc;
                case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
                    return R.mipmap.icon_germany_pk10;
                case LotteryConfig.LOTTERY_ID.QQ_REAMTIME:
                    return R.mipmap.icon_qq_realtime;
                case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                    return R.mipmap.icon_italy_pk10;
                case LotteryConfig.LOTTERY_ID.ITALY_REAMTIME:
                    return R.mipmap.icon_italy_mmc;
                case LotteryConfig.LOTTERY_ID.REAL_MAN:
                    return R.mipmap.agag;
                case LotteryConfig.LOTTERY_ID.SLOT:
                    return R.mipmap.slot_machine;
                case LotteryConfig.LOTTERY_ID.BAIJIALE:
                    return R.mipmap.icon_jxbaccarat;
                case LotteryConfig.LOTTERY_ID.CHATROOM_BJL:
                    return R.mipmap.chat_baccarat;
                case LotteryConfig.LOTTERY_ID.BAIJIALES:
                    return R.mipmap.slot_bjl;
                case LotteryConfig.LOTTERY_ID.FISHING:
                    return R.mipmap.icon_fishing;
            }
        }

        return R.mipmap.icon_chongqing_ssc;
    }

    public static int getPK10NumberImage(int number) {
        switch (number) {
            case 1:
                return R.mipmap.pk10_1;
            case 2:
                return R.mipmap.pk10_2;
            case 3:
                return R.mipmap.pk10_3;
            case 4:
                return R.mipmap.pk10_4;
            case 5:
                return R.mipmap.pk10_5;
            case 6:
                return R.mipmap.pk10_6;
            case 7:
                return R.mipmap.pk10_7;
            case 8:
                return R.mipmap.pk10_8;
            case 9:
                return R.mipmap.pk10_9;
            case 10:
                return R.mipmap.pk10_10;
        }

        return 0;
    }

    public static int getLotteryMessage(int lotteryId) {
        switch (lotteryId) {
            case LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME:
                return R.string.message_default;
            case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
                return R.string.message_welfare_3d;
            case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                return R.string.message_sports;
            case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
                return R.string.message_guangdong_select_five;
            case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
                return R.string.message_shandong_select_five;
            case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
                return R.string.message_beijing_pk10;
            case LotteryConfig.LOTTERY_ID.HS_REALTIME:
                return R.string.message_hs_realtime;
            case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                return R.string.message_hs_select_five;
            case LotteryConfig.LOTTERY_ID.XINJIANG_REALTIME:
                return R.string.message_xinjiang_realtime;
            case LotteryConfig.LOTTERY_ID.HS_SF_REAMTIME:
                return R.string.message_hs_sf_reamtime;
            case LotteryConfig.LOTTERY_ID.HS_PK10:
                return R.string.message_hs_pk10;
            case LotteryConfig.LOTTERY_ID.HS_MMC:
                return R.string.message_default;
            case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
                return R.string.message_hs_mmc_pk10;
            case LotteryConfig.LOTTERY_ID.TIANJIN_REALTIME:
                return R.string.message_tianjin_realtime;
            case LotteryConfig.LOTTERY_ID.TAIWAN_WF_REALTIME:
                return R.string.message_taiwan_wf_realtime;
            case LotteryConfig.LOTTERY_ID.BEIJING_KENO:
                return R.string.message_beijing_keno;
            case LotteryConfig.LOTTERY_ID.KOREA_WF_REALTIME:
                return R.string.message_default;
            case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
                return R.string.message_germany_pk10;
            case LotteryConfig.LOTTERY_ID.QQ_REAMTIME:
                return R.string.message_qq_realtime;
            case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                return R.string.message_italy_pk10;
            case LotteryConfig.LOTTERY_ID.ITALY_REAMTIME:
                return R.string.message_italy_realtime;
            case LotteryConfig.LOTTERY_ID.REAL_MAN:
                return R.string.message_realman;
            case LotteryConfig.LOTTERY_ID.SLOT:
                return R.string.message_slot;
            case LotteryConfig.LOTTERY_ID.BAIJIALE:
                return R.string.message_bjl_entrance;
            case LotteryConfig.LOTTERY_ID.CHATROOM_BJL:
                return R.string.message_chatroom_bjl;
            case LotteryConfig.LOTTERY_ID.BAIJIALES:
                return R.string.message_bjl;
            case LotteryConfig.LOTTERY_ID.FISHING:
                return R.string.message_fishing;
            case LotteryConfig.LOTTERY_ID.JIANG_SU_KUAI_SAN:
                return R.string.message_jiang_su_kuai_san;
        }

        return R.string.message_default;
    }
}
