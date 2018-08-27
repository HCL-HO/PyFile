package com.hec.app.util;

import com.google.common.hash.Hashing;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class StringUtil {

    /**
     * Encode URL using "UTF-8" format.
     *
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeURL(String value) throws UnsupportedEncodingException {
        if (!isEmpty(value)) {
            return URLEncoder.encode(value, "UTF-8");
        }
        return "";
    }

    /**
     * Decode URL using "UTF-8" format.
     *
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decodeURL(String value) throws UnsupportedEncodingException {
        if (!isEmpty(value)) {
            return URLDecoder.decode(value, "UTF-8");
        }
        return "";
    }

    /**
     * check if the string is empty
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().equals("") || value.length() == 0;
    }

    /**
     * Check the string whether is long data type
     *
     * @param str
     * @return
     */
    public static boolean isLong(String str) {
        if ("0".equals(str.trim())) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[^0]\\d*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * Check the string whether is float data type
     *
     * @param str
     * @return
     */
    public static boolean isFloat(String str) {
        if (isLong(str)) {
            return true;
        }
        Pattern pattern = Pattern.compile("\\d*\\.{1}\\d+");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * Check the string whether is float data type with precison
     *
     * @param str
     * @param precision
     * @return
     */
    public static boolean isFloat(String str, int precision) {
        String regStr = "\\d*\\.{1}\\d{" + precision + "}";
        Pattern pattern = Pattern.compile(regStr);
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * Check the string whether is number data type
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (isLong(str)) {
            return true;
        }
        Pattern pattern = Pattern.compile("(-)?(\\d*)\\.{0,1}(\\d*)");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * Check the string whether is email data type
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher isEMail = pattern.matcher(str);
        return isEMail.matches();
    }


    public static boolean isIdentityNo(String str) {
        if (str != null) {
            str = str.toLowerCase();
        }
        Pattern pattern = Pattern.compile("^(\\d{18,18}|\\d{15,15}|\\d{17,17}x)$");
        Matcher isIdentityNo = pattern.matcher(str);
        return isIdentityNo.matches();
    }


    /**
     * Format the specified String with param placeholder list. ex. the string
     * like format "I am {0},and she comes from {1}."
     *
     * @param str
     * @param args the param placeholder list.
     * @return the formated string
     */
    public static String format(String str, Object... args) {

        if (StringUtil.isEmpty(str) || args.length == 0) {
            return str;
        }

        String result = str;
        Pattern pattern = java.util.regex.Pattern.compile("\\{(\\d+)\\}");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            if (index < args.length) {
                String valueString = "";
                if (args[index] != null) {
                    valueString = args[index].toString();
                }
                result = result.replace(matcher.group(), valueString);
            }
        }

        return result;
    }

    /**
     * Format price
     *
     * @param price
     * @return format like "￥123.00" or "-￥123.00"
     */
    public static String priceToString(double price) {
        String priceString = new DecimalFormat("###,###,###.##").format(Math.abs(price));
        if (!priceString.contains(".")) {
            priceString = priceString + ".00";
        } else {
            DecimalFormat format = new DecimalFormat("###,###,###.00");
            priceString = format.format(Math.abs(price));
        }
        if (priceString.indexOf(".") == 0) {
            priceString = "0" + priceString;
        }
        if (price < 0) {
            return "￥-" + priceString;
        } else if (price < 1) {
            return "￥" + priceString;
        } else {
            return "￥" + priceString;
        }
    }

    /**
     * format point
     *
     * @param point
     * @return
     */
    public static String getPointToString(float point) {
        String priceString = new DecimalFormat("###,###,###.##").format(Math.abs(point));
        return priceString;
    }

    /**
     * format number to price
     *
     * @param number
     * @return
     */
    public static String getPriceByFloat(float number) {

        String mynumber = String.valueOf(number);
        if (!mynumber.contains(".")) {
            mynumber = mynumber + ".00";
        } else {
            DecimalFormat format = new DecimalFormat("#.00");
            mynumber = format.format(number);
            if (mynumber.indexOf(".") == 0) {
                mynumber = "0" + mynumber;
            }

        }

        return mynumber;

    }

    /**
     * @param number
     * @return
     */
    public static String getPriceByDouble(double number) {

        String mynumber = String.valueOf(number);
        if (!mynumber.contains(".")) {
            mynumber = mynumber + ".00";
        } else {
            DecimalFormat format = new DecimalFormat("#.00");
            mynumber = format.format(number);
            if (mynumber.indexOf(".") == 0) {
                mynumber = "0" + mynumber;
            }
        }

        return mynumber;
    }

    /**
     * get price from string
     *
     * @param inputString
     * @return
     */
    public static float stringToPrice(String inputString) {
        if (isEmpty(inputString))
            return 0.0f;
        if (inputString.contains("￥")) {
            inputString = inputString.replace("￥", "");
        }
        inputString = inputString.replaceAll(",", "");
        return Float.parseFloat(inputString);
    }

    public static String formatDoubleWith4Point(double input) {
        return new DecimalFormat("#########.####").format(input);
    }

    public static String getFormatDate() {
        StringBuffer buffer = new StringBuffer();
//		GregorianCalendar c = new GregorianCalendar();
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 12) {
            hour = hour - 12;
        }
        int minute = c.get(Calendar.MINUTE);
        int ampm = c.get(Calendar.AM_PM);//结果为“0”是上午 结果为“1”是下午
        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("EEEE", Locale.US);
        buffer.append(f.format(c.getTime()));
        buffer.append(" - ");
        if (minute < 30) {
            buffer.append(String.valueOf(hour));
            buffer.append(":00");
            buffer.append(ampm == 0 ? "am" : "pm");
        } else {
            buffer.append(String.valueOf(hour));
            buffer.append(":30");
            buffer.append(ampm == 0 ? "am" : "pm");
        }

        return buffer.toString();
    }

    public static String getMd5Hash(String input) {
        return Hashing.md5().hashString(input, Charset.defaultCharset()).toString();
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] messageDigest = md.digest(input.getBytes());
//            BigInteger number = new BigInteger(1, messageDigest);
//            String md5 = number.toString(16);
//
//            while (md5.length() < 32)
//                md5 = "0" + md5;
//
//            return md5;
//        } catch (NoSuchAlgorithmException e) {
//            TestUtil.print(e.getLocalizedMessage());
//            return null;
//        }
    }

    public static String encrypt(String input){
        String part1 = Hashing.md5().hashString(input, Charset.defaultCharset()).toString().toLowerCase();
        String p1 = part1.substring(3, 3+16);
        String part2 = Hashing.sha1().hashString(p1, Charset.defaultCharset()).toString().toLowerCase();
        return part2.substring(4, 4+14);
    }

    public static String joinHtmlColor(String str, String color) {
        return "<font color=" + color + ">" + str + "</font>";
    }

}