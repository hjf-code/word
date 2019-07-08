package com.paul.web.module.word.controller;

import com.paul.common.base.BaseController;
import com.paul.common.util.R;
import com.paul.web.module.word.entity.WordEntity;
import com.paul.web.module.word.service.WordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 单词
 *
 * @author paul paul@gmail.com
 * @since 2019/3/28 19:59
 */
@RestController
@RequestMapping("word")
public class WordController extends BaseController<WordEntity, WordService> {

    /**
     * 获得下一个单词
     *
     * @param know 1: 认识, 0: 不认识, 2: 初始加载
     * @return com.paul.common.util.R
     */
    @GetMapping("next/{know}")
    public R next(@PathVariable int know) {

        return service.next(know);
    }
}