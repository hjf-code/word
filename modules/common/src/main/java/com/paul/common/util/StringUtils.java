package com.paul.common.util;

/**
 * 字符串操作工具类, 继承org.springframework.util.StringUtils里的方法
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/25 15:51
 */
public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * StringBuilder移除最后一个字符
     *
     * @param sb 要移除的StringBuilder对象
     * @return java.lang.StringBuilder
     */
    public static StringBuilder removeLastOneChar(StringBuilder sb) {

        return removeLastChar(sb, 1);
    }

    /**
     * StringBuilder移除最后两个字符, 拼接SQL语句时, 需要去掉后面的逗号和空格
     *
     * @param sb 要移除的StringBuilder对象
     * @return java.lang.StringBuilder
     */
    public static StringBuilder removeLastTwoChar(StringBuilder sb) {

        return removeLastChar(sb, 2);
    }

    /**
     * StringBuilder移除后面几个字符
     *
     * @param sb            要移除的StringBuilder对象
     * @param lastCharCount 移除数量, 0或负数不移除
     * @return java.lang.StringBuilder
     */
    public static StringBuilder removeLastChar(StringBuilder sb, int lastCharCount) {

        if (lastCharCount > 0 && sb != null) {
            int start = 0, end = sb.length();
            if (end > lastCharCount) {
                start = end - lastCharCount;
            }
            sb.delete(start, end);
        }
        return sb;
    }

}