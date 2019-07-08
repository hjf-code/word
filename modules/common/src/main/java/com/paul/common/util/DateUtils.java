package com.paul.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.paul.common.constant.DateConstant.DEFAULT_DATE_PATTERN;

/**
 * 日期工具类
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 19-7-4 下午7:09
 */
public class DateUtils {

    /**
     * 按照给定格式转换日期
     *
     * @param date 日期
     * @param pattern 日期格式
     * @return java.lang.String
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = DEFAULT_DATE_PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String parse(Date date) {

        return format(date, null);
    }
}