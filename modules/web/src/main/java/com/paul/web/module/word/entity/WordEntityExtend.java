package com.paul.web.module.word.entity;

import lombok.Data;

/**
 * 扩展
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 19-7-5 下午5:56
 */
@Data
public class WordEntityExtend extends WordEntity{

    private static final long serialVersionUID = -8749230700069427434L;

    /**
     * 距离开始背诵时间间隔
     */
    private Integer dayCount;
}