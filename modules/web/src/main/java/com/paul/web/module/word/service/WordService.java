package com.paul.web.module.word.service;

import com.paul.common.base.BaseService;
import com.paul.common.util.R;
import com.paul.web.module.word.entity.WordEntity;
import com.paul.web.module.word.entity.WordEntityExtend;

import java.util.concurrent.BlockingQueue;

/**
 * 单词
 *
 * @author paul paulandcode@gmail.com
 * @since 2019/3/28 20:05
 */
public interface WordService extends BaseService<WordEntity> {

    /**
     * 获得下一个单词
     *
     * @param know 1: 认识, 0: 不认识, 2: 初始加载
     * @return com.paul.common.util.R
     */
    R next(int know);

    /**
     * 获得昨日没背完的单词
     *
     * @return java.util.concurrent.BlockingQueue<com.paul.web.module.word.entity.WordEntityExtend>
     */
    BlockingQueue<WordEntityExtend> getTodayWords();

    /**
     * 初始化今日单词
     */
    void init();
}