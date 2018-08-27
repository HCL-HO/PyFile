package com.hec.app.lottery.generator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class Welfare3DGenerator extends BaseGenerator {
    public Welfare3DGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 0, 9);
    }

    public List<String> generate() {
        switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_MAP.get(mPlayTypeName)) {
            case LotteryConfig.LOW_FREQUECE.THREE_STAR:
                return threeStar();
            case LotteryConfig.LOW_FREQUECE.TWO_STAR:
                return twoStar();
            case LotteryConfig.LOW_FREQUECE.SPECIFIC_POSITIONING:
                return specificPositionCalculate();
            case LotteryConfig.LOW_FREQUECE.UNSPECIFIC_POSITIONING:
                return unSpecificPosition();
            case LotteryConfig.LOW_FREQUECE.BIG_SMALL_ODD_EVEN:
                mDigitNumbers = 2;
                return generateBigSmallOddEven();
            default:
                return null;
        }
    }

    private List<String> threeStar() {
        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.LOW_FREQUECE.DIRECTION_DUPLEX:
                    mDigitNumbers = 3;
                    return generateCommon();
                case LotteryConfig.LOW_FREQUECE.DIRECTION_SUM:
                    mDigitNumbers = 3;
                    return generateDirectionSum();
                case LotteryConfig.LOW_FREQUECE.GROUP_THREE_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCombination();
                case LotteryConfig.LOW_FREQUECE.GROUP_SIX_DUPLEX:
                    mDigitNumbers = 3;
                    return generateCombination();
                case LotteryConfig.LOW_FREQUECE.GROUP_SELECTION_SUM:
                    mDigitNumbers = 3;
                    return generateCombinationSum();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> twoStar() {
        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.LOW_FREQUECE.FRONT_TWO_DIRECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.BACK_TWO_DIRECTION_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCommon();
                case LotteryConfig.LOW_FREQUECE.FRONT_TWO_GROUP_SELECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.BACK_TWO_GROUP_SELECTION_DUPLEX:
                    mDigitNumbers = 2;
                    return generateCombination();
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> specificPositionCalculate() {
        mDigitNumbers = 1;
        List<String> list = generateCommon();
        fillingThreePosition(list);
        return list;
    }

    private List<String> unSpecificPosition() {
        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.LOW_FREQUECE.ONE_NUMBER_UNSPECIFIC_POSITIONING:
                    mDigitNumbers = 1;
                    return generateCommon();
                case LotteryConfig.LOW_FREQUECE.TWO_NUMBER_UNSPECIFIC_POSITIONING:
                    mDigitNumbers = 2;
                    String value = generateNonRepeatNumbers(-1);
                    List<String> result = new ArrayList<>();
                    result.add(value);

                    return result;
                default:
                    return null;
            }
        }

        return null;
    }
}
