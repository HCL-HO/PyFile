package com.hec.app.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hec on 2015/11/12.
 */
public class LotteryCalculator {

    public static int calculate(String lotteryName, String playTypeName, String playTypeRadioName, List<String> selected) {
        if (lotteryName.contains("韩国") || lotteryName.contains("快乐8") || lotteryName.contains("分分彩") ||
                lotteryName.contains("台湾") || lotteryName.contains("时时彩") || lotteryName.contains("三分彩") || (lotteryName.contains("秒秒彩") && !lotteryName.contains("PK"))) {
            return new RealtimeCalculator(playTypeName, playTypeRadioName, selected).calculate();
        } else if (lotteryName.contains("十一选五")) {
            return new SelectFiveCalculator(playTypeName, playTypeRadioName, selected).calculate();
        } else if (lotteryName.contains("PK")) {
            return new PK10Calculator(playTypeName, playTypeRadioName, selected).calculate();
        } else if (lotteryName.contains("福彩3D") || lotteryName.contains("体彩排列三")) {
            return new Welfare3DCalculator(playTypeName, playTypeRadioName, selected).calculate();
        } else if (lotteryName.contains("江苏快三")) {
            return new KuaiSanCalculator(playTypeName, playTypeRadioName, selected).calculate();
        }
        return 0;
    }
}

abstract class BaseCalculator {
    protected int digitNumbers = 5;
    protected String playTypeName;
    protected String playTypeRadioName;
    protected List<String> selected;

    public BaseCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        this.playTypeName = playTypeName;
        this.playTypeRadioName = playTypeRadioName;
        this.selected = selected;
    }

    protected int factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        } else {
            int multip = 1;
            for (int i = n; i > 0; i--) {
                multip = multip * i;
            }
            return multip;
            //return n * factorial(n - 1);
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
            } else {
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
        for (String s :
                a) {
            if (b.contains(s)) {
                ret.add(s);
            }
        }
        return ret;
    }

    private static ArrayList ls = new ArrayList();

    public List<String> permutation(List<String> inputList) {
        List<String> resList = new ArrayList<>();
        permutationInt(inputList, resList, 0,
                new char[inputList.size()]);
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
        } else {
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
        if (selected != null) {
            for (String s :
                    selected) {
                if (!isEmpty(s)) {
                    String[] array = s.split(",");
                    if (array.length > 0) {
                        count++;
                    }
                }
            }
        }
        return count == digitNumbers;
    }

    protected int commonCalculate() {
        int totalNumbers = 0;
        if (!isAllPositionSelected()) {
            return 0;
        }
        if (playTypeRadioName.contains("单式") && !selected.isEmpty()) {
            int num = selected.get(0).split(",").length;
            for (String s : selected) {
                if (s.split(",").length != num)
                    return 0;
            }
            return num;
        }
        for (String s :
                selected) {
            String[] array = s.split(",");
            if (totalNumbers > 0) {
                totalNumbers *= array.length;
            } else {
                totalNumbers = array.length;
            }
        }

        return totalNumbers;
    }

    public abstract int calculate();


    public boolean isEmpty(String value) {
        return value == null || value.trim().equals("") || (value.length() == 0);
    }
}

class RealtimeCalculator extends BaseCalculator {

    int sumCount = 0;

    public RealtimeCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("五星", 1);
        map.put("四星", 2);
        map.put("三星直选", 3);
        map.put("三星组选", 4);
        map.put("二星直选", 5);
        map.put("二星组选", 6);
        map.put("不定位胆", 7);
        map.put("定位胆", 8);
        map.put("任选", 9);
        map.put("大小单双", 10);

