package com.hec.app.lottery.generator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class PK10Generator extends BaseGenerator {
    public PK10Generator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 1, 10);
    }

    public List<String> generate() {
        switch (LotteryConfig.PK_10.PLAY_TYPE_MAP.get(mPlayTypeName)) {
            case LotteryConfig.PK_10.FIRST_ONE:
                mDigitNumbers = 1;
                return generateCommon();
            case LotteryConfig.PK_10.FIRST_TWO:
                mDigitNumbers = 2;
                return generateNonRepeat();
            case LotteryConfig.PK_10.FIRST_THREE:
                mDigitNumbers = 3;
                return generateNonRepeat();
            case LotteryConfig.PK_10.FIRST_FOUR:
                mDigitNumbers = 4;
                return generateNonRepeat();
            case LotteryConfig.PK_10.FIRST_FIVE:
                mDigitNumbers = 5;
                return generateNonRepeat();
            case LotteryConfig.PK_10.FIRST_SIX:
                mDigitNumbers = 6;
                return generateNonRepeat();
            case LotteryConfig.PK_10.SPECIFIC_POSITIONING:
                return specificPositionCalculate();
            case LotteryConfig.PK_10.DRAGON_TIGER_FIGHT:
                mDigitNumbers = 1;
                return generateDragonTiger();
            case LotteryConfig.PK_10.BIG_SMALL_ODD_EVEN:
                mDigitNumbers = 1;
                return generateBigSmallOddEven();
            case LotteryConfig.PK_10.GUAN_YA_SUM:
                return generateGuanYaSum();
            case LotteryConfig.PK_10.SYNTHESIS:
                return generateSynthesis();
            default:
                return null;
        }
    }

    private List<String> specificPositionCalculate() {
        int index = (int) (Math.random() * 10);
        List<String> playNumsList = new ArrayList<>();
        playNumsList.add("冠军");
        playNumsList.add("亚军");
        playNumsList.add("季军");
        playNumsList.add("第四名");
        playNumsList.add("第五名");
        playNumsList.add("第六名");
        playNumsList.add("第七名");
        playNumsList.add("第八名");
        playNumsList.add("第九名");
        playNumsList.add("第十名");

        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            if (i == index) {
                result.add(playNumsList.get(i) + " " + String.valueOf(generateOneNumber()) + " 1");
            }
            else {
                result.add("");
            }
        }
        return result;
    }

    private List<String> generateDragonTiger() {
        List<String> list = new ArrayList<>();
        list.add("龙");
        list.add("虎");

        int index = (int) (Math.random() * 2);
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index));
        return result;
    }

    private List<String> generateGuanYaSum() {
        List<String> list = new ArrayList<>();
        for (int i = 3; i <= 19; ++i) {
            list.add(String.valueOf(i));
        }
        list.add("冠亚大");
        list.add("冠亚小");
        list.add("冠亚单");
        list.add("冠亚双");

        int index = (int) (Math.random() * 21);
        ArrayList<String> result = new ArrayList<>();
        result.add("冠亚和 " + list.get(index) + " 1");
        return result;
    }

    private List<String> generateSynthesis() {
        List<String> bigSmallOddEvenList = new ArrayList<>();
        bigSmallOddEvenList.add("大");
        bigSmallOddEvenList.add("小");
        bigSmallOddEvenList.add("单");
        bigSmallOddEvenList.add("双");

        List<String> dragonTigerList = new ArrayList<>();
        dragonTigerList.add("龙");
        dragonTigerList.add("虎");

        List<String> playNumsList = new ArrayList<>();
        playNumsList.add("冠军");
        playNumsList.add("亚军");
        playNumsList.add("季军");
        playNumsList.add("第四名");
        playNumsList.add("第五名");
        playNumsList.add("第六名");
        playNumsList.add("第七名");
        playNumsList.add("第八名");
        playNumsList.add("第九名");
        playNumsList.add("第十名");
        playNumsList.add("1vs10");
        playNumsList.add("2vs9");
        playNumsList.add("3vs8");
        playNumsList.add("4vs7");
        playNumsList.add("5vs6");

        int playNumsRandom = (int) (Math.random() * 15);
        int bigSmallOddEvenRandom = (int) (Math.random() * 4);
        int dragonTigerRandom = (int) (Math.random() * 2);
        ArrayList<String> result = new ArrayList<>();

        if (playNumsRandom > 9) {
            result.add(playNumsList.get(playNumsRandom) + " " + dragonTigerList.get(dragonTigerRandom) + " 1");
        }
        else {
            result.add(playNumsList.get(playNumsRandom) + " " + bigSmallOddEvenList.get(bigSmallOddEvenRandom) + " 1");
        }

        return result;
    }
}