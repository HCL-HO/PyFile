package com.hec.app.lottery.base;

import java.util.Comparator;

/**
 * Created by jhezenhu on 2017/4/14.
 */

public class SortComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        return (Integer.parseInt(lhs.toString()) - Integer.parseInt(rhs.toString()));
    }
}