        if (playTypeName != null) {
            switch (map.get(playTypeName)) {
                case 1:
                    digitNumbers = 5;
                    return commonCalculate();
                case 2:
                    digitNumbers = 4;
                    return commonCalculate();
                case 3:
                    digitNumbers = 3;
                    return commonCalculate();
                case 4:
                    digitNumbers = 2;
                    return threeStarCombinationCalculate();
                case 5:
                    digitNumbers = 2;
                    return commonCalculate();
                case 6:
                    digitNumbers = 2;
                    return twoStarCombinationCalculate();
                case 7:
                    return unSpecificPositionCalculate();
                case 8:
                    return specificPositionCalculate();
                case 9:
                    return atWillPlayCalculate();
                case 10:
                    return largeSmallSingleDoubleCalculate();
                default:
                    return 0;
            }
        }
        return 0;
    }

    private int threeStarCombinationCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            int pickNum = s.split(",").length;
            if (playTypeRadioName.contains("组三")) {
                totalNumbers = pickNum * 3 * (pickNum - 1) * 1;
            } else {
                // 选三组六 N*(N-1)*(N-2)
                totalNumbers = pickNum * (pickNum - 1) * (pickNum - 2);
            }
        }
        return totalNumbers;
    }

    //两星组选
    private int twoStarCombinationCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            int pickNum = s.split(",").length;
            totalNumbers = permutation(pickNum, 2);
        }
        return totalNumbers;
    }

    private int unSpecificPositionCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            totalNumbers += s.split(",").length;
        }
        return totalNumbers;
    }

    private int specificPositionCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            if (!s.equals(",") && !s.equals("")) {
                totalNumbers += s.split(",").length;
            }
        }
        return totalNumbers;
    }

    private int atWillPlayCalculate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("任选四复式", 1);
        map.put("任选四单式", 2);
        map.put("任选三复式", 3);
        map.put("任选三单式", 4);
        map.put("任选二复式", 5);
        map.put("任选二单式", 6);

        switch (map.get(this.playTypeRadioName)) {
            case 1:
                digitNumbers = 4;
                break;
            case 2:
                digitNumbers = 4;
                break;
            case 3:
                digitNumbers = 3;
                break;
            case 4:
                digitNumbers = 3;
                break;
            case 5:
                digitNumbers = 2;
                break;
            case 6:
                digitNumbers = 2;
                break;
            default:
                break;
        }
        int count = 0;

        if (playTypeRadioName.contains("单式") && !selected.isEmpty()) {
            int num = selected.get(0).split(",").length;
            for (String s : selected) {
                Log.i("CalculatorAtWillSingleS", s);
                if (s.split(",").length != num) {
                    return 0;
                }
            }
            if (selected.size() == digitNumbers) {
                Log.i("CalculatorAtWillSingle", "Here2");
                return num;
            } else {
                Log.i("CalculatorAtWillSingle", "Here3 num = " + Integer.toString(num));
                return 0;
            }
        }

        for (String s :
                selected) {
            Log.i("CalculatorAtWill", s);
            if (!isEmpty(s) && !s.equals(",")) {
                count++;
            }
        }
        if (count >= digitNumbers) {
            List<Integer> list = new ArrayList<>();
            for (String str :
                    selected) {
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

    private int largeSmallSingleDoubleCalculate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("前二大小单双", 1);
        map.put("后二大小单双", 2);
        map.put("前三大小单双", 3);
        map.put("后三大小单双", 4);
        switch (map.get(this.playTypeRadioName)) {
            case 1:
            case 2:
                digitNumbers = 2;
                break;
            case 3:
            case 4:
                digitNumbers = 3;
                break;
            default:
                break;
        }
        return commonCalculate();
    }

    private int getSumCount(List<Integer> list) {
        sumCount = 0;
        getNext(0, list, digitNumbers);
        return sumCount;
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
                sumCount += (obj1 * obj2.get(b));
            }
        }
    }
}

class SelectFiveCalculator extends BaseCalculator {

    public SelectFiveCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        if (selected.isEmpty())
            return 0;
        Map<String, Integer> map = new HashMap<>();

        map.put("三星直选", 1);
        map.put("三星组选", 2);
        map.put("二星直选", 3);
        map.put("二星组选", 4);
        map.put("不定位胆", 5);
        map.put("定位胆", 6);
        map.put("任选复式", 7);
        map.put("任选胆拖", 8);

