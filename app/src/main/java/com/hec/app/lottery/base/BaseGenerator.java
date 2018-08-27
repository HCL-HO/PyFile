package com.hec.app.lottery.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public abstract class BaseGenerator {
    protected int mDigitNumbers = 5;
    protected String mPlayTypeName;
    protected String mPlayTypeRadioName;
    private int mMin;
    private int mMax;

    public BaseGenerator(String playTypeName, String playTypeRadioName, int min, int max) {
        this.mPlayTypeName = playTypeName;
        this.mPlayTypeRadioName = playTypeRadioName;
        this.mMin = min;
        this.mMax = max;
    }

    protected int generateOneNumber() {
        return (int) (Math.random() * (mMax + 1 - mMin) + mMin);
    }

    protected int generateOneNumberWithBoundray(int min, int max){
        return (int) (Math.random() * (max + 1 - min) + min);
    }

    protected List<String> generateCommon() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < mDigitNumbers; i++) {
            list.add(String.valueOf(generateOneNumber()));
        }
        return list;
    }

    protected List<String> generateNonRepeat() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < mDigitNumbers; i++) {
            while (true) {
                String number = String.valueOf(generateOneNumber());
                if (!list.contains(number)) {
                    list.add(number);
                    break;
                }
            }
        }
        return list;
    }

    //生成同一行的随机数并按大小排序
    protected String generateNonRepeatNumbers(int excludeNumber) {
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mDigitNumbers; i++) {
            while (true) {
                String number = String.valueOf(generateOneNumber());
                boolean equal = String.valueOf(excludeNumber).equals(number);
                if (!list.contains(number) && !equal) {
                    list.add(number);
                    break;
                }
            }
        }

        Collections.sort(list, new SortComparator());
        for (String s : list) {
            sb.append(s + ",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    protected List<String> generateCombination() {
        List<String> list = new ArrayList<>();
        List<Integer> sortList = new ArrayList<>();
        String number = "";
        int count = 0;
        while (true) {
            int random = generateOneNumber();
            if (!sortList.contains(random)) {
                sortList.add(random);
                count++;
                if (count == mDigitNumbers)
                    break;
            }
        }
        Comparator comp = new SortComparator();
        Collections.sort(sortList, comp);
        for (Integer i : sortList) {
            number += i.toString() + ",";
        }
        list.add(number.substring(0, number.length() - 1));
        return list;
    }

    //补齐5位，针对时时彩任选和定位胆
    protected void fillingFivePosition(List<String> list) {
        int count = list.size();
        if (count < 5) {
            for (Integer i = 0; i < 5 - count; i++) {
                list.add(",");
            }
        }
    }

    //补齐3位，针对十一选五和福彩3D，体彩
    protected void fillingThreePosition(List<String> list) {
        int count = list.size();
        if (count < 3) {
            for (Integer i = 0; i < 3 - count; i++) {
                list.add("");
            }
        }
    }

    //补齐10位，针对北京PK10
    protected void fillingPK10Position(List<String> list) {
        int count = list.size();
        if (count < 10) {
            for (Integer i = 0; i < 10 - count; i++) {
                list.add("");
            }
        }
    }

    // 大小单双
    protected List<String> generateBigSmallOddEven() {
        List<String> list = new ArrayList<>();
        list.add("大");
        list.add("小");
        list.add("单");
        list.add("双");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < mDigitNumbers; i++) {
            int index = (int) (Math.random() * 4);
            result.add(list.get(index));
        }
        return result;
    }

    // 直选和值
    protected List<String> generateDirectionSum() {
        int max = 0;
        if (mDigitNumbers == 3) {
            max = 27;
        }
        else if (mDigitNumbers == 2) {
            max = 18;
        }
        else {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (int i = 0; i <= max; i++) {
            list.add(String.valueOf(i));
        }

        int index = (int) (Math.random() * max+1);
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index));

        return result;
    }

    // 组选和值
    protected List<String> generateCombinationSum() {
        int max = 0;
        if (mDigitNumbers == 3) {
            max = 26;
        }
        else if (mDigitNumbers == 2) {
            max = 17;
        }
        else {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (int i = 1; i <= max; i++) {
            list.add(String.valueOf(i));
        }

        int index = (int) (Math.random() * max+1);
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index));

        return result;
    }

    public abstract List<String> generate();
}
