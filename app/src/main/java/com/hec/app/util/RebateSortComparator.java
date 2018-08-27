package com.hec.app.util;

import com.hec.app.entity.SelectListItem;

import java.util.Comparator;

/**
 * Created by jhezenhu on 2017/6/19.
 */

public class RebateSortComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        SelectListItem leftItem = (SelectListItem) lhs;
        SelectListItem rightItem = (SelectListItem) rhs;

        double leftValue = Double.parseDouble(leftItem.getValue());
        double rightValue = Double.parseDouble(rightItem.getValue());
        double value = leftValue - rightValue;

        if (value < 0.0) {
            return -1;
        }
        return 1;
    }
}