        switch (map.get(playTypeName)) {
            case 1:
                digitNumbers = 3;
                return threeStarDirectionCalculate();
            case 2:
                digitNumbers = 1;
                return threeStarCombinationCalculate();
            case 3:
                digitNumbers = 2;
                return twoStarDirectionCalculate();
            case 4:
                digitNumbers = 1;
                return twoStarCombinationCalculate();
            case 5:
                digitNumbers = 1;
                return unspecificPositionCalculate();
            case 6:
                return specificPositionCalculate();
            case 7:
                digitNumbers = 1;
                return atWillDoubleCalculate();
            case 8:
                digitNumbers = 2;
                return atWillTowedCalculate();
            default:
                return 0;
        }
    }

    private int threeStarDirectionCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(selected.get(2).split(","));
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

    private int threeStarCombinationCalculate() {
        int pickNum = selected.get(0).split(",").length;
        int totalNums = (pickNum * (pickNum - 1) * (pickNum - 2)) / (3 * 2 * 1);
        return totalNums;
    }

    private int twoStarDirectionCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));

        List<String> fEs = interSect(firstRow, secondRow);

        int repeatNums = fEs.size();

        int totalNums = firstRow.size() * secondRow.size() - repeatNums;
        return totalNums;
    }

    private int twoStarCombinationCalculate() {
        int pickNum = selected.get(0).split(",").length;
        return combination(pickNum, 2);
    }

    private int unspecificPositionCalculate() {
        return selected.get(0).split(",").length;
    }

    private int specificPositionCalculate() {
        int total = 0;
        for (String s :
                selected) {
            if (!isEmpty(s)) {
                total += s.split(",").length;
            }
        }
        return total;
    }

    private int atWillDoubleCalculate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("一中一", 1);
        map.put("二中二", 2);
        map.put("三中三", 3);
        map.put("四中四", 4);
        map.put("五中五", 5);
        map.put("六中五", 6);
        map.put("七中五", 7);
        map.put("八中五", 8);
        int pickNum = selected.get(0).split(",").length;
        switch (map.get(playTypeRadioName)) {
            case 1:
                return combination(pickNum, 1);
            case 2:
                return combination(pickNum, 2);
            case 3:
                return combination(pickNum, 3);
            case 4:
                return combination(pickNum, 4);
            case 5:
                return combination(pickNum, 5);
            case 6:
                return combination(pickNum, 6);
            case 7:
                return combination(pickNum, 7);
            case 8:
                return combination(pickNum, 8);
            default:
                return 0;
        }
    }

    private int getMaxNumsInBet() {
        Map<String, Integer> map = new HashMap<>();

        map.put("二中二", 1);
        map.put("三中三", 2);
        map.put("四中四", 3);
        map.put("五中五", 4);
        map.put("六中五", 5);
        map.put("七中五", 6);
        map.put("八中五", 7);
        switch (map.get(playTypeRadioName)) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            default:
                return 0;
        }
    }

    private int atWillTowedCalculate() {
        Map<String, Integer> map = new HashMap<>();
        if (isEmpty(selected.get(0)) || isEmpty(selected.get(1))) {
            return 0;
        }
        int boldNums = selected.get(0).split(",").length;
        int towedNums = selected.get(1).split(",").length;
        int m = getMaxNumsInBet() - boldNums;
        map.put("二中二", 1);
        map.put("三中三", 2);
        map.put("四中四", 3);
        map.put("五中五", 4);
        map.put("六中五", 5);
        map.put("七中五", 6);
        map.put("八中五", 7);

        switch (map.get(playTypeRadioName)) {
            case 1:
                return towedNums;
            case 2:
                return combination(towedNums, m);
            case 3:
                return combination(towedNums, m);
            case 4:
                return combination(towedNums, m);
            case 5:
                return combination(towedNums, m);
            case 6:
                return combination(towedNums, m);
            case 7:
                return combination(towedNums, m);
            default:
                return 0;
        }
    }
}

class Welfare3DCalculator extends BaseCalculator {

    public Welfare3DCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("三星直选", 1);
        map.put("三星组选", 2);
        map.put("二星直选", 3);
        map.put("不定位胆", 4);
        map.put("定位胆", 5);

        switch (map.get(playTypeName)) {
            case 1:
                digitNumbers = 3;
                return commonCalculate();
            case 2:
                digitNumbers = 3;
                return threeStarCombinationCalculate();
            case 3:
                digitNumbers = 2;
                return commonCalculate();
            case 4:
                return unSpecificPositionCalculate();
            case 5:
                return specificPositionCalculate();
            default:
                return 0;
        }
    }

    private int threeStarCombinationCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            int pickNum = s.split(",").length;
            if (playTypeRadioName.contains("组三")) {
                totalNumbers = pickNum * 3 * (pickNum - 1) * 1;
            } else {
                // 选三组六 N*(N-1)*(N-2)
                totalNumbers = pickNum * (pickNum - 1) * (pickNum - 2);
            }
        }
        return totalNumbers;
    }

    private int unSpecificPositionCalculate() {
        if (!selected.isEmpty())
            return selected.get(0).split(",").length;
        return 0;
    }

    private int specificPositionCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            if (!isEmpty(s)) {
                totalNumbers += s.split(",").length;
            }
        }
        return totalNumbers;
    }
}

