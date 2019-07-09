package com.paul.common.base;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import static com.paul.common.constant.DateConstant.DEFAULT_DATE_PATTERN;

/**
 * 基础的实体类, 可序列化, 主键字段默认为id
 * 规定:
 * 1. 该类及其子类的所有属性(除了serialVersionUID属性)需要满足两个条件:
 * (1) 必须在数据库里有对应字段(符合驼峰转换), 否则使用Dao的时候要重写Dao中相应接口
 * (2) 不能有基本类型, 必须是包装类(这也是阿里Java规范), 否则会被插入/更新为默认值(int默认值为0, boolean默认值为false)
 * 2. 该类与其子类属性名不能重复(除了serialVersionUID属性)
 * 3. 实体类的类名命名规则为: 表名转大驼峰, 然后追加Entity, 例: 表名为test_user, 则实体类名为TestUserEntity
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/27 22:27
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -5532439383771563598L;

    /**
     * 主键, 使用UUID, 这里用Object类型, 这样可以支持Integer
     */
    protected Object id;

    /**
     * 备注
     */
    protected String remark;

    /**
     * 创建人ID, 若为系统创建, 则为system
     */
    protected String createBy;

    /**
     * 更新人ID, 若为系统更新, 则为system
     */
    protected String updateBy;

    /**
     * 创建时间, 默认当前时间
     */
    @JSONField(format = DEFAULT_DATE_PATTERN)
    protected Date createDate;

    /**
     * 更新时间, 根据当前时间自动更新
     */
    @JSONField(format = DEFAULT_DATE_PATTERN)
    protected Date updateDate;

    /**
     * 备用字段1
     */
    protected String column1;

    /**
     * 备用字段2
     */
    protected String column2;

    /**
     * 备用字段3
     */
    protected String column3;

    /**
     * 删除标志
     */
    protected Boolean delFlag;

    public String getId() {

        return id == null ? null : id.toString();
    }

    @Override
    public String toString() {

        return "id=" + id +
            ", remark='" + remark + '\'' +
            ", createBy='" + createBy + '\'' +
            ", updateBy='" + updateBy + '\'' +
            ", createDate=" + createDate +
            ", updateDate=" + updateDate +
            ", column1='" + column1 + '\'' +
            ", column2='" + column2 + '\'' +
            ", column3='" + column3 + '\'' +
            ", delFlag=" + delFlag + ", ";
    }
}