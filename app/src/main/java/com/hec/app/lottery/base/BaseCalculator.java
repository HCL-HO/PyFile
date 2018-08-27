package com.hec.app.lottery.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public abstract class BaseCalculator {
    protected int mDigitNumbers = 5;
    protected String mPlayTypeName;
    protected String mPlayTypeRadioName;
    protected List<String> mSelected;

    public BaseCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        this.mPlayTypeName = playTypeName;
        this.mPlayTypeRadioName = playTypeRadioName;
        this.mSelected = selected;
    }

    protected int factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        else {
            int multip = 1;
            for(int i=n;i>0;i--){
                multip = multip * i;
            }
            return multip;
        }
    }

    protected int permutation(int n, int m) {
        int ret = 0;
        for (int i = 0; i < m; i++) {
            if ((n - i) < 0) {
                break;
            }

            if (ret == 0) {
                ret = n;
            }
            else {
                ret *= (n - i);
            }
        }
        return ret;
    }

    protected int combination(int n, int m) {
        int p = permutation(n, m);
        int f = factorial(m);
        return p / f;
    }

    protected List<String> interSect(List<String> a, List<String> b) {
        List<String> ret = new ArrayList<>();
        for (String s : a) {
            if (b.contains(s)) {
                ret.add(s);
            }
        }
        return ret;
    }

    private static ArrayList ls = new ArrayList();

    public List<String> permutation(List<String> inputList) {
        List<String> resList = new ArrayList<>();
        permutationInt(inputList, resList, 0, new char[inputList.size()]);
        return resList;
    }

    private void permutationInt(List<String> inputList, List<String> resList,
                                int ind, char[] arr) {
        if (ind == inputList.size()) {
            resList.add(new String(arr));
            return;
        }

        for (char c : inputList.get(ind).toCharArray()) {
            arr[ind] = c;
            permutationInt(inputList, resList, ind + 1, arr);
        }
    }

    protected List<String> allPossibleCases(List<List<String>> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        else {
            List<String> result = new ArrayList<>();
            List<List<String>> p = new ArrayList<>();

            for (int index = 0; index < list.size(); index++) {
                if (index > 0) {
                    p.add(list.get(index));
                }
            }

            List<String> allCasesOfRest = allPossibleCases(p);
            for (int i = 0; i < allCasesOfRest.size(); i++) {
                for (int j = 0; j < list.get(0).size(); j++) {
                    String r = list.get(0).get(j) + allCasesOfRest.get(i);
                    result.add(r);
                }
            }
            return result;
        }
    }

    protected boolean isAllPositionSelected() {
        int count = 0;
        if (mSelected != null) {
            for (String s : mSelected) {
                if (!isEmpty(s)) {
                    String[] array = s.split(",");
                    if (array.length > 0) {
                        count++;
                    }
                }
            }
        }
        return count == mDigitNumbers;
    }

    protected int commonCalculate() {
        int totalNumbers = 0;
        if (!isAllPositionSelected()) {
            return 0;
        }

        if (mPlayTypeRadioName.contains("单式") && !mSelected.isEmpty()) {
            int num = mSelected.get(0).split(",").length;
            for (String s : mSelected) {
                if (s.split(",").length != num) {
                    return 0;
                }
            }
            return num;
        }

        for (String s : mSelected) {
            String[] array = s.split(",");
            if (totalNumbers > 0) {
                totalNumbers *= array.length;
            }
            else {
                totalNumbers = array.length;
            }
        }

        return totalNumbers;
    }

    public boolean isEmpty(String value) {
        return value == null || value.trim().equals("") || (value.length() == 0);
    }

    // 直选和值
    protected int commonDirectionSum() {
        int totalNums = 0;
        for (String s : mSelected) {
            if (!isEmpty(s)) {
                List<String> list = Arrays.asList(s.split(","));
                for (String s1 : list) {
                    totalNums += getDirectionSumNumber(Integer.parseInt(s1));
                }
            }
        }
        return totalNums;
    }

    private int getDirectionSumNumber(int num) {
        int count = 0;
        if (mDigitNumbers == 3) {
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    for (int k = 0; k < 10; ++k) {
                        if (num == (i + j + k)) {
                            ++count;
                        }
                    }
                }
            }
        }
        else if (mDigitNumbers == 2) {
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    if (num == (i + j)) {
                        ++count;
                    }
                }
            }
        }

        return count;
    }

    // 组选和值
    protected int commonCombinationSum() {
        int totalNums = 0;
        for (String s : mSelected) {
            if (!isEmpty(s)) {
                List<String> list = Arrays.asList(s.split(","));
                for (String s1 : list) {
                    totalNums += getCombinationSumNumber(Integer.parseInt(s1));
                }
            }
        }
        return totalNums;
    }

    private int getCombinationSumNumber(int num) {
        int count = 0;
        if (mDigitNumbers == 3) {
            for (int i = 0; i < 10; ++i) {
                for (int j = i; j < 10; ++j) {
                    for (int k = j; k < 10; ++k) {
                        if (num == (i + j + k) && !(i == j && j == k)) {
                            ++count;
                        }
                    }
                }
            }
        }
        else if (mDigitNumbers == 2) {
            for (int i = 0; i < 10; ++i) {
                for (int j = i; j < 10; ++j) {
                    if (num == (i + j) && i != j) {
                        ++count;
                    }
                }
            }
        }

        return count;
    }

    // All Size
    protected int commonSelectedSize() {
        int count = 0;
        for (String s : mSelected) {
            if (!isEmpty(s)) {
                count += s.split(",").length;
            }
        }

        return count;
    }

    // 不定位
    protected int commonUnSpecificPosition() {
        int count = commonSelectedSize();
        int totalNumbers = 0;

        switch (mDigitNumbers) {
            case 1:
                // 一码
                totalNumbers = count;
                break;
            case 2:
                // 二码
                if (count >= 2) {
                    totalNumbers = combination(count, 2);
                }
                break;
            case 3:
                // 三码
                if (count >= 3) {
                    totalNumbers = combination(count, 3);
                }
                break;
        }

        return totalNumbers;
    }

    // 三星组选
    protected int commonThreeStarCombination() {
        int totalNumbers = 0;

        if (mPlayTypeRadioName.contains("单式")) {
            if (mSelected.size() == 3) {
                if (mDigitNumbers == 2) {
                    // 组三，2個相同數字1個不同，例:112, 566
                    if ((mSelected.get(0).equals(mSelected.get(1)) && !mSelected.get(1).equals(mSelected.get(2))) ||
                            (mSelected.get(1).equals(mSelected.get(2)) && !mSelected.get(2).equals(mSelected.get(0))) ||
                            (mSelected.get(2).equals(mSelected.get(0)) && !mSelected.get(0).equals(mSelected.get(1)))) {
                        totalNumbers = 1;
                    }
                }
                else if (mDigitNumbers == 3) {
                    // 组六，3個不相同的數，例:123, 852
                    if (!mSelected.get(0).equals(mSelected.get(1)) && !mSelected.get(1).equals(mSelected.get(2))) {
                        totalNumbers = 1;
                    }
                }
            }
        }
        else {
            for (String s : mSelected) {
                int pickNum = s.split(",").length;
                if (mDigitNumbers == 2) {
                    // 组三
                    totalNumbers = permutation(pickNum, 2);
                }
                else if (mDigitNumbers == 3) {
                    // 组六
                    totalNumbers = combination(pickNum, 3);
                }
            }
        }

        return totalNumbers;
    }

    // 两星组选
    protected int commonTwoStarCombination() {
        int totalNumbers = 0;

        if (mPlayTypeRadioName.contains("单式")) {
            if (mSelected.size() == 2) {
                if (!mSelected.get(0).equals(mSelected.get(1))) {
                    totalNumbers = 1;
                }
            }
        }
        else {
            for (String s : mSelected) {
                int pickNum = s.split(",").length;
                totalNumbers = combination(pickNum, 2);
            }
        }

        return totalNumbers;
    }

    // 大小单双
    protected int commonBigSmallOddEven() {
        switch (mDigitNumbers) {
            case 1:
                return commonSelectedSize();
            case 2:
            case 3:
                return commonCalculate();
            default:
                return 0;
        }
    }

    // 混合组选
    protected int commonMixedGroupSelection() {
        // 组三、组六
        if (mSelected.size() == 3) {
            if (!(mSelected.get(0).equals(mSelected.get(1)) && mSelected.get(1).equals(mSelected.get(2)))) {
                return 1;
            }
        }

        return 0;
    }

    public abstract int calculate();
}
