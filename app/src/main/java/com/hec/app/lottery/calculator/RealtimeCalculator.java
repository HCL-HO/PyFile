package com.hec.app.lottery.calculator;

import android.util.Log;

import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.base.BaseCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class RealtimeCalculator extends BaseCalculator {
    private int mSumCount = 0;
    private int mUnits;

    public RealtimeCalculator(String playTypeName, String playTypeRadioName, List<String> selected, int units) {
        super(playTypeName, playTypeRadioName, selected);
        mUnits = units;
    }

    @Override
    public int calculate() {
        if (mSelected.isEmpty()) {
            return 0;
        }

        if(LotteryConfig.REAL_TIME.PLAY_TYPE_MAP.containsKey(mPlayTypeName)) {
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
                    return commonSelectedSize();
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
                    return commonSelectedSize();
                case LotteryConfig.REAL_TIME.OPTIONAL:
                    return optional();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_MIXED_SINGLE:
                    return commonMixedGroupSelection() * permutation(3, 3);
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int fiveStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 5;
                    return commonCalculate();
                case LotteryConfig.REAL_TIME.FIVE_COMBINATION:
                    mDigitNumbers = 5;
                    return commonCombination();
                case LotteryConfig.REAL_TIME.SMOOTH_SAILING:
                case LotteryConfig.REAL_TIME.GOOD_THINGS_IN_PAIRS:
                case LotteryConfig.REAL_TIME.SAN_XING_BAO_XI:
                case LotteryConfig.REAL_TIME.SI_JI_FA_CAI:
                    return commonSelectedSize();
                default:
                    return commonGroupSelection();
            }
        }

        return 0;
    }

    private int fourStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 4;
                    return commonCalculate();
                case LotteryConfig.REAL_TIME.FOUR_COMBINATION:
                    mDigitNumbers = 4;
                    return commonCombination();
                default:
                    return commonGroupSelection();
            }
        }

        return 0;
    }

    private int threeStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 3;
                    return commonCalculate();
                case LotteryConfig.REAL_TIME.BACK_THREE_COMBINATION:
                case LotteryConfig.REAL_TIME.CENTER_THREE_COMBINATION:
                case LotteryConfig.REAL_TIME.FRONT_THREE_COMBINATION:
                    mDigitNumbers = 3;
                    return commonCombination();
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 3;
                    return commonDirectionSum();
                case LotteryConfig.REAL_TIME.DIRECTION_SPAN:
                    mDigitNumbers = 3;
                    return commonDirectionSpan();
                case LotteryConfig.REAL_TIME.GROUP_THREE_DUPLEX:
                case LotteryConfig.REAL_TIME.GROUP_THREE_SINGLE:
                    mDigitNumbers = 2;
                    return commonThreeStarCombination();
                case LotteryConfig.REAL_TIME.GROUP_SIX_DUPLEX:
                case LotteryConfig.REAL_TIME.GROUP_SIX_SINGLE:
                    mDigitNumbers = 3;
                    return commonThreeStarCombination();
                case LotteryConfig.REAL_TIME.MIXED_GROUP_SELECTION:
                    return commonMixedGroupSelection();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 3;
                    return commonCombinationSum();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_TOWED:
                    return 54;
                case LotteryConfig.REAL_TIME.SUM_MANTISSA:
                case LotteryConfig.REAL_TIME.SPECIAL_NUMBER:
                    return commonSelectedSize();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int twoStar() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 2;
                    return commonCalculate();
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 2;
                    return commonDirectionSum();
                case LotteryConfig.REAL_TIME.DIRECTION_SPAN:
                    mDigitNumbers = 2;
                    return commonDirectionSpan();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_DUPLEX:
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SINGLE:
                    return commonTwoStarCombination();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 2;
                    return commonCombinationSum();
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_TOWED:
                    return 9;
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int unSpecificPosition() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.BACK_THREE_ONE_NUMBER:
                case LotteryConfig.REAL_TIME.FRONT_THREE_ONE_NUMBER:
                case LotteryConfig.REAL_TIME.FOUR_STAR_ONE_NUMBER:
                    mDigitNumbers = 1;
                    return commonUnSpecificPosition();
                case LotteryConfig.REAL_TIME.BACK_THREE_TWO_NUMBER:
                case LotteryConfig.REAL_TIME.FRONT_THREE_TWO_NUMBER:
                case LotteryConfig.REAL_TIME.FOUR_STAR_TWO_NUMBER:
                case LotteryConfig.REAL_TIME.FIVE_STAR_TWO_NUMBER:
                    mDigitNumbers = 2;
                    return commonUnSpecificPosition();
                case LotteryConfig.REAL_TIME.FIVE_STAR_THREE_NUMBER:
                    mDigitNumbers = 3;
                    return commonUnSpecificPosition();
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int bigSmallOddEven() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.FRONT_TWO_BIG_SMALL_ODD_EVEN:
                case LotteryConfig.REAL_TIME.BACK_TWO_BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 2;
                    break;
                case LotteryConfig.REAL_TIME.FRONT_THREE_BIG_SMALL_ODD_EVEN:
                case LotteryConfig.REAL_TIME.BACK_THREE_BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 3;
                    break;
                case LotteryConfig.REAL_TIME.SUM_BIG_SMALL_ODD_EVEN:
                    mDigitNumbers = 1;
                    break;
                default:
                    return 0;
            }

            return commonBigSmallOddEven();
        }

        return 0;
    }

    private int optionalTwo() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            int result;
            int combinationValue = 0;
            if ( LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName) != LotteryConfig.REAL_TIME.DIRECTION_DUPLEX ) {
                // 至少勾選2個
                if ( mUnits < 2 || mUnits > 5 ) {
                    return 0;
                }

                combinationValue = combination(mUnits, 2);
            }

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 2;
                    return atWillPlayCalculate();
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 2;
                    result = atWillPlayCalculate() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 2;
                    result = commonDirectionSum() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_DUPLEX:
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SINGLE:
                    result = commonTwoStarCombination() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 2;
                    result = commonCombinationSum() * combinationValue;
                    return result;
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int optionalThree() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            int result;
            int combinationValue = 0;
            if ( LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName) != LotteryConfig.REAL_TIME.DIRECTION_DUPLEX ) {
                // 至少勾選3個
                if ( mUnits < 3 || mUnits > 5 ) {
                    return 0;
                }

                combinationValue = combination(mUnits, 3);
            }

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 3;
                    return atWillPlayCalculate();
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 3;
                    result = atWillPlayCalculate() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.DIRECTION_SUM:
                    mDigitNumbers = 3;
                    result = commonDirectionSum() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.GROUP_THREE_DUPLEX:
                case LotteryConfig.REAL_TIME.GROUP_THREE_SINGLE:
                    mDigitNumbers = 2;
                    result = commonThreeStarCombination() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.GROUP_SIX_DUPLEX:
                case LotteryConfig.REAL_TIME.GROUP_SIX_SINGLE:
                    mDigitNumbers = 3;
                    result = commonThreeStarCombination() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.MIXED_GROUP_SELECTION:
                    mDigitNumbers = 3;
                    result = commonMixedGroupSelection() * combinationValue;
                    return result;
                case LotteryConfig.REAL_TIME.GROUP_SELECTION_SUM:
                    mDigitNumbers = 3;
                    result = commonCombinationSum() * combinationValue;
                    return result;
                default:
                    return 0;
            }
        }

        return 0;
    }

    private int optionalFour() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            int result;
            int combinationValue = 0;
            if ( LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName) != LotteryConfig.REAL_TIME.DIRECTION_DUPLEX ) {
                // 至少勾選4個
                if ( mUnits < 4 || mUnits > 5 ) {
                    return 0;
                }

                combinationValue = combination(mUnits, 4);
            }

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.DIRECTION_DUPLEX:
                    mDigitNumbers = 4;
                    return atWillPlayCalculate();
                case LotteryConfig.REAL_TIME.DIRECTION_SINGLE:
                    mDigitNumbers = 4;
                    result = atWillPlayCalculate() * combinationValue;
                    return result;
                default:
                    result = commonGroupSelection() * combinationValue;
                    return result;
            }
        }

        return 0;
    }

    private int optional() {
        if (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.containsKey(mPlayTypeRadioName)) {
            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.OPTIONAL_FOUR_DUPLEX:
                case LotteryConfig.REAL_TIME.OPTIONAL_FOUR_SINGLE:
                    mDigitNumbers = 4;
                    break;
                case LotteryConfig.REAL_TIME.OPTIONAL_THREE_DUPLEX:
                case LotteryConfig.REAL_TIME.OPTIONAL_THREE_SINGLE:
                    mDigitNumbers = 3;
                    break;
                case LotteryConfig.REAL_TIME.OPTIONAL_TWO_DUPLEX:
                case LotteryConfig.REAL_TIME.OPTIONAL_TWO_SINGLE:
                    mDigitNumbers = 2;
                    break;
                default:
                    return 0;
            }

            int combinationValue = 0;
            if ( LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName) != LotteryConfig.REAL_TIME.DIRECTION_DUPLEX ) {
                if ( mUnits < mDigitNumbers || mUnits > 5 ) {
                    return 0;
                }

                combinationValue = combination(mUnits, mDigitNumbers);
            }

            switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
                case LotteryConfig.REAL_TIME.OPTIONAL_FOUR_DUPLEX:
                case LotteryConfig.REAL_TIME.OPTIONAL_THREE_DUPLEX:
                case LotteryConfig.REAL_TIME.OPTIONAL_TWO_DUPLEX:
                    return atWillPlayCalculate();
                case LotteryConfig.REAL_TIME.OPTIONAL_FOUR_SINGLE:
                case LotteryConfig.REAL_TIME.OPTIONAL_THREE_SINGLE:
                case LotteryConfig.REAL_TIME.OPTIONAL_TWO_SINGLE:
                    return atWillPlayCalculate() * combinationValue;
            }
        }

        return 0;
    }

    private int commonDirectionSpan() {
        int totalNumbers = 0;
        List<String> row = Arrays.asList(mSelected.get(0).split(","));

        if (row.size() < 1) {
            return totalNumbers;
        }

        if (mDigitNumbers == 3) {
            int count;
            for (String number : row) {
                count = 0;
                for (int i = 0; i < 10; ++i) {
                    for (int j = 0; j < 10; ++j) {
                        for (int k = 0; k < 10; ++k) {
                            List<Integer> nums = new ArrayList<>();
                            nums.add(i);
                            nums.add(j);
                            nums.add(k);
                            Collections.sort(nums);

                            if (Integer.parseInt(number) == (nums.get(2)-nums.get(0))) {
                                ++count;
                            }
                        }
                    }
                }

                totalNumbers += count;
            }
        }
        else if (mDigitNumbers == 2) {
            int count;
            for (String number : row) {
                count = 0;
                for (int i = 0; i < 10; ++i) {
                    for (int j = 0; j < 10; ++j) {
                        List<Integer> nums = new ArrayList<>();
                        nums.add(i);
                        nums.add(j);
                        Collections.sort(nums);

                        if (Integer.parseInt(number) == (nums.get(1)-nums.get(0))) {
                            ++count;
                        }
                    }
                }

                totalNumbers += count;
            }
        }

        return totalNumbers;
    }

    private int commonCombination() {
        if (!isAllPositionSelected()) {
            return 0;
        }

        int totalNumbers = 0;
        for (String s : mSelected) {
            String[] array = s.split(",");
            if (totalNumbers > 0) {
                totalNumbers *= array.length;
            }
            else {
                totalNumbers = array.length;
            }
        }
        totalNumbers *= mDigitNumbers;

        return totalNumbers;
    }

    private int commonGroupSelection() {
        switch (LotteryConfig.REAL_TIME.PLAY_TYPE_REDIO_MAP.get(mPlayTypeRadioName)) {
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_120:
                return getGroupSelectionNum(0, 0, 5);
            case LotteryConfig.REAL_TIME.FOUR_STAR_GROUP_SELECTION:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_24:
                return getGroupSelectionNum(0, 0, 4);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_6:
                return getGroupSelectionNum(0, 0, 2);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_60:
                return getGroupSelectionNum(1, 1, 3);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_30:
                return getGroupSelectionNum(2, 1, 2);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_20:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_12:
                return getGroupSelectionNum(1, 1, 2);
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_10:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_5:
            case LotteryConfig.REAL_TIME.GROUP_SELECTION_4:
                return getGroupSelectionNum(1, 1, 1);
            default:
                return 0;
        }
    }

    private int getGroupSelectionNum(int type, int oneNumSize, int moreNumSize) {
        List<String> moreNumRow = new ArrayList<>();
        List<String> oneNumRow = new ArrayList<>();
        int totalNumbers = 0;

        switch (type) {
            case 0:
                int count = commonSelectedSize();
                if (count >= moreNumSize) {
                    return combination(count, moreNumSize);
                }

                return totalNumbers;
            case 1:
                for (String numStr : mSelected.get(0).split(",")) {
                    if (!numStr.isEmpty()) {
                        oneNumRow.add(numStr);
                    }
                }
                for (String numStr : mSelected.get(1).split(",")) {
                    if (!numStr.isEmpty()) {
                        moreNumRow.add(numStr);
                    }
                }
                break;
            case 2:
                for (String numStr : mSelected.get(0).split(",")) {
                    if (!numStr.isEmpty()) {
                        moreNumRow.add(numStr);
                    }
                }
                for (String numStr : mSelected.get(1).split(",")) {
                    if (!numStr.isEmpty()) {
                        oneNumRow.add(numStr);
                    }
                }
                break;
            default:
                return totalNumbers;
        }

        if (oneNumRow.size() >= oneNumSize && moreNumRow.size() >= moreNumSize) {
            int count;
            for (String first : oneNumRow) {
                count = 0;
                for (String second : moreNumRow) {
                    if (!first.contains(second)) {
                        ++count;
                    }
                }

                if (oneNumRow.size() >= oneNumSize && count >= moreNumSize) {
                    totalNumbers += combination(count, moreNumSize);
                }
            }
        }

        return totalNumbers;
    }

    private int atWillPlayCalculate() {
        int count = 0;

        if (mPlayTypeRadioName.contains("单式") && !mSelected.isEmpty()) {
            int num = mSelected.get(0).split(",").length;
            for (String s : mSelected) {
                Log.i("CalculatorAtWillSingleS", s);
                if (s.split(",").length != num) {
                    return 0;
                }
            }
            if (mSelected.size() == mDigitNumbers) {
                Log.i("CalculatorAtWillSingle", "Here2");
                return num;
            } else {
                Log.i("CalculatorAtWillSingle", "Here3 num = " + Integer.toString(num));
                return 0;
            }
        }

        for (String s : mSelected) {
            Log.i("CalculatorAtWill", s);
            if (!isEmpty(s) && !s.equals(",")) {
                count++;
            }
        }
        if (count >= mDigitNumbers) {
            List<Integer> list = new ArrayList<>();
            for (String str : mSelected) {
                if (!isEmpty(str)) {
                    int pickNum = str.split(",").length;
                    if (pickNum > 0) {
                        list.add(pickNum);
                    }
                }
            }
            return getSumCount(list);
        }
        return 0;
    }

    private int getSumCount(List<Integer> list) {
        mSumCount = 0;
        getNext(0, list, mDigitNumbers);
        return mSumCount;
    }

    private void getNext(int obj1, List<Integer> obj2, int obj3) {
        if (obj3 > 1) {
            for (int i = 0; i < (obj2.size() - obj3 + 1); i++) {
                int str = obj2.get(i);
                List<Integer> list = new ArrayList<>();
                for (int j = i + 1; j < obj2.size(); j++) {
                    list.add(obj2.get(j));

                }
                this.getNext(obj1 == 0 ? str : obj1 * str, list, obj3 - 1);
            }
        } else {
            for (int b = 0; b < obj2.size(); b++) {
                mSumCount += (obj1 * obj2.get(b));
            }
        }
    }
}
