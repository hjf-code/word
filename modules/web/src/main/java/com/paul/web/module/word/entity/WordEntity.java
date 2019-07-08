package com.paul.web.module.word.entity;

import com.paul.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 单词
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 19-6-27 下午2:01
 */
@Data
public class WordEntity extends BaseEntity {

    private static final long serialVersionUID = -568639849174256641L;

    /***
     * 单词
     */
    private String word;

    /**
     * 音标
     */
    private String sound;

    /**
     * 翻译
     */
    private String translation;

    /**
     * 改字段在数据库中永远为0, 不会更新, 只会在内存里的todayWords中更新
     * 进度: 0(未背诵), 1(已背诵1遍), 2(已背诵2遍), 3(已背诵3遍, 今日无需再背诵),
     * 4(不认识: 只要一次背诵时忘记, 则变为不认识状态)
     */
    private Integer schedule;

    /**
     * 计划开始时间, 初始为录入单词的第二天, 后续按照艾宾浩斯记忆曲线背诵单词: 0, 1, 2, 4, 7, 15, 31, 107.
     * 若某个单词没有在规定日期24点前背完, 则改为规定日期的第二天, 即单词进入记忆曲线的开始, 算作没背单词的惩罚
     */
    private Date startDate;

    @Override
    public String toString() {

        return "WordEntity{" + super.toString() +
               "word='" + word + '\'' +
               ", sound='" + sound + '\'' +
               ", translation='" + translation + '\'' +
               ", schedule='" + schedule + '\'' +
               '}';
    }
}