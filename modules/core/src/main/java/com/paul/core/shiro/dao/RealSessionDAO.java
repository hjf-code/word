package com.paul.core.shiro.dao;

import com.paul.core.shiro.entity.SessionEntity;
import org.apache.ibatis.annotations.*;

import static com.paul.common.constant.SqlConstant.*;

/**
 * 真正Shiro会话的DAO, 这是个不继承BaseDao的例子
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/21 22:32
 */
@Mapper
public interface RealSessionDAO {
        String TABLE_NAME = "core_shiro_session";
        String SELECT_COLUMNS = "`id`, `session`";

        /**
         * 增
         *
         * @param entity 实体类
         */
        @Insert({
                INSERT_INTO,
                TABLE_NAME,
                VALUES,
                "(#{id}, #{session})"
        })
        void insert(SessionEntity entity);

        /**
         * 删
         *
         * @param id 主键
         */
        @Delete({
                DELETE_FROM,
                TABLE_NAME,
                WHERE,
                "`id` = #{id}"
        })
        void deleteById(String id);

        /**
         * 改
         *
         * @param entity 实体类
         */
        @Update({
                UPDATE,
                TABLE_NAME,
                SET,
                "`session` = #{session}",
                WHERE,
                "`id` = #{id}"
        })
        void updateById(SessionEntity entity);

        /**
         * 查
         *
         * @param id 主键
         * @return com.paul.core.shiro.entity.SessionEntity
         */
        @Select({
                SELECT,
                SELECT_COLUMNS,
                FROM,
                TABLE_NAME,
                WHERE,
                "`id` = #{id}"
        })
        SessionEntity selectById(String id);
}