package com.paul.web.module.word.schedule;

import com.paul.web.module.word.dao.WordDao;
import com.paul.web.module.word.entity.WordEntity;
import com.paul.web.module.word.entity.WordEntityExtend;
import com.paul.web.module.word.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.BlockingQueue;

/**
 * 单词定时任务
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 19-7-6 上午1:39
 */
@Configuration
@EnableScheduling
@Component
public class WordScheduleJobs {

    private final WordService wordService;

    private final WordDao wordDao;

    @Autowired
    public WordScheduleJobs(WordService wordService, WordDao wordDao) {

        this.wordService = wordService;
        this.wordDao = wordDao;
    }

    @Scheduled(cron = "${spring.schedule.corn.word-check}")
    public void wordCheck() {

        Date startDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 1);
        startDate = calendar.getTime();

        BlockingQueue<WordEntityExtend> todayWords = wordService.getTodayWords();
        if (todayWords != null) {
            // 获取并移除队列的头, 若队列为空, 则返回null
            WordEntityExtend todayWord = todayWords.poll();
            while (todayWord != null) {
                WordEntity word = new WordEntity();
                word.setId(todayWord.getId());
                word.setStartDate(startDate);
                word.setSchedule(0);
                wordDao.update(word);
                todayWord = todayWords.poll();
            }
        }

        wordService.init();
    }
}