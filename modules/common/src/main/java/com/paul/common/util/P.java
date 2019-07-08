package com.paul.common.util;

import com.paul.common.exception.CustomException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数, 除了继承自HashMap<String, Object>(方便编码), 还有分页参数(方便分页)
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/26 12:53
 */
@Getter
public class P extends HashMap<String, Object> {

    private static final long serialVersionUID = -8922845867719592408L;

    /**
     * MySQL分页参数, 与MySQL语法一致, 查询列表的起始行(第一行为0, 大于等于0), 包含该行
     * 默认为NULL, offset或limit只要有一个是NULL, 则不分页
     * 入口是page和limit, 出口是offset和limit
     */
    private Integer offset;

    /**
     * MySQL分页参数, 与MySQL语法一致, 查询列表的行数(从起始行开始往后数几行, 大于0)
     */
    private Integer limit;

    public P() {

    }

    public P(Integer page, Integer limit) {

        if (page == null || limit == null) {
            throwPageParamsException();
        } else if (page > 0 && limit > 0) {
            setPageAndLimit(page, limit);
        }
    }

    public P(String key, Object value) {

        super.put(key, value);
    }

    public P(Map<String, Object> map) {

        super.putAll(map);
    }

    /**
     * 移除指定参数.
     *
     * @param key 键
     * @return com.paul.common.util.P
     */
    public P remove(String key) {

        super.remove(key);
        return this;
    }

    /**
     * 响应中加入一个键值对
     * 这个方法不能放分页参数.
     *
     * @param key   键
     * @param value 值
     * @return com.paul.common.util.P
     */
    @Override
    public P put(String key, Object value) {

        super.put(key, value);
        return this;
    }

    /**
     * 响应中加入多个键值对
     * 这个方法不能放分页参数.
     *
     * @param params 参数
     * @return com.paul.common.util.P
     */
    public P putMap(Map<String, Object> params) {

        super.putAll(params);
        return this;
    }

    /**
     * 更改页码, 前提是之前指定过
     *
     * @param page 页码
     * @return com.paul.common.util.P
     */
    public P updatePage(Integer page) {

        if (limit == null || page == null || page < 1) {
            throwPageParamsException();
        }
        offset = (page - 1) * limit;
        return this;
    }

    /**
     * 更改每页条数, 前提是之前指定过
     *
     * @param limit 每页条数
     * @return com.paul.common.util.P
     */
    public P updateLimit(Integer limit) {

        if (offset == null || limit == null || limit < 1) {
            throwPageParamsException();
        }
        int page = offset == 0 ? 1 : (offset / limit + 1);
        this.limit = limit;
        offset = (page - 1) * limit;
        return this;
    }

    /**
     * 设置分页参数
     *
     * @param page  页码
     * @param limit 每页条数
     */
    private void setPageAndLimit(Integer page, Integer limit) {

        if (page == null || limit == null || page < 1 || limit < 1) {
            throwPageParamsException();
        }
        offset = (page - 1) * limit;
        this.limit = limit;
    }

    /**
     * 抛出异常提示分页参数有误
     */
    private void throwPageParamsException() {

        throw new CustomException("分页参数必须大于0! ");
    }
}