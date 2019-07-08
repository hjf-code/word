package com.paul.common.base;

import com.paul.common.util.P;
import com.paul.common.util.R;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 基础增删改查, 遵循REST风格, 即: PUT<==>改, POST<==>增, GET<==>查, DELETE<==>删, 并且在路径上传ID
 * 在Controller子类上要加@RestController注解
 * 方法上的@RequestMapping在多个Controller上重复, 所以Controller子类上的@RequestMapping必须有且不能重复
 * 注意: 下面用到@RequestBody的地方用到ajax时需要传的data和contentType格式如下:
 * data: JSON.stringify({name: "张三", age: 12}),
 * contentType: "application/json; charset=utf-8",
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/27 17:23
 */
public abstract class BaseController<E extends BaseEntity, S extends BaseService<E>> {

    @Autowired
    protected S service;

    /**
     * 插入单个
     *
     * @param entity 实体类
     * @return com.paul.common.util.R
     */
    @PostMapping("one")
    public R insert(@RequestBody E entity) {

        return service.insert(entity);
    }

    /**
     * 批量插入
     *
     * @param list 实体列表
     * @return com.paul.common.util.R
     */
    @PostMapping("list")
    public R insertBatch(@RequestBody List<E> list) {

        return service.insertBatch(list);
    }

    /**
     * 根据主键删除单个, 逻辑删除
     *
     * @param id 主键, 一般为String或Integer
     * @return com.paul.common.util.R
     */
    @DeleteMapping("one/{id}")
    public R delete(@PathVariable Object id) {

        return service.delete(id);
    }

    /**
     * 根据主键删除多个, 逻辑删除
     *
     * @param ids 主键列表, 一般为String或Integer
     * @return com.paul.common.util.R
     */
    @DeleteMapping("list/{ids}")
    public R deleteBatch(@PathVariable String ids) {

        return service.deleteBatch(ids);
    }

    /**
     * 根据主键更新单个, 主键必须传
     *
     * @param entity 更新的实体类
     * @return com.paul.common.util.R
     */
    @PutMapping("one")
    public R update(@RequestBody E entity) {

        return service.update(entity);
    }

    /**
     * 根据主键更新多个, 主键必须传
     *
     * @param updateData 更新数据, 包括:
     *                   entity 更新的实体类, 批量更新的公共内容
     *                   ids 批量更新的主键集合
     * @return com.paul.common.util.R
     */
    @PutMapping("list")
    public R updateBatch(@RequestBody UpdateBatchData<E> updateData) {

        return service.updateBatch(updateData.getEntity(), updateData.getIds());
    }

    /**
     * 根据主键查询单个
     *
     * @param id 主键, 一般为String或Integer
     * @return com.paul.common.util.R
     */
    @GetMapping("one/{id}")
    public R getObject(@PathVariable Object id) {

        return service.getObject(id);
    }

    /**
     * 根据主键查询多个
     * 注意: 由于这里是GET请求, 所以不能用@RequestBody注解接收
     * GET请求拼接的URL最多为1024字节, 每个ID长度32字节的话, 可以有32个(不考虑其他字符), 满足参数只有多个ID的情况
     *
     * @param ids 主键, 以英文逗号隔开
     * @return com.paul.common.util.R
     */
    @GetMapping("list/{ids}")
    public R listObjects(@PathVariable String ids) {

        return service.listObjects(ids);
    }

    /**
     * 根据参数查询列表, 默认为分页查询, 且没有其他查询条件, 若要加条件, 可在URL上追加
     *
     * @param page   页码, 若不分页, 则传0或负数
     * @param limit  每页条数, 则传0或负数
     * @param params 参数
     * @return com.paul.common.util.R
     */
    @GetMapping("list/{page}/{limit}")
    public R listObjects(@PathVariable Integer page, @PathVariable Integer limit,
        @RequestParam Map<String, Object> params) {

        return service.listObjects(new P(page, limit).putMap(params));
    }
}

/**
 * 批量更新的实体类, 解决@RequestBody只能传一个对象的问题
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/27 17:23
 */
@Data
class UpdateBatchData<E> {

    /**
     * 批量更新的实体类
     */
    private E entity;

    /**
     * 批量更新的实体类ID, 以英文逗号隔开
     */
    private String ids;
}