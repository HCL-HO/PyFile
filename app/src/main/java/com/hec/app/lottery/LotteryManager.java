package com.hec.app.lottery;

import com.hec.app.lottery.base.BaseGenerator;
import com.hec.app.lottery.calculator.PK10Calculator;
import com.hec.app.lottery.calculator.RealtimeCalculator;
import com.hec.app.lottery.calculator.SelectFiveCalculator;
import com.hec.app.lottery.calculator.Welfare3DCalculator;
import com.hec.app.lottery.generator.RealtimeGenerator;
import com.hec.app.lottery.generator.Welfare3DGenerator;
import com.hec.app.lottery.generator.PK10Generator;
import com.hec.app.lottery.generator.SelectFiveGenerator;

import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class LotteryManager {
    public static int calculate(int lotteryId, String playTypeName, String playTypeRadioName, List<String> selected, int units) {
        switch(lotteryId) {
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
                return new RealtimeCalculator(playTypeName, playTypeRadioName, selected, units).calculate();
            case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
            case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
            case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                return new SelectFiveCalculator(playTypeName, playTypeRadioName, selected).calculate();
            case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
            case LotteryConfig.LOTTERY_ID.HS_PK10:
            case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
            case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
            case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                return new PK10Calculator(playTypeName, playTypeRadioName, selected).calculate();
            case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
            case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                return new Welfare3DCalculator(playTypeName, playTypeRadioName, selected).calculate();
            default:
                return  0;
        }
    }

    public static List<String> generateSingleBet(int lotteryId, String playTypeName, String playTypeRadioName) {
        BaseGenerator generator = null;

        switch(lotteryId) {
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
                generator = new RealtimeGenerator(playTypeName, playTypeRadioName);
                break;
            case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
            case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
            case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                generator = new SelectFiveGenerator(playTypeName, playTypeRadioName);
                break;
            case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
            case LotteryConfig.LOTTERY_ID.HS_PK10:
            case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
            case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
            case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                generator = new PK10Generator(playTypeName, playTypeRadioName);
                break;
            case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
            case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                generator = new Welfare3DGenerator(playTypeName, playTypeRadioName);
                break;
        }

        return generator.generate();
    }
}
