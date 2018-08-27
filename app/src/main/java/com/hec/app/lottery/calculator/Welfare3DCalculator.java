package com.hec.app.lottery.calculator;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseCalculator;

import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class Welfare3DCalculator extends BaseCalculator {
    public Welfare3DCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        if (mSelected.isEmpty()) {
            return 0;
        }

        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_MAP.containsKey(mPlayTypeName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_MAP.get(mPlayTypeName)) {
                case LotteryConfig.LOW_FREQUECE.THREE_STAR:
                    return threeStar();
                case LotteryConfig.LOW_FREQUECE.TWO_STAR:
                    return twoStar();
                case LotteryConfig.LOW_FREQUECE.SPECIFIC_POSITIONING:
                    return commonSelectedSize();
                case LotteryConfig.LOW_FREQUECE.UNSPECIFIC_POSITIONING:
                    return unSpecificPosition();
                case LotteryConfig.LOW_FREQUECE.BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 2;
                    return commonBigSmallOddEven();
                case LotteryConfig.LOW_FREQUECE.GROUP_SELECTION_MIXED_SINGLE:
                    return commonMixedGroupSelection() * permutation(3, 3);
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int threeStar() {
        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.LOW_FREQUECE.DIRECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.DIRECTION_SINGLE:
                    mDigitNumbers = 3;
                    return commonCalculate();
                case LotteryConfig.LOW_FREQUECE.DIRECTION_SUM:
                    mDigitNumbers = 3;
                    return commonDirectionSum();
                case LotteryConfig.LOW_FREQUECE.GROUP_THREE_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.GROUP_THREE_SINGLE:
                    mDigitNumbers = 2;
                    return commonThreeStarCombination();
                case LotteryConfig.LOW_FREQUECE.GROUP_SIX_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.GROUP_SIX_SINGLE:
                    mDigitNumbers = 3;
                    return commonThreeStarCombination();
                case LotteryConfig.LOW_FREQUECE.MIXED_GROUP_SELECTION:
                    return commonMixedGroupSelection();
                case LotteryConfig.LOW_FREQUECE.GROUP_SELECTION_SUM:
                    mDigitNumbers = 3;
                    return commonCombinationSum();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int twoStar() {
        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.LOW_FREQUECE.FRONT_TWO_DIRECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.FRONT_TWO_DIRECTION_SINGLE:
                case LotteryConfig.LOW_FREQUECE.BACK_TWO_DIRECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.BACK_TWO_DIRECTION_SINGLE:
                    mDigitNumbers = 2;
                    return commonCalculate();
                case LotteryConfig.LOW_FREQUECE.FRONT_TWO_GROUP_SELECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.FRONT_TWO_GROUP_SELECTION_SINGLE:
                case LotteryConfig.LOW_FREQUECE.BACK_TWO_GROUP_SELECTION_DUPLEX:
                case LotteryConfig.LOW_FREQUECE.BACK_TWO_GROUP_SELECTION_SINGLE:
                    return commonTwoStarCombination();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int unSpecificPosition() {
        if (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.LOW_FREQUECE.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.LOW_FREQUECE.ONE_NUMBER_UNSPECIFIC_POSITIONING:
                    mDigitNumbers = 1;
                    return commonUnSpecificPosition();
                case LotteryConfig.LOW_FREQUECE.TWO_NUMBER_UNSPECIFIC_POSITIONING:
                    mDigitNumbers = 2;
                    return commonUnSpecificPosition();
                default:
                    return 0;
            }
        }

        return 0;
    }
}
