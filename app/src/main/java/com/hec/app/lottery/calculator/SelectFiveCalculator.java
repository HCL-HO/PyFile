package com.hec.app.lottery.calculator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseCalculator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class SelectFiveCalculator extends BaseCalculator {
    public SelectFiveCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        if (mSelected.isEmpty()) {
            return 0;
        }

        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_MAP.containsKey(mPlayTypeName)) {
            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_MAP.get(mPlayTypeName)) {
                case LotteryConfig.SELECT_FILE.THREE_NUMBER:
                    return threeStar();
                case LotteryConfig.SELECT_FILE.TWO_NUMBER:
                    return twoStar();
                case LotteryConfig.SELECT_FILE.UNSPECIFIC_POSITIONING:
                case LotteryConfig.SELECT_FILE.SPECIFIC_POSITIONING:
                case LotteryConfig.SELECT_FILE.CARNIVAL:
                    return commonSelectedSize();
                case LotteryConfig.SELECT_FILE.AT_WILL_DOUBLE_DUPLEX:
                case LotteryConfig.SELECT_FILE.AT_WILL_DOUBLE_SINGLE:
                    return atWillDoubleCalculate();
                case LotteryConfig.SELECT_FILE.AT_WILL_TOWED:
                    return atWillTowedCalculate();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int threeStar() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.FRONT_THREE_DIRECTION_DUPLEX:
                case LotteryConfig.SELECT_FILE.FRONT_THREE_DIRECTION_SINGLE:
                    mDigitNumbers = 3;
                    return threeStarDirectionCalculate();
                case LotteryConfig.SELECT_FILE.FRONT_THREE_GROUP_SELECTION_DUPLEX:
                case LotteryConfig.SELECT_FILE.FRONT_THREE_GROUP_SELECTION_SINGLE:
                    mDigitNumbers = 3;
                    return threeStarCombinationCalculate();
                case LotteryConfig.SELECT_FILE.FRONT_THREE_GROUP_SELECTION_TOWED:
                    return threeStarAtWillTowedCalculate();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int threeStarDirectionCalculate() {
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

    private int threeStarAtWillTowedCalculate(){
        if (isEmpty(mSelected.get(0)) || isEmpty(mSelected.get(1))) {
            return 0;
        }
        int boldNums = mSelected.get(0).split(",").length;
        int towedNums = mSelected.get(1).split(",").length;
        int m = 3 - boldNums;

        return combination(towedNums, m);
    }

    private int threeStarCombinationCalculate() {
        int totalNums = 0;
        if (mPlayTypeRadioName.contains("单式")) {
            // 3個不相同的數字
            if (mSelected.size() == 3) {
                if (!mSelected.get(0).equals(mSelected.get(1)) && !mSelected.get(1).equals(mSelected.get(2)) && !mSelected.get(2).equals(mSelected.get(0))) {
                    totalNums = 1;
                }
            }
        }
        else {
            int pickNum = commonSelectedSize();
            totalNums = (pickNum * (pickNum - 1) * (pickNum - 2)) / (3 * 2 * 1);
        }

        return totalNums;
    }

    private int twoStar() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.FRONT_TWO_DIRECTION_DUPLEX:
                case LotteryConfig.SELECT_FILE.FRONT_TWO_DIRECTION_SINGLE:
                    mDigitNumbers = 2;
                    return twoStarDirectionCalculate();
                case LotteryConfig.SELECT_FILE.FRONT_TWO_GROUP_SELECTION_DUPLEX:
                case LotteryConfig.SELECT_FILE.FRONT_TWO_GROUP_SELECTION_SINGLE:
                    return commonTwoStarCombination();
                case LotteryConfig.SELECT_FILE.FRONT_TWO_GROUP_SELECTION_TOWED:
                    return twoStarAtWillTowedCalculate();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int twoStarDirectionCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(mSelected.get(0).split(","));
        List<String> secondRow = Arrays.asList(mSelected.get(1).split(","));

        List<String> fEs = interSect(firstRow, secondRow);

        int repeatNums = fEs.size();

        int totalNums = firstRow.size() * secondRow.size() - repeatNums;
        return totalNums;
    }

    private int twoStarAtWillTowedCalculate(){
        if (isEmpty(mSelected.get(0)) || isEmpty(mSelected.get(1))) {
            return 0;
        }

        return mSelected.get(1).split(",").length;
    }

    private int atWillDoubleCalculate() {
        int result = 0;
        int bet = getMaxNumsInBet();

        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_MAP.get(mPlayTypeName) == LotteryConfig.SELECT_FILE.AT_WILL_DOUBLE_SINGLE) {
            if (mSelected.size() == bet) {
                if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName) == LotteryConfig.SELECT_FILE.OPTIONAL_ONE_OF_ONE) {
                    result = 1;
                }
                else {
                    // 數字皆不相同
                    boolean isError = false;
                    for(int i = 0; i < bet-1; ++i) {
                        for (int j = i+1; j < bet; ++j) {
                            if (mSelected.get(i).equals(mSelected.get(j))) {
                                isError = true;
                                break;
                            }
                        }

                        if (isError) {
                            break;
                        }
                    }

                    if (!isError) {
                        result = 1;
                    }
                }
            }
        }
        else {
            int pickNum = commonSelectedSize();
            result = combination(pickNum, bet);
        }

        return result;
    }

    private int atWillTowedCalculate() {
        if (isEmpty(mSelected.get(0)) || isEmpty(mSelected.get(1)) || !LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            return 0;
        }

        int boldNums = mSelected.get(0).split(",").length;
        int towedNums = mSelected.get(1).split(",").length;
        int bet = getMaxNumsInBet() - boldNums;

        switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
            case LotteryConfig.SELECT_FILE.OPTIONAL_TWO_OF_TWO:
                return towedNums;
            case LotteryConfig.SELECT_FILE.OPTIONAL_THREE_OF_THREE:
            case LotteryConfig.SELECT_FILE.OPTIONAL_FOUR_OF_FOUR:
            case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_FIVE:
            case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SIX:
            case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SEVEN:
            case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_EIGHT:
                return combination(towedNums, bet);
            default:
                return 0;
        }
    }

    private int getMaxNumsInBet() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.OPTIONAL_ONE_OF_ONE:
                case LotteryConfig.SELECT_FILE.ONE_OF_ONE:
                    return 1;
                case LotteryConfig.SELECT_FILE.OPTIONAL_TWO_OF_TWO:
                case LotteryConfig.SELECT_FILE.TWO_OF_TWO:
                    return 2;
                case LotteryConfig.SELECT_FILE.OPTIONAL_THREE_OF_THREE:
                case LotteryConfig.SELECT_FILE.THREE_OF_THREE:
                    return 3;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FOUR_OF_FOUR:
                case LotteryConfig.SELECT_FILE.FOUR_OF_FOUR:
                    return 4;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_FIVE:
                case LotteryConfig.SELECT_FILE.FIVE_OF_FIVE:
                    return 5;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SIX:
                case LotteryConfig.SELECT_FILE.FIVE_OF_SIX:
                    return 6;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SEVEN:
                case LotteryConfig.SELECT_FILE.FIVE_OF_SEVEN:
                    return 7;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_EIGHT:
                case LotteryConfig.SELECT_FILE.FIVE_OF_EIGHT:
                    return 8;
                default:
                    return 0;
            }
        }

        return 0;
    }
}

