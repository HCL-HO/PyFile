package com.hec.app.lottery.calculator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class PK10Calculator extends BaseCalculator {
    public PK10Calculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        if (mSelected.isEmpty()) {
            return 0;
        }

        if (LotteryConfig.PK_10.PLAY_TYPE_MAP.containsKey(mPlayTypeName)) {
            switch (LotteryConfig.PK_10.PLAY_TYPE_MAP.get(mPlayTypeName)) {
                case LotteryConfig.PK_10.FIRST_TWO:
                    mDigitNumbers = 2;
                    return firstTwoCalculate();
                case LotteryConfig.PK_10.FIRST_THREE:
                    mDigitNumbers = 3;
                    return firstThreeCalculate();
                case LotteryConfig.PK_10.FIRST_FOUR:
                    mDigitNumbers = 4;
                    return firstFourCalculate();
                case LotteryConfig.PK_10.FIRST_FIVE:
                    mDigitNumbers = 5;
                    return firstFiveCalculate();
                case LotteryConfig.PK_10.FIRST_SIX:
                    mDigitNumbers = 6;
                    return firstSixCalculate();
                case LotteryConfig.PK_10.BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 1;
                    return commonBigSmallOddEven();
                case LotteryConfig.PK_10.FIRST_ONE:
                case LotteryConfig.PK_10.SPECIFIC_POSITIONING:
                case LotteryConfig.PK_10.DRAGON_TIGER_FIGHT:
                case LotteryConfig.PK_10.GUAN_YA_SUM:
                case LotteryConfig.PK_10.SYNTHESIS:
                    return commonSelectedSize();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int firstTwoCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }

        List<String> firstRow = Arrays.asList(mSelected.get(0).split(","));
        List<String> secondRow = Arrays.asList(mSelected.get(1).split(","));
        List<String> allRow = interSect(firstRow, secondRow);
        int repeatNums = allRow.size();
        int totalNums = firstRow.size() * secondRow.size() - repeatNums;

        return totalNums;
    }

    private int firstThreeCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }

        List<String> firstRow = Arrays.asList(mSelected.get(0).split(","));
        List<String> secondRow = Arrays.asList(mSelected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(mSelected.get(2).split(","));
        List<String> fEs = interSect(firstRow, secondRow);
        List<String> allE = interSect(fEs, thirdRow);
        List<String> fEt = interSect(firstRow, thirdRow);
        List<String> sEt = interSect(secondRow, thirdRow);

        int repeatNums = (fEs.size() * thirdRow.size() - allE.size());
        repeatNums += (fEt.size() * secondRow.size() - allE.size());
        repeatNums += (sEt.size() * firstRow.size() - allE.size());
        repeatNums += allE.size();

        int totalNums = firstRow.size() * secondRow.size() * thirdRow.size() - repeatNums;
        return totalNums;
    }

    private int firstFourCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }

        List<String> firstRow = Arrays.asList(mSelected.get(0).split(","));
        List<String> secondRow = Arrays.asList(mSelected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(mSelected.get(2).split(","));
        List<String> fourthRow = Arrays.asList(mSelected.get(3).split(","));
        List<List<String>> list = new ArrayList<>();
        list.add(firstRow);
        list.add(secondRow);
        list.add(thirdRow);
        list.add(fourthRow);

        List<String> all = allPossibleCases(list);
        List<String> tt = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String s : all) {
            tt.clear();
            String str = s.replace("10", "X");
            String temp;
            for (int i = 0; i < str.length(); i++) {
                temp = str.substring(i, i + 1);
                if (!tt.contains(temp)) {
                    tt.add(temp);
                }
            }
            if (tt.size() == str.length()) {
                result.add(str);
            }
        }

        return result.size();
    }

    private int firstFiveCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(mSelected.get(0).split(","));
        List<String> secondRow = Arrays.asList(mSelected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(mSelected.get(2).split(","));
        List<String> fourthRow = Arrays.asList(mSelected.get(3).split(","));
        List<String> fifthRow = Arrays.asList(mSelected.get(4).split(","));
        List<List<String>> list = new ArrayList<>();
        list.add(firstRow);
        list.add(secondRow);
        list.add(thirdRow);
        list.add(fourthRow);
        list.add(fifthRow);

        List<String> all = allPossibleCases(list);
        List<String> tt = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String s : all) {
            tt.clear();
            String str = s.replace("10", "X");
            String temp;
            char[] array = str.toCharArray();
            Arrays.sort(array);
            str = new String(array);
            for (int i = 0; i < str.length(); i++) {
                temp = str.substring(i, i + 1);
                if (!tt.contains(temp)) {
                    tt.add(temp);
                } else {
                    break;
                }
            }
            if (tt.size() == str.length()) {
                result.add(str);
            }
        }
        return result.size();
    }

    private int firstSixCalculate(){
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(mSelected.get(0).split(","));
        List<String> secondRow = Arrays.asList(mSelected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(mSelected.get(2).split(","));
        List<String> fourthRow = Arrays.asList(mSelected.get(3).split(","));
        List<String> fifthRow = Arrays.asList(mSelected.get(4).split(","));
        List<String> sixRow = Arrays.asList(mSelected.get(5).split(","));
        List<List<String>> list = new ArrayList<>();
        list.add(firstRow);
        list.add(secondRow);
        list.add(thirdRow);
        list.add(fourthRow);
        list.add(fifthRow);
        list.add(sixRow);

        List<String> all = allPossibleCases(list);
        List<String> tt = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String s : all) {
            tt.clear();
            String str = s.replace("10", "X");
            String temp;
            char[] array = str.toCharArray();
            Arrays.sort(array);
            str = new String(array);
            for (int i = 0; i < str.length(); i++) {
                temp = str.substring(i, i + 1);
                if (!tt.contains(temp)) {
                    tt.add(temp);
                } else {
                    break;
                }
            }
            if (tt.size() == str.length()) {
                result.add(str);
            }
        }
        return result.size();
    }
}
