package com.tony.exoplayersample.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tony on 2017/12/1.
 */
public class StringUtil {

    public static boolean isHex(String str) {

        return check(str, "[0123456789abcdef]+");
    }

    public static boolean isOctal(String str) {

        return check(str, "[01234567]+");
    }

    public static boolean isBinary(String str) {

        return check(str, "[01]+");
    }

    public static boolean check(String str, String regex) {
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
    }

    public static double factorial(String str) {

        boolean flag = false;
        double number = 0;
        try {
            number = Double.parseDouble(str);

            if (number == 0) {
                return 0;
            } else if (number < 0) {
                number = -number;
                flag = true;
            }
            if (number > 105) {
                return Double.NaN;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }

        double result = 1;

        for (int i = 1; i < (int)number; i++) {

            result = result * i;
        }

        if (flag) {
            result = -result;
        }

        return result;
    }

    /**
     * 10进制转16进制
     */
    public static String convertToHex(String str) {

        long number = 0;
        try {
            number = Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return Long.toHexString(number);
    }

    /**
     * 10进制转8进制
     */
    public static String convertToOctal(String str) {

        long number = 0;
        try {
            number = Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return Long.toOctalString(number);
    }

    /**
     * 10进制转2进制
     */
    public static String convertToBinary(String str) {

        long number = 0;
        try {
            number = Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return Long.toBinaryString(number);
    }

    /**
     * 16进制转10进制
     */
    public static Long hexToDecimal(String str) {

        try {
            return Long.parseLong(str,16);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Long.MAX_VALUE;
    }

    /**
     * 8进制转10进制
     */
    public static Long octalToDecimal(String str) {

        try {
            return Long.parseLong(str,8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Long.MAX_VALUE;
    }

    /**
     * 2进制转10进制
     */
    public static Long binaryToDecimal(String str) {

        try {
            return Long.parseLong(str,2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Long.MAX_VALUE;
    }

}