class PK10Calculator extends BaseCalculator {
    public PK10Calculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("前一", 1);
        map.put("前二", 2);
        map.put("前三", 3);
        map.put("前四", 4);
        map.put("前五", 5);
        map.put("前六", 11);
        map.put("定位胆", 6);
        map.put("龙虎斗", 7);
        map.put("大小", 8);
        map.put("单双", 9);
        map.put("和值", 10);
        if (map.containsKey(playTypeName)) {
            switch (map.get(playTypeName)) {
                case 1:
                    return firstOneCalculate();
                case 2:
                    digitNumbers = 2;
                    return firstTwoCalculate();
                case 3:
                    digitNumbers = 3;
                    return firstThreeCalculate();
                case 4:
                    digitNumbers = 4;
                    return firstFourCalculate();
                case 5:
                    digitNumbers = 5;
                    return firstFiveCalculate();
                case 6:
                    return specificPositionCalculate();
                case 7:
                case 8:
                case 9:
                    return singleCalculate();
                case 10:
                    return sumCalculate();
                case 11:
                    digitNumbers = 6;
                    return firstSixCalculate();
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    private int firstOneCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            totalNumbers = s.split(",").length;
        }
        return totalNumbers;
    }

    private int firstTwoCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));
        List<String> fEs = interSect(firstRow, secondRow);
        int repeatNums = fEs.size();
        int totalNums = firstRow.size() * secondRow.size() - repeatNums;
        return totalNums;
    }

    private int firstThreeCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(selected.get(2).split(","));
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
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(selected.get(2).split(","));
        List<String> fourthRow = Arrays.asList(selected.get(3).split(","));
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
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(selected.get(2).split(","));
        List<String> fourthRow = Arrays.asList(selected.get(3).split(","));
        List<String> fifthRow = Arrays.asList(selected.get(4).split(","));
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

    private int firstSixCalculate() {
        if (!isAllPositionSelected()) {
            return 0;
        }
        List<String> firstRow = Arrays.asList(selected.get(0).split(","));
        List<String> secondRow = Arrays.asList(selected.get(1).split(","));
        List<String> thirdRow = Arrays.asList(selected.get(2).split(","));
        List<String> fourthRow = Arrays.asList(selected.get(3).split(","));
        List<String> fifthRow = Arrays.asList(selected.get(4).split(","));
        List<String> sixRow = Arrays.asList(selected.get(5).split(","));
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

    private int specificPositionCalculate() {
        int totalNumbers = 0;
        for (String s :
                selected) {
            if (!s.equals(",") && !s.equals("")) {
                totalNumbers += s.split(",").length;
            }
        }
        return totalNumbers;
    }

    private int singleCalculate() {
        int total = 0;
        for (String s : selected) {
            if (!isEmpty(s)) {
                total += 1;
            }
        }
        return total;
    }

    private int sumCalculate() {
        int totalNums = 0;
        for (String s : selected) {
            if (!isEmpty(s)) {
                List<String> list = Arrays.asList(s.split(","));
                for (String s1 : list) {
                    totalNums += getNums(Integer.parseInt(s1));
                }
            }
        }
        return totalNums;
    }

    private int getNums(int num) {
        switch (num) {
            case 3:
                return 1 * 2;
            case 4:
                return 1 * 2;
            case 5:
                return 2 * 2;
            case 6:
                return 2 * 2;
            case 7:
                return 3 * 2;
            case 8:
                return 3 * 2;
            case 9:
                return 4 * 2;
            case 10:
                return 4 * 2;
            case 11:
                return 5 * 2;
            case 12:
                return 4 * 2;
            case 13:
                return 4 * 2;
            case 14:
                return 3 * 2;
            case 15:
                return 3 * 2;
            case 16:
                return 2 * 2;
            case 17:
                return 2 * 2;
            case 18:
                return 1 * 2;
            case 19:
                return 1 * 2;
            default:
                return 0;
        }
    }
}

class KuaiSanCalculator extends BaseCalculator {
    public KuaiSanCalculator(String playTypeName, String playTypeRadioName, List<String> selected) {
        super(playTypeName, playTypeRadioName, selected);
    }

    @Override
    public int calculate() {
        if (selected.isEmpty())
            return 0;
        Map<String, Integer> map = new HashMap<>();

        map.put("三连号通选", 1);
        map.put("三不同号", 2);
        map.put("三同号单选", 3);
        map.put("三同号通选", 4);
        map.put("二同号单选", 5);
        map.put("二同号复选", 6);
        map.put("二不同号", 7);
        map.put("大小", 8);
        map.put("单双", 9);
        map.put("和值", 10);
        map.put("猜一个号", 11);
        map.put("三不同号胆拖选号", 12);
        map.put("三不同和值", 13);
        map.put("二不同号胆拖选号", 14);

        switch (map.get(playTypeName)) {
            case 1: // 三连号通选
            case 4: // 三同号通选
                digitNumbers = 3;
                return TLHTXCalculate();
            case 2: // 三不同号
            case 12: // 三不同号胆拖选号
            case 13: // 三不同和值
                digitNumbers = 1;
                return TBTHCalculate();
            case 3: // 三同号单选
                digitNumbers = 2;
                return TTHDXCalculate();
            case 5: // 二同号单选
                digitNumbers = 1;
                return ETHDXCalculate();
            case 6: // 二同号复选
                return ETHFXCalculate();
            case 7: // 二不同号
                digitNumbers = 1;
                return EBTHCalculate();
            case 8: // 大小
            case 9: // 单双
                return singleCalculate();
            case 10: // 和值
            case 11: // 猜一个号
                return ETHFXCalculate();
            default:
                return 0;
        }
    }

    // 三連號通選-24種組合,只有一注
    private int TLHTXCalculate() {
        return 1;
    }

    // 三不同号
    private int TBTHCalculate() {
        int totalNums = 0;

        if (playTypeRadioName.contains("单式") && !selected.isEmpty()) {
            int num = selected.get(0).split(",").length;
            for (String s : selected) {
                if (s.split(",").length != num)
                    return 0;
            }
            return num;
        }

        if (playTypeRadioName.contains("标准选号")) { // 三不同號-C selected.get(0).len取3
            int length = selected.get(0).split(",").length;
            totalNums = combination(length, 3);

        } else if (playTypeRadioName.contains("胆拖选号")) { // 三不同號-C 拖數 取 3-膽數
            if (isEmpty(selected.get(0)) || isEmpty(selected.get(1))) {
                return 0;
            }
            int boldNums = selected.get(0).split(",").length;
            int towedNums = selected.get(1).split(",").length;
            if(boldNums == 1  && towedNums == 2){
                return 1;
            }
            totalNums = combination(towedNums, 3 - boldNums);

        } else if (playTypeRadioName.contains("三不同和值")) {
            String[] numbers = selected.get(0).split(",");
            for (String number : numbers) {
                int tmp = Integer.parseInt(number);
                if (tmp == 6 || tmp == 7 || tmp == 14 || tmp == 15) {
                    totalNums += 1;
                } else if (tmp == 8 || tmp == 13)
                    totalNums += 2;
                else
                    totalNums += 3;
            }
        }
        return totalNums;
    }

    // 三同号单选
    private int TTHDXCalculate() {
        int length = selected.get(0).split(",").length;
        return length > 0 ? length : 0;
    }

    // 二同号单选
    private int ETHDXCalculate() {
        if (selected.get(0).isEmpty() || selected.get(1).isEmpty()) {
            return 0;
        }
        int total = 0;
        int firstLen = selected.get(0).split(",").length;
        int secondLen = selected.get(1).split(",").length;
        if (firstLen != 0 && secondLen != 0)
            total = firstLen * secondLen;
        return total;
    }

    // 和值 & 猜一个号
    private int ETHFXCalculate() {
        if (selected.get(0).isEmpty()) {
            return 0;
        }
        return selected.get(0).split(",").length;
    }

    // 二不同号
    private int EBTHCalculate() { // 二不同號-C selected.get(0).len取2
        if (playTypeRadioName.contains("单式") && !selected.isEmpty()) {
            int num = selected.get(0).split(",").length;
            for (String s : selected) {
                if (s.split(",").length != num)
                    return 0;
            }
            return num;
        }

        int totalNums = 0;
        if (playTypeRadioName.contains("二不同号")) {
            int length = selected.get(0).split(",").length;
            totalNums = combination(length, 2);
        } else if (playTypeRadioName.contains("胆拖选号")) {
            if (isEmpty(selected.get(0)) || isEmpty(selected.get(1))) {
                return 0;
            }
            int boldNums = selected.get(0).split(",").length;
            int towedNums = selected.get(1).split(",").length;
            if (playTypeName.compareTo("二不同号") == 0)
                totalNums = towedNums;
        }

        return totalNums;
    }

    // 大小 & 单双, 要扣除“,”才算是真正的注數
    private int singleCalculate() {
        return selected.get(0).split(",").length;
    }
}

