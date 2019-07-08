package com.paul.common.base;

import com.paul.common.util.P;
import com.paul.common.util.R;

import java.util.List;

/**
 * 基础接口
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 19-6-27 上午11:24
 */
public interface BaseService<E extends BaseEntity> {

    /**
     * 插入单个
     *
     * @param entity 插入的实体类
     * @return com.paul.common.util.R Web响应信息
     */
    R insert(E entity);

    /**
     * 批量插入
     *
     * @param list 插入的实体列表
     * @return com.paul.common.util.R Web响应信息
     */
    R insertBatch(List<E> list);

    /**
     * 根据主键删除单个
     *
     * @param id 主键, 一般为String或Integer
     * @return com.paul.common.util.R Web响应信息
     */
    R delete(Object id);

    /**
     * 根据主键删除多个
     *
     * @param ids 主键, 以英文逗号隔开
     * @return com.paul.common.util.R Web响应信息
     */
    R deleteBatch(String ids);

    /**
     * 根据主键更新单个, 主键必须传
     *
     * @param entity 更新的实体类
     * @return com.paul.common.util.R Web响应信息
     */
    R update(E entity);

    /**
     * 根据主键更新多个, 主键必须传
     *
     * @param entity 更新的实体类, 批量更新的公共内容
     * @param ids    批量更新的ID, 以英文逗号隔开
     * @return com.paul.common.util.R Web响应信息
     */
    R updateBatch(E entity, String ids);

    /**
     * 根据主键查询单个
     *
     * @param id 主键, 一般为String或Integer
     * @return com.paul.common.util.R Web响应信息
     */
    R getObject(Object id);

    /**
     * 根据主键查询多个
     *
     * @param ids 主键, 以英文逗号隔开
     * @return com.paul.common.util.R Web响应信息
     */
    R listObjects(String ids);

    /**
     * 根据参数查询列表
     *
     * @param p 多条件查询参数
     * @return com.paul.common.util.R Web响应信息
     */
    R listObjects(P p);
}