package com.hec.app.lottery.generator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseGenerator;
import com.hec.app.lottery.base.SortComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class RealtimeGenerator extends BaseGenerator {
    public RealtimeGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 0, 9);
    }

    @Override
    public List<String> generate() {
        switch (LotteryConfig.REAL_TIME.PLAY_TYPE_MAP.get(mPlayTypeName)) {
            case LotteryConfig.REAL_TIME.FIVE_STAR:
                return fiveStar();
            case LotteryConfig.REAL_TIME.FOUR_STAR:
                return fourStar();
            case LotteryConfig.REAL_TIME.BACK_THREE:
            case LotteryConfig.REAL_TIME.CENTER_THREE:
            case LotteryConfig.REAL_TIME.FRONT_THREE:
                return threeStar();
            case LotteryConfig.REAL_TIME.BACK_TWO:
            case LotteryConfig.REAL_TIME.FRONT_TWO:
                return twoStar();
            case LotteryConfig.REAL_TIME.SPECIFIC_POSITIONING:
                return specificPositionCalculate();
            case LotteryConfig.REAL_TIME.UNSPECIFIC_POSITIONING:
                return unSpecificPosition();
            case LotteryConfig.REAL_TIME.BIG_SMALL_ODD_EVEN:
                return bigSmallOddEven();
            case LotteryConfig.REAL_TIME.OPTIONAL_TWO:
                return optionalTwo();
            case LotteryConfig.REAL_TIME.OPTIONAL_THREE:
                return optionalThree();
            case LotteryConfig.REAL_TIME.OPTIONAL_FOUR:
                return optionalFour();
            case LotteryConfig.REAL_TIME.DRAGON_TIGER_TIE:
                return generateDragonTiger();
            case LotteryConfig.REAL_TIME.OPTIONAL:
                return optional();
            default:
                return null;
        }
    }

    private List<String> fiveStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 5;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.FIVE_COMBINATION:
                    mDigitNumbers = 5;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.SMOOTH_SAILING:
                case LotteryConfig.REAL_TIME.GOOD_THINGS_IN_PAIRS:
                case LotteryConfig.REAL_TIME.SAN_XING_BAO_XI:
                case LotteryConfig.REAL_TIME.SI_JI_FA_CAI:
                    mDigitNumbers = 1;
                    return generateCommon();
                default:
                    return generateGroupSelection();
            }
        }

        return null;
    }

    private List<String> fourStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 4;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.FOUR_COMBINATION:
                    mDigitNumbers = 4;
                    return generateCommon();
                default:
                    return generateGroupSelection();
            }
        }

        return null;
    }

    private List<String> threeStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 3;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.BACK_THREE_COMBINATION:
                case LotteryConfig.REAL_TIME.CENTER_THREE_COMBINATION:
                case LotteryConfig.REAL_TIME.FRONT_THREE_COMBINATION:
                    mDigitNumbers = 3;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 3;
                    return generateDirectionSum();
                case LotteryConfig.REAL_TIME.DIRECTION_SPAN:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.GROUP_THREE_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCombination();
                case LotteryConfig.REAL_TIME.GROUP_SIX_DUPLEX:
                    mDigitNumbers = 3;
                    return generateCombination();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 3;
                    return generateCombinationSum();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_TOWED:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.SUM_MANTISSA:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.SPECIAL_NUMBER:
                    List<String> list = new ArrayList<>();
                    list.add("豹子");
                    list.add("顺子");
                    list.add("对子");
                    list.add("半顺");
                    list.add("杂六");

                    int index = (int) (Math.random() * 5);
                    ArrayList<String> result = new ArrayList<>();
                    result.add(list.get(index));
                    return result;
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> twoStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 2;
                    return generateDirectionSum();
                case LotteryConfig.REAL_TIME.DIRECTION_SPAN:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCombination();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 2;
                    return generateCombinationSum();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_TOWED:
                    mDigitNumbers = 1;
                    return generateCommon();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> specificPositionCalculate() {
        mDigitNumbers = 1;
        List<String> list = generateCommon();
        fillingFivePosition(list);
        return list;
    }

    private List<String> unSpecificPosition() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            String value = "";
            List<String> result = new ArrayList<>();

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.BACK_THREE_ONE_NUMBER:
                case LotteryConfig.REAL_TIME.FRONT_THREE_ONE_NUMBER:
                case LotteryConfig.REAL_TIME.FOUR_STAR_ONE_NUMBER:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.REAL_TIME.BACK_THREE_TWO_NUMBER:
                case LotteryConfig.REAL_TIME.FRONT_THREE_TWO_NUMBER:
                case LotteryConfig.REAL_TIME.FOUR_STAR_TWO_NUMBER:
                case LotteryConfig.REAL_TIME.FIVE_STAR_TWO_NUMBER:
                    mDigitNumbers = 2;
                    value = generateNonRepeatNumbers(-1);
                    result.add(value);

                    return result;
                case LotteryConfig.REAL_TIME.FIVE_STAR_THREE_NUMBER:
                    mDigitNumbers = 3;
                    value = generateNonRepeatNumbers(-1);
                    result.add(value);

                    return result;
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> bigSmallOddEven() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.FRONT_TWO_BIG_SMALL_ODD_EVEN:
                case LotteryConfig.REAL_TIME.BACK_TWO_BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 2;
                    return generateBigSmallOddEven();
                case LotteryConfig.REAL_TIME.FRONT_THREE_BIG_SMALL_ODD_EVEN:
                case LotteryConfig.REAL_TIME.BACK_THREE_BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 3;
                    return generateBigSmallOddEven();
                case LotteryConfig.REAL_TIME.SUM_BIG_SMALL_ODD_EVEN:
                    List<String> list = new ArrayList<>();
                    list.add("总和大");
                    list.add("总和小");
                    list.add("总和单");
                    list.add("总和双");

                    int index = (int) (Math.random() * 4);
                    ArrayList<String> result = new ArrayList<>();
                    result.add(list.get(index));
                    return result;
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> optionalTwo() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            mDigitNumbers = 2;

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    List<String> result = generateCommon();
                    fillingFivePosition(result);
                    return result;
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    return generateDirectionSum();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_DUPLEX:
                    return generateCombination();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    return generateCombinationSum();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> optionalThree() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 3;
                    List<String> result = generateCommon();
                    fillingFivePosition(result);
                    return result;
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 3;
                    return generateDirectionSum();
                case LotteryConfig.REAL_TIME.GROUP_THREE_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCombination();
                case LotteryConfig.REAL_TIME.GROUP_SIX_DUPLEX:
                    mDigitNumbers = 3;
                    return generateCombination();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 3;
                    return generateCombinationSum();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> optionalFour() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 4;
                    List<String> result = generateCommon();
                    fillingFivePosition(result);
                    return result;
                default:
                    return generateGroupSelection();
            }
        }

        return null;
    }

    private List<String> optional() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            List<String> result = new ArrayList<>();

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.OPTIONAL_FOUR_DUPLEX:
                    mDigitNumbers = 4;
                    result = generateCommon();
                    fillingFivePosition(result);
                    return result;
                case LotteryConfig.REAL_TIME.OPTIONAL_THREE_DUPLEX:
                    mDigitNumbers = 3;
                    result = generateCommon();
                    fillingFivePosition(result);
                    return result;
                case LotteryConfig.REAL_TIME.OPTIONAL_TWO_DUPLEX:
                    mDigitNumbers = 2;
                    result = generateCommon();
                    fillingFivePosition(result);
                    return result;
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> generateDragonTiger() {
        List<String> list = new ArrayList<>();
        list.add("龙");
        list.add("虎");
        list.add("和");

        int index = (int) (Math.random() * 3);
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index));
        return result;
    }

    private List<String> generateGroupSelection() {
        switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_120:
                mDigitNumbers = 5;
                return getGroupSelectionList(1);
            case LotteryConfig.REAL_TIME.FOUR_STAR_GROUP_SELECTION:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_24:
                mDigitNumbers = 4;
                return getGroupSelectionList(1);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_6:
                mDigitNumbers = 2;
                return getGroupSelectionList(1);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_60:
                mDigitNumbers = 3;
                return getGroupSelectionList(2);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_30:
                mDigitNumbers = 2;
                return getGroupSelectionList(3);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_20:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_12:
                mDigitNumbers = 2;
                return getGroupSelectionList(2);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_10:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_5:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_4:
                mDigitNumbers = 1;
                return getGroupSelectionList(2);
            default:
                return null;
        }
    }

    private List<String> getGroupSelectionList(int type) {
        int intRow = 0;
        String stringRow = "";
        List<String> result = new ArrayList<>();

        switch (type) {
            case 1:
                stringRow = generateNonRepeatNumbers(-1);
                result.add(stringRow);
                
                return result;
            case 2:
                intRow = generateOneNumber();
                stringRow = generateNonRepeatNumbers(intRow);

                result = new ArrayList<>();
                result.add(0, String.valueOf(intRow));
                result.add(1, stringRow);
                return result;
            case 3:
                intRow = generateOneNumber();
                stringRow = generateNonRepeatNumbers(intRow);

                result = new ArrayList<>();
                result.add(0, stringRow);
                result.add(1, String.valueOf(intRow));
                return result;

            default:
                return null;
        }
    }
}

