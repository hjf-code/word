package com.paul.web.module.word.dao;

import com.paul.common.base.BaseDao;
import com.paul.common.util.P;
import com.paul.web.module.word.entity.WordEntity;
import com.paul.web.module.word.entity.WordEntityExtend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

import static com.paul.common.constant.SqlConstant.*;
import static com.paul.common.util.SqlUtils.notDeleted;

/**
 * 单词
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/28 20:04
 */
@Mapper
public interface WordDao extends com.paul.common.base.BaseDao<WordEntity> {

    /**
     * 统计进度
     *
     * @param p 参数: date:日期
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Integer>>
     */
    @Select({
        SELECT + "`schedule`, count(`schedule`) count",
        FROM + "`word`",
        WHERE + "`del_flag` = 0",
        AND +
            "timestampdiff(day, date_format(`start_date`, '%Y-%m-%d'), #{date}) in (0, 1, 2, 4, 7, " +
            "15, 31, 107)",
        GROUP_BY + "`schedule`"
    })
    List<Map<String, Object>> count(P p);

    /**
     * 查询拓展的列表
     *
     * @param p 单词
     * @return java.util.List<com.paul.web.module.word.entity.WordEntityExtend>
     */
    @SelectProvider(type = BaseDao.Provider.class, method = "listObjectsExtend")
    List<WordEntityExtend> listObjectsExtend(P p);

    @Select({
        SELECT,
        "`id`",
        FROM,
        "`word`",
        WHERE,
        "`del_flag` = 0 AND ",
        "`word` = #{word}"
    })
    WordEntity getObjectByWord(String word);

    class Provider extends com.paul.common.base.BaseDao.Provider<WordEntity> {

        public String listObjectsExtend(P p) {

            return new SQL() {{

                SELECT("`word`, " +
                    " `sound`, " +
                    " `translation`, " +
                    " `schedule`, " +
                    " `start_date`, " +
                    " `id`, " +
                    " `remark`" +
                    ", timestampdiff(day, date_format(`start_date`, '%Y-%m-%d'), " +
                    "#{date}) AS dayCount");
                FROM(tableName);
                WHERE(notDeleted());
                WHERE(
                    "timestampdiff(day, date_format(`start_date`, '%Y-%m-%d'), #{date}) in (0, 1," +
                        " 2, 4, 7, 15, 31, 107)");
                ORDER_BY("`start_date` DESC");
            }}.toString();
        }
    }
}