package com.paul.common.util;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paul.common.constant.SqlConstant.*;
import static com.paul.common.constant.StringConstant.*;

/**
 * SQL中的关键字, 以及工具类
 *
 * @author paul paul@gmail.com
 * @since 2019/3/28 19:37
 */
public interface SqlUtils {

    /**
     * 将多个id拼接成SQL
     *
     * @param ids id字符串, 以英文逗号隔开, 例: asd, sdfds, ,123655, ,
     * @return java.lang.String 例: ('asd', 'sdfds', '', '123655')
     */
    static String formatIds(Object ids) {

        String[] idArr = ids.toString().split(COMMA);
        Object[] idObjectArr = new Object[idArr.length];
        System.arraycopy(idArr, 0, idObjectArr, 0, idArr.length);
        return formatIdList(Arrays.asList(idObjectArr));
    }

    /**
     * 将多个id拼接成SQL
     *
     * @param ids id列表
     * @return java.lang.String 例: ('asd', 'sdfds', ,'', '123655')
     */
    static String formatIdList(List<Object> ids) {

        StringBuilder sb = new StringBuilder("(");
        for (Object id : ids) {
            sb.append("'").append(id.toString().trim()).append("'").append(COMMA_SPACE);
        }
        return StringUtils.removeLastTwoChar(sb).append(")").toString();
    }

    /**
     * 数组由下划线转换为驼峰
     *
     * @param arr 带有下划线的数组
     * @return java.lang.String[]
     */
    static String[] arrUnderlineToHump(String[] arr) {

        if (arr == null) {
            return null;
        }
        String[] newArr = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = toHump(arr[i]);
        }
        return newArr;
    }

    /**
     * 给查询字段添加对应的表别名前缀, 如: id, name --> a.id, a.name
     *
     * @param alias         表别名
     * @param selectColumns 查询字段
     * @return java.lang.String
     */
    static String addAliasPrefix(String alias, StringBuilder selectColumns) {

        return alias + "." + selectColumns.toString().replace(", ", ", " + alias + ".");
    }

    /**
     * 数组由驼峰转换为下划线
     *
     * @param arr 带有下划线的数组
     * @return java.lang.String[]
     */
    static String[] arrHumpToUnderline(String[] arr) {

        if (arr == null) {
            return null;
        }
        String[] newArr = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = toUnderline(arr[i]);
        }
        return newArr;
    }

    /**
     * 将Map中的key由下划线转换为驼峰
     *
     * @param map 带有下划线的map
     * @return java.util.Map<java.lang.String, V>
     */
    static <V> Map<String, V> mapKeyUnderlineToHump(Map<String, V> map) {

        if (map == null) {
            return null;
        }
        Map<String, V> newMap = new HashMap<>(map.size());
        for (Map.Entry<String, V> entry : map.entrySet()) {
            String key = entry.getKey();
            String newKey = toHump(key);
            newMap.put(newKey, entry.getValue());
        }
        return newMap;
    }

    /**
     * 将Map中的key由驼峰转换为下划线
     *
     * @param map 带有驼峰的map
     * @return java.util.Map<java.lang.String, V>
     */
    static <V> Map<String, V> mapKeyHumpToUnderline(Map<String, V> map) {

        if (map == null) {
            return null;
        }
        Map<String, V> newMap = new HashMap<>(map.size());
        for (Map.Entry<String, V> entry : map.entrySet()) {
            String key = entry.getKey();
            String newKey = toUnderline(key);
            newMap.put(newKey, entry.getValue());
        }
        return newMap;
    }

    /**
     * 下划线转驼峰
     *
     * @param colName 字符串
     * @return java.lang.String
     */
    static String toHump(String colName) {

        if (colName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] str = colName.toLowerCase().split(UNDERLINE);
        for (String s : str) {
            if (s.length() == 1) {
                sb.append(s.toUpperCase());
                continue;
            }
            if (s.length() > 1) {
                sb.append(s.substring(0, 1).toUpperCase());
                sb.append(s.substring(1));
            }
        }
        String result = sb.toString();
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    /**
     * 驼峰转下划线
     *
     * @param colName 字符串
     * @return java.lang.String
     */
    static String toUnderline(String colName) {

        if (colName == null) {
            return null;
        }
        String result = colName.replaceAll("[A-Z]", UNDERLINE + "$0");
        return result.toLowerCase();
    }

    /**
     * 将一个或多个字段转为MyBatis用的SQL插入语句, 其中下换线自动转为驼峰
     *
     * @param columns 英文逗号隔开的字段, 例: id, name, age, del_flag
     * @return java.lang.String SQL插入语句, 例: (#{id}, #{name}, #{age}, #{delFlag})
     */
    static String columnsToValueColumns(String columns) {

        return "(#{" + toHump(columns).replace(COMMA, "}, #{") + "})";
    }

    /**
     * 将一个或多个字段转为MyBatis用的SQL批量插入语句, 其中下换线自动转为驼峰
     *
     * @param columns 英文逗号隔开的字段, 例: id, del_flag
     * @param size    批量插入的条数
     * @param argName Dao接口方法上的参数名参数名, 默认为list
     * @return java.lang.String 例: (#{list[0].id}, #{list[0].delFlag}), (#{list[1].id}, #{list[1]
     * .delFlag})
     */
    static String columnsToValueColumnsBatch(String columns, int size, String argName) {

        if (argName == null) {
            argName = "list";
        }
        StringBuilder sb = new StringBuilder();
        MessageFormat mf =
            new MessageFormat("(#'{'" + argName + "[{0}]." + toHump(columns).replace("`", "")
                                                                            .replace(COMMA,
                                                                                     "}, #'{'" +
                                                                                     argName +
                                                                                     "[{0}].") +
                              "})");
        for (int i = 0; i < size; i++) {
            sb.append(mf.format(new Object[]{i})).append(COMMA);
        }
        return StringUtils.removeLastTwoChar(sb).toString();
    }

    /**
     * 将一个或多个字段转为MyBatis用的SQL批量插入语句, 其中下换线自动转为驼峰
     *
     * @param columns 英文逗号隔开的字段, 例: id, del_flag
     * @param size    批量插入的条数
     * @return java.lang.String
     */
    static String columnsToValueColumnsBatch(String columns, int size) {

        return columnsToValueColumnsBatch(columns, size, null);
    }

    /**
     * 获得MySQL的分页SQL
     *
     * @param p   参数， limit和offset必须有值
     * @param sql 未分页的SQL
     * @return java.lang.String
     */
    static String page(P p, String sql) {

        Integer limit = p.getLimit();
        Integer offset = p.getOffset();
        if (limit != null && offset != null && limit > 0 && offset >= 0) {
            sql += LIMIT + p.getOffset() + ", " + p.getLimit();
        }
        return sql;
    }

    /**
     * 查询条件今日条件
     *
     * @param column 字段名
     * @return java.lang.String
     */
    static String getTodaySql(String column) {

        return "to_char(" + column + ", 'yyyymmdd') = to_char(sysdate, 'yyyymmdd')";
    }

    /**
     * 在Where语句后面追加条件所用到的枚举
     *
     * @author paulandcode paulandcode@gmail.com
     * @since 19-6-21 上午11:04
     */
    enum AndEnum {
        // 追加LIKE语句
        LIKE,
        // 追加IN语句, 内容为字符串
        IN_IDS,
        // 追加IN语句为SQL语句
        IN_SQL,
        // 追加全等语句
        EQUAL,
        // 追加不等语句
        UNEQUAL,
        // 为空
        IS_NULL,
        // 不为空
        NOT_NULL,
    }

    /**
     * 在Where语句后面追加条件
     *
     * @param prefix 前缀
     * @param column 字段名
     * @param value  字段值
     * @param type   类型，
     *               IN_IDS：追加IN语句，value要传英文逗号隔开的ids
     *               IN_SQL：追加IN语句，value要传一个SQL语句
     *               LIKE：追加LIKE语句， value要传文本
     *               EQUAL：追加全等语句， value要传文本
     *               IS_NULL: 为空
     *               NOT_NULL: 不为空
     * @return java.lang.String
     */
    static String condition(String prefix, String column, AndEnum type, Object value) {

        column = addPrefix(prefix) + addDot(column);
        String condition = TRUE;
        switch (type) {
            case IN_IDS:
                if (!StringUtils.isEmpty(value)) {
                    condition = column + IN + SqlUtils.formatIds(value);
                }
                break;
            case IN_SQL:
                if (!StringUtils.isEmpty(value)) {
                    condition = column + IN + "(" + value.toString() + ")";
                }
                break;
            case LIKE:
                if (!StringUtils.isEmpty(value)) {
                    condition = column + LIKE + "'%" + value + "%'";
                }
                break;
            case EQUAL:
                if (!StringUtils.isEmpty(value)) {
                    condition = column + " = '" + value + "'";
                }
                break;
            case UNEQUAL:
                if (!StringUtils.isEmpty(value)) {
                    condition = column + " != '" + value + "'";
                }
                break;
            case IS_NULL:
                condition = column + " IS NULL";
                break;
            case NOT_NULL:
                condition = column + " IS NOT NULL";
                break;
            default:
        }
        return condition;
    }

    /**
     * where条件
     *
     * @param column 字段名
     * @param type   类型
     * @param value  值
     * @return java.lang.String
     */
    static String condition(String column, AndEnum type, Object value) {

        return condition(null, column, type, value);
    }

    /**
     * 某字段为空条件
     *
     * @param prefix 前缀
     * @param column 字段名
     * @return java.lang.String
     */
    static String isNull(String prefix, String column) {

        return condition(prefix, column, AndEnum.IS_NULL, null);
    }

    /**
     * 某字段为空条件
     *
     * @param column 字段
     * @return java.lang.String
     */
    static String isNull(String column) {

        return isNull(null, column);
    }

    /**
     * 某字段不为空条件
     *
     * @param prefix 前缀
     * @param column 字段名
     * @return java.lang.String
     */
    static String notNull(String prefix, String column) {

        return condition(prefix, column, AndEnum.NOT_NULL, null);
    }

    /**
     * 某字段不为空
     *
     * @param column 字段名
     * @return java.lang.String
     */
    static String notNull(String column) {

        return notNull(null, column);
    }

    /**
     * 某字段等于条件
     *
     * @param prefix 前缀
     * @param column 字段名
     * @param value  值
     * @return java.lang.String
     */
    static String equal(String prefix, String column, Object value) {

        return condition(prefix, column, AndEnum.EQUAL, value);
    }

    /**
     * 某字段等于条件
     *
     * @param column 字段名
     * @param value  值
     * @return java.lang.String
     */
    static String equal(String column, Object value) {

        return equal(null, column, value);
    }

    /**
     * 某字段不等条件
     *
     * @param prefix 前缀
     * @param column 字段名
     * @param value  值
     * @return java.lang.String
     */
    static String unequal(String prefix, String column, Object value) {

        return condition(prefix, column, AndEnum.UNEQUAL, value);
    }

    /**
     * 某字段不等条件
     *
     * @param column 字段名
     * @param value  值
     * @return java.lang.String
     */
    static String unequal(String column, Object value) {

        return unequal(null, column, value);
    }

    /**
     * ID值条件
     *
     * @param prefix 前缀
     * @param id     ID值
     * @return java.lang.String
     */
    static String equalId(String prefix, Object id) {

        return equal(prefix, "id", id);
    }

    /**
     * ID值条件
     *
     * @param id ID值
     * @return java.lang.String
     */
    static String equalId(Object id) {

        return equalId(null, id);
    }

    /**
     * ID值条件
     *
     * @param prefix 前缀
     * @param ids    ID值, 英文逗号隔开
     * @return java.lang.String
     */
    static String inIds(String prefix, String ids) {

        return condition(prefix, "id", AndEnum.IN_IDS, ids);
    }

    /**
     * ID值条件
     *
     * @param ids ID值, 英文逗号隔开
     * @return java.lang.String
     */
    static String inIds(String ids) {

        return inIds("id", ids);
    }

    /**
     * LIKE条件
     *
     * @param prefix 前缀
     * @param column 字段名
     * @param value  值
     * @return java.lang.String
     */
    static String like(String prefix, String column, Object value) {

        return condition(prefix, column, AndEnum.LIKE, value);
    }

    /**
     * LIKE条件
     *
     * @param column 字段名
     * @param value  值
     * @return java.lang.String
     */
    static String like(String column, Object value) {

        return like(null, column, value);
    }

    /**
     * 已删除条件
     *
     * @param prefix 前缀
     * @return java.lang.String
     */
    static String deleted(String prefix) {

        return equal(prefix, "del_flag", 1);
    }

    /**
     * 已删除条件
     *
     * @return java.lang.String
     */
    static String deleted() {

        return deleted(null);
    }

    /**
     * 未删除条件
     *
     * @param prefix 前缀
     * @return java.lang.String
     */
    static String notDeleted(String prefix) {

        return equal(prefix, "del_flag", 0);
    }

    /**
     * 已删除条件
     *
     * @return java.lang.String
     */
    static String notDeleted() {

        return notDeleted(null);
    }

    /**
     * 给字段两边增加点（`）
     *
     * @param columns 初始字段名
     * @return java.lang.String
     */
    static String addDot(String columns) {

        if (!columns.contains(DOT)) {
            String[] columnArr = columns.split(",");
            StringBuilder columnsBuilder = new StringBuilder();
            for (String column : columnArr) {
                columnsBuilder.append(DOT).append(column.trim()).append("`, ");
            }
            columns = StringUtils.removeLastTwoChar(columnsBuilder).toString();
        }
        return columns;
    }

    /**
     * 增加前缀
     *
     * @param prefix 前缀
     * @return java.lang.String
     */
    static String addPrefix(String prefix) {

        return StringUtils.isEmpty(prefix) ? "" : prefix + ".";
    }
}