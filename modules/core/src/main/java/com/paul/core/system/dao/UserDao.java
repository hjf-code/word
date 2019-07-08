package com.paul.core.system.dao;

import com.paul.common.base.BaseDao;
import com.paul.core.system.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import static com.paul.common.constant.SqlConstant.*;

/**
 * 系统用户DAO
 *
 * @author paul paul@gmail.com
 * @since 2019/3/24 13:39
 */
@Mapper
public interface UserDao extends BaseDao<UserEntity> {

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return com.paul.core.system.entity.UserEntity
     */
    @Select({
        SELECT,
        "`id`, `username`, `password`, `salt`, `locked`, `del_flag`",
        FROM,
        "core_sys_user",
        WHERE,
        "`username` = #{username}"
    })
    UserEntity getObjectByUsername(String username);

    class Provider extends BaseDao.Provider<UserEntity> {

        {
            insertBatchColumns = "`id`, `username`, `password`, `salt`";
            tableName = "core_sys_" + tableName;
        }
    }
}