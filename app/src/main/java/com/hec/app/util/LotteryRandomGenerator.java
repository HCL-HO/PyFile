package com.hec.app.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hec on 2015/12/16.
 */
public class LotteryRandomGenerator {
    public static List<String> generateSingleBet(String lotteryName, String playTypeName, String playTypeRadioName) {
        BaseGenerator generator = null;
        if (lotteryName.contains("韩国") ||lotteryName.contains("5分彩") || lotteryName.contains("分分彩") || lotteryName.contains("快乐8") ||lotteryName.contains("时时彩") || lotteryName.contains("三分彩") || (lotteryName.contains("秒秒彩")&& !lotteryName.contains("PK"))) {
            generator = new RealtimeGenerator(playTypeName, playTypeRadioName);
        } else if (lotteryName.contains("十一选五")) {
            generator = new SelectFiveGenerator(playTypeName, playTypeRadioName);
        } else if (lotteryName.contains("PK")) {
            generator = new PK10Generator(playTypeName, playTypeRadioName);
        } else if (lotteryName.contains("福彩3D") || lotteryName.contains("体彩排列三")) {
            generator = new Welfare3DGenerator(playTypeName, playTypeRadioName);
        } else if(lotteryName.contains("江苏快三"))
            generator = new KuaiSanGenerator(playTypeName, playTypeRadioName);
        return generator.generate();
    }
}

abstract class BaseGenerator {
    protected int digitNumbers = 5;
    protected String playTypeName;
    protected String playTypeRadioName;
    private int min;
    private int max;

    public BaseGenerator(String playTypeName, String playTypeRadioName, int min, int max) {
        this.playTypeName = playTypeName;
        this.playTypeRadioName = playTypeRadioName;
        this.min = min;
        this.max = max;
    }

    protected int generateOneNumber() {
        return (int) (Math.random() * (max + 1 - min) + min);
    }

    protected int generateOneNumberWithBoundray(int min, int max){
        return (int) (Math.random() * (max + 1 - min) + min);
    }

    protected List<String> generateCommon() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < digitNumbers; i++) {
            list.add(String.valueOf(generateOneNumber()));
        }
        return list;
    }

    protected List<String> generateNonRepeat() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < digitNumbers; i++) {
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
        for (int i = 0; i < digitNumbers; i++) {
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
                if (count == digitNumbers)
                    break;
            }
        }
        Comparator comp = new SortComparator();
        Collections.sort(sortList, comp);
        for (Integer i :
                sortList) {
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

    public abstract List<String> generate();
}

class SortComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        return (Integer.parseInt(lhs.toString()) - Integer.parseInt(rhs.toString()));
    }
}

class RealtimeGenerator extends BaseGenerator {
    public RealtimeGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 0, 9);
    }

    @Override
    public List<String> generate() {
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

        switch (map.get(playTypeName)) {
            case 1:
                digitNumbers = 5;
                return generateCommon();
            case 2:
                digitNumbers = 4;
                return generateCommon();
            case 3:
                digitNumbers = 3;
                return generateCommon();
            case 4:
                if(playTypeRadioName.contains("组三")){
                    digitNumbers = 2;
                }else {
                    digitNumbers = 3;
                }
                return generateCombination();
            case 5:
                digitNumbers = 2;
                return generateCommon();
            case 6:
                digitNumbers = 2;
                return generateCombination();
            case 7:
                digitNumbers = 1;
                return generateCommon();
            case 8:
                digitNumbers = 1;
                List<String> list = generateCommon();
                fillingFivePosition(list);
                return list;
            case 9:
                atWillPlay();
                List<String> list1 = generateCommon();
                fillingFivePosition(list1);
                return list1;
            case 10:
                largeSmallSingleDouble();
                return generateLargeSmallSingleDouble();
            default:
                digitNumbers = 0;
                return null;
        }
    }

    private void atWillPlay() {
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
        }
    }

    private List<String> generateLargeSmallSingleDouble() {
        List<String> list = new ArrayList<>();
        list.add("大");
        list.add("小");
        list.add("单");
        list.add("双");
        int max = 3;
        int min = 0;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < digitNumbers; i++) {
            int index = (int) (Math.random() * (max + 1 - min) + min);
            result.add(list.get(index).toString());
        }
        return result;
    }

    private void largeSmallSingleDouble() {
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
    }
}

class SelectFiveGenerator extends BaseGenerator {
    public SelectFiveGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 1, 11);
    }

    @Override
    public List<String> generate() {
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
                return generateNonRepeat();
            case 2:
                digitNumbers = 3;
                return generateCombination();
            case 3:
                digitNumbers = 2;
                return generateNonRepeat();
            case 4:
                digitNumbers = 2;
                return generateCombination();
            case 5:
                digitNumbers = 1;
                return generateCommon();
            case 6:
                digitNumbers = 1;
                List<String> result = generateCommon();
                fillingThreePosition(result);
                return result;
            case 7:
                List<String> list = generateAtWillDouble();
                Comparator comp = new SortComparator();
                Collections.sort(list, comp);
                List<String> r = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                for (String s : list) {
                    sb.append(s);
                    sb.append(",");
                }
                r.add(sb.substring(0, sb.length() - 1));
                return r;
            case 8:
                return generateAtWillTowed();
            default:
                return null;
        }
    }

    private List<String> generateAtWillDouble() {
        Map<String, Integer> map = new HashMap<>();

        map.put("一中一", 1);
        map.put("二中二", 2);
        map.put("三中三", 3);
        map.put("四中四", 4);
        map.put("五中五", 5);
        map.put("六中五", 6);
        map.put("七中五", 7);
        map.put("八中五", 8);

        switch (map.get(playTypeRadioName)) {
            case 1:
                digitNumbers = 1;
                return generateCommon();
            case 2:
                digitNumbers = 2;
                return generateNonRepeat();
            case 3:
                digitNumbers = 3;
                return generateNonRepeat();
            case 4:
                digitNumbers = 4;
                return generateNonRepeat();
            case 5:
                digitNumbers = 5;
                return generateNonRepeat();
            case 6:
                digitNumbers = 6;
                return generateNonRepeat();
            case 7:
                digitNumbers = 7;
                return generateNonRepeat();
            case 8:
                digitNumbers = 8;
                return generateNonRepeat();
            default:
                return null;
        }
    }

    private List<String> generateAtWillTowed() {
        Map<String, Integer> map = new HashMap<>();

        map.put("二中二", 1);
        map.put("三中三", 2);
        map.put("四中四", 3);
        map.put("五中五", 4);
        map.put("六中五", 5);
        map.put("七中五", 6);
        map.put("八中五", 7);

        List<String> list = new ArrayList<>();
        int danMa;
        String tuoMa;
        switch (map.get(playTypeRadioName)) {
            case 1:
                digitNumbers = 2;
                return generateNonRepeat();
            case 2:
                digitNumbers = 2;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list.add(0, String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            case 3:
                digitNumbers = 3;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list.add(0, String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            case 4:
                digitNumbers = 4;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list.add(0, String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            case 5:
                digitNumbers = 5;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list.add(0, String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            case 6:
                digitNumbers = 6;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list.add(0, String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            case 7:
                digitNumbers = 7;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list.add(0, String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            default:
                return null;
        }
    }
}

class PK10Generator extends BaseGenerator {
    public PK10Generator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 1, 10);
    }

    public List<String> generate() {
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

        switch (map.get(playTypeName)) {
            case 1:
                digitNumbers = 1;
                return generateCommon();
            case 2:
                digitNumbers = 2;
                return generateNonRepeat();
            case 3:
                digitNumbers = 3;
                return generateNonRepeat();
            case 4:
                digitNumbers = 4;
                return generateNonRepeat();
            case 5:
                digitNumbers = 5;
                return generateNonRepeat();
            case 11:
                digitNumbers = 6;
                return generateNonRepeat();
            case 6:
                digitNumbers = 1;
                List<String> result = generateCommon();
                //result.set(0, " " + result.get(0));
                fillingPK10Position(result);
                return result;
            case 7:
                digitNumbers = 1;
                return generateDragonTiger();
            case 8:
                digitNumbers = 1;
                return generateBigSmall();
            case 9:
                digitNumbers = 1;
                return generateOddEven();
            case 10:
                digitNumbers = 1;
                return generateSum();
            default:
                return null;
        }
    }

    private List<String> generateDragonTiger() {
        int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        list.add("龙");
        list.add("虎");
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }

    private List<String> generateBigSmall() {
        int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        list.add("大");
        list.add("小");
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }

    private List<String> generateOddEven() {
        int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        list.add("单");
        list.add("双");
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }

    private List<String> generateSum() {
        int index = (int) (Math.random() * 17 + 1);
        //int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        for (int i = 3; i <= 19; i++) {
            list.add(String.valueOf(i));
        }

        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }
}

class Welfare3DGenerator extends BaseGenerator {
    public Welfare3DGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 0, 9);
    }

    public List<String> generate() {
        Map<String, Integer> map = new HashMap<>();

        map.put("三星直选", 1);
        map.put("三星组选", 2);
        map.put("二星直选", 3);
        map.put("不定位胆", 4);
        map.put("定位胆", 5);

        switch (map.get(playTypeName)) {
            case 1:
                digitNumbers = 3;
                return generateCommon();
            case 2:
                if (playTypeRadioName.equals("组六复式")) {
                    digitNumbers = 3;
                    return generateCombination();
                } else {
                    digitNumbers = 2;
                    return generateCombination();
                }
            case 3:
                digitNumbers = 2;
                return generateCommon();
            case 4:
                digitNumbers = 1;
                return generateCommon();
            case 5:
                digitNumbers = 1;
                List<String> list = generateCommon();
                fillingThreePosition(list);
                return list;
            default:
                return null;
        }
    }
}

class KuaiSanGenerator extends BaseGenerator {

    public KuaiSanGenerator(String playTypeName, String playTypeRadioName) {
        super(playTypeName, playTypeRadioName, 1, 6);
    }

    public List<String> generate() {

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

        switch (map.get(playTypeName)) {
            case 1:
                digitNumbers = 3;
                return TLHTXGenerator();
            case 2:
                return TBTHGenerator();
            case 3:
                digitNumbers = 1;
                List<String> list = new ArrayList<>();
                int tmp = generateOneNumber();
                list.add(tmp+""+tmp+""+tmp);
                return list;
            case 4:
                list = new ArrayList<>();
                list.add("三同号通选");
                return list;
            case 5:
                digitNumbers = 1;
                int danMa;
                String tuoMa;
                danMa = generateOneNumber();
                tuoMa = generateNonRepeatNumbers(danMa);
                list = new ArrayList<>();
                list.add(0, String.valueOf(danMa) + String.valueOf(danMa));
                list.add(1, tuoMa);
                return list;
            case 6:
                digitNumbers = 1;
                list = new ArrayList<>();
                tmp = generateOneNumber();
                list.add(tmp+""+tmp+"*");
                return list;
            case 7:
                if(playTypeRadioName.equals("二不同号")){
                    digitNumbers = 2;
                    List<String> res = new ArrayList<>();
                    res.add(generateNonRepeatNumbers(9));
                    return res;
                }else if(playTypeRadioName.equals("胆拖选号")) {
                    digitNumbers = 1;
                    danMa = generateOneNumber();
                    tuoMa = generateNonRepeatNumbers(danMa);
                    list = new ArrayList<>();
                    list.add(0, String.valueOf(danMa));
                    list.add(1, tuoMa);
                    return list;
                }
            case 8:
                return generateBigSmall();
            case 9:
                return generateOddEven();
            case 10:
                return generateSum();
            case 11:
                digitNumbers = 1;
                return generateCommon();
            default:
                return new ArrayList<>();
        }
    }

    private List<String> TLHTXGenerator(){
        List<String> list = new ArrayList<>();
        list.add("三连号通选");
        return list;
    }

    private List<String> TBTHGenerator(){
        List<String> list = new ArrayList<>();
        if(playTypeRadioName.equals("标准选号")){
            digitNumbers = 3;
            List<String> res = new ArrayList<>();
            res.add(generateNonRepeatNumbers(9));
            return res;
        }else if(playTypeRadioName.equals("胆拖选号")){
            int danMa;
            String tuoMa;
            digitNumbers = 2;
            danMa = generateOneNumber();
            tuoMa = generateNonRepeatNumbers(danMa);
            list.add(0, String.valueOf(danMa));
            list.add(1, tuoMa);
            return list;
        }else if(playTypeRadioName.contains("和值")){
            digitNumbers = 1;
            list = new ArrayList<>();
            for (int i = 0; i < digitNumbers; i++) {
                list.add(String.valueOf(generateOneNumberWithBoundray(6, 15)));
            }
            return list;
        }
        return list;
    }


    private List<String> generateBigSmall() {
        int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        list.add("大");
        list.add("小");
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }

    private List<String> generateOddEven() {
        int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        list.add("单");
        list.add("双");
        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }

    private List<String> generateSum() {
        int index = (int) (Math.random() * 16 + 1);
        //int index = (int) (Math.random() * 2 + 1);
        List<String> list = new ArrayList<>();
        for (int i = 3; i <= 18; i++) {
            list.add(String.valueOf(i));
        }

        ArrayList<String> result = new ArrayList<>();
        result.add(list.get(index - 1));
        return result;
    }


}



