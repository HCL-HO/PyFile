package com.hec.app.lottery.generator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseGenerator;
import com.hec.app.lottery.base.SortComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class SelectFiveGenerator extends BaseGenerator {
    public SelectFiveGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 1, 11);
    }

    @Override
    public List<String> generate() {
        switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_MAP.get(mPlayTypeName)) {
            case LotteryConfig.SELECT_FILE.THREE_NUMBER:
                return threeStar();
            case LotteryConfig.SELECT_FILE.TWO_NUMBER:
                return twoStar();
            case LotteryConfig.SELECT_FILE.UNSPECIFIC_POSITIONING:
                mDigitNumbers = 1;
                return generateCommon();
            case LotteryConfig.SELECT_FILE.SPECIFIC_POSITIONING:
                return specificPositionCalculate();
            case LotteryConfig.SELECT_FILE.CARNIVAL:
                return Carnival();
            case LotteryConfig.SELECT_FILE.AT_WILL_DOUBLE_DUPLEX:
                return atWillDouble();
            case LotteryConfig.SELECT_FILE.AT_WILL_TOWED:
                return generateAtWillTowed();
            default:
                return null;
        }
    }

    private List<String> threeStar() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.FRONT_THREE_DIRECTION_DUPLEX:
                    mDigitNumbers = 3;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.FRONT_THREE_GROUP_SELECTION_DUPLEX:
                    mDigitNumbers = 3;
                    return generateCombination();
                case LotteryConfig.SELECT_FILE.FRONT_THREE_GROUP_SELECTION_TOWED:
                    mDigitNumbers = 2;
                    int danMa = generateOneNumber();
                    String tuoMa = generateNonRepeatNumbers(danMa);

                    List<String> list = new ArrayList<>();
                    list.add(0, String.valueOf(danMa));
                    list.add(1, tuoMa);
                    return list;
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> twoStar() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            mDigitNumbers = 2;

            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.FRONT_TWO_DIRECTION_DUPLEX:
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.FRONT_TWO_GROUP_SELECTION_DUPLEX:
                    return generateCombination();
                case LotteryConfig.SELECT_FILE.FRONT_TWO_GROUP_SELECTION_TOWED:
                    return generateNonRepeat();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> specificPositionCalculate() {
        mDigitNumbers = 1;
        List<String> result = generateCommon();
        fillingThreePosition(result);
        return result;
    }

    private List<String> Carnival() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            List<String> list;
            ArrayList<String> result;
            int index;

            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.ODD_EVEN:
                    list = new ArrayList<>();
                    list.add("5单0双");
                    list.add("4单1双");
                    list.add("3单2双");
                    list.add("2单3双");
                    list.add("1单4双");
                    list.add("0单5双");

                    index = (int) (Math.random() * 6);
                    result = new ArrayList<>();
                    result.add(list.get(index));

                    return result;
                case LotteryConfig.SELECT_FILE.GUESS_MEDIAN:
                    list = new ArrayList<>();
                    for (int i = 3; i <= 9; i++) {
                        list.add(String.valueOf(i));
                    }

                    index = (int) (Math.random() * 7);
                    result = new ArrayList<>();
                    result.add(list.get(index));

                    return result;
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> atWillDouble() {
        List<String> list = generateAtWillDouble();
        Comparator comp = new SortComparator();
        Collections.sort(list, comp);

        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s);
            builder.append(",");
        }

        List<String> result = new ArrayList<>();
        result.add(builder.substring(0, builder.length()-1));

        return result;
    }

    private List<String> generateAtWillDouble() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.OPTIONAL_ONE_OF_ONE:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.SELECT_FILE.OPTIONAL_TWO_OF_TWO:
                    mDigitNumbers = 2;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_THREE_OF_THREE:
                    mDigitNumbers = 3;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_FOUR_OF_FOUR:
                    mDigitNumbers = 4;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_FIVE:
                    mDigitNumbers = 5;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SIX:
                    mDigitNumbers = 6;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SEVEN:
                    mDigitNumbers = 7;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_EIGHT:
                    mDigitNumbers = 8;
                    return generateNonRepeat();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> generateAtWillTowed() {
        if (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            List<String> list = new ArrayList<>();
            int danMa;
            String tuoMa;

            switch (LotteryConfig.SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.SELECT_FILE.OPTIONAL_TWO_OF_TWO:
                    mDigitNumbers = 2;
                    return generateNonRepeat();
                case LotteryConfig.SELECT_FILE.OPTIONAL_THREE_OF_THREE:
                    mDigitNumbers = 2;
                    break;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FOUR_OF_FOUR:
                    mDigitNumbers = 3;
                    break;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_FIVE:
                    mDigitNumbers = 4;
                    break;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SIX:
                    mDigitNumbers = 5;
                    break;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_SEVEN:
                    mDigitNumbers = 6;
                    break;
                case LotteryConfig.SELECT_FILE.OPTIONAL_FIVE_OF_EIGHT:
                    mDigitNumbers = 7;
                    break;
                default:
                    return null;
            }

            danMa = generateOneNumber();
            tuoMa = generateNonRepeatNumbers(danMa);
            list.add(0, String.valueOf(danMa));
            list.add(1, tuoMa);
            return list;
        }

        return null;
    }
}