package com.paul.common.base;

import com.paul.common.util.IDUtils;
import com.paul.common.util.P;
import com.paul.common.util.R;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 基础增删改查, 在Service子类上, 要加@Service注解
 * 这里的接口只返回数据, 没有用com.paul.common.R包装, 方便不同Service实现类之间相互调用
 * 根据需要, Service实现类新加的方法也可以直接返回com.paul.common.R类型
 * 某类型的Service实现类最好只调用对应类型的Dao实现类, 不要调用其他类型的Dao实现类
 * 若需要查询其他类型的数据, 可以通过这个类型的Service实现类来调用
 *
 * @author paul paul@gmail.com
 * @since 2019/3/24 13:50
 */
public abstract class BaseServiceImpl<E extends BaseEntity, D extends BaseDao<E>> {

    @Autowired
    protected D dao;

    /**
     * 插入单个
     *
     * @param entity 插入的实体类
     * @return com.paul.common.util.R Web响应信息
     */
    public R insert(E entity) {
        // 插入前, 后台通过UUID生成主键
        // 若主键为自增, 则不需要UUID, 不推荐自增
        entity.setId(IDUtils.getId());
        dao.insert(entity);
        return R.ok();
    }

    /**
     * 批量插入
     *
     * @param list 插入的实体列表
     * @return com.paul.common.util.R Web响应信息
     */
    public R insertBatch(List<E> list) {

        for (E entity : list) {
            // 插入前, 后台通过UUID生成主键
            // 若主键为自增, 则不需要UUID, 不推荐自增
            entity.setId(IDUtils.getId());
        }
        dao.insertBatch(list);
        return R.ok();
    }

    /**
     * 根据主键删除单个
     *
     * @param id 主键, 一般为String或Integer
     * @return com.paul.common.util.R Web响应信息
     */
    public R delete(Object id) {

        dao.delete(id);
        return R.ok();
    }

    /**
     * 根据主键删除多个
     *
     * @param ids 主键, 以英文逗号隔开
     * @return com.paul.common.util.R Web响应信息
     */
    public R deleteBatch(String ids) {

        dao.deleteBatch(ids);
        return R.ok();
    }

    /**
     * 根据主键更新单个, 主键必须传
     *
     * @param entity 更新的实体类
     * @return com.paul.common.util.R Web响应信息
     */
    public R update(E entity) {

        dao.update(entity);
        return R.ok();
    }

    /**
     * 根据主键更新多个, 主键必须传
     *
     * @param entity 更新的实体类, 批量更新的公共内容
     * @param ids    批量更新的id, 以英文逗号隔开
     * @return com.paul.common.util.R Web响应信息
     */
    public R updateBatch(E entity, String ids) {

        dao.updateBatch(entity, ids);
        return R.ok();
    }

    /**
     * 根据主键查询单个
     *
     * @param id 主键, 一般为String或Integer
     * @return com.paul.common.util.R Web响应信息
     */
    public R getObject(Object id) {

        return R.ok(dao.getObject(id));
    }

    /**
     * 根据主键查询多个
     *
     * @param ids 主键, 以英文逗号隔开
     * @return com.paul.common.util.R Web响应信息
     */
    public R listObjects(String ids) {

        return R.ok(dao.listObjectsByIds(ids));
    }

    /**
     * 根据参数查询列表
     *
     * @param p 多条件查询参数
     * @return com.paul.common.util.R Web响应信息
     */
    public R listObjects(P p) {

        return R.ok(dao.listObjects(p));
    }
}