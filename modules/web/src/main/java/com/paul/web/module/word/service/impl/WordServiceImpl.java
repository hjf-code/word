package com.paul.web.module.word.service.impl;

import com.paul.common.base.BaseServiceImpl;
import com.paul.common.util.DateUtils;
import com.paul.common.util.P;
import com.paul.common.util.R;
import com.paul.common.util.StringUtils;
import com.paul.web.module.word.dao.WordDao;
import com.paul.web.module.word.entity.WordEntity;
import com.paul.web.module.word.entity.WordEntityExtend;
import com.paul.web.module.word.service.WordService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.paul.common.constant.DateConstant.DAY_DATE_PATTERN;

/**
 * 单词
 *
 * @author paul paul@gmail.com
 * @since 2019/3/28 20:05
 */
@Service("wordService")
public class WordServiceImpl extends BaseServiceImpl<WordEntity, WordDao> implements WordService {

    /**
     * 下一个要背的单词
     */
    private WordEntityExtend nextWord;

    /**
     * 今日单词
     */
    private BlockingQueue<WordEntityExtend> todayWords;

    /**
     * 是否已完成今日任务
     */
    private boolean finished;

    /**
     * 进度: 0(未背诵), 1(已背诵1遍), 2(已背诵2遍), 3(已背诵3遍, 今日无需再背诵), 4(不认识: 只要一次背诵时忘记, 则变为不认识状态)
     */
    private int[] schedules = new int[]{0, 0, 0, 0, 0};

    @Override
    public R insert(WordEntity entity) {

        WordEntity word = dao.getObjectByWord(entity.getWord());
        if (word != null) {
            return R.err("数据库已有该单词！");
        }
        Date startDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 1);
        startDate = calendar.getTime();
        entity.setStartDate(startDate);
        return super.insert(entity);
    }

    @Override
    public R delete(Object id) {

        WordEntityExtend word = todayWords.poll();
        assert word != null;
        Integer schedule = word.getSchedule();
        // 当前进度减一
        schedules[schedule]--;
        if (todayWords.size() == 0) {
            // 完成并重置, 但保留进度
            finished = true;
            todayWords = null;
            nextWord = null;
        } else {
            nextWord = todayWords.peek();
        }

        int totalCount = 0;
        for (int oneSchedule : schedules) {
            totalCount += oneSchedule;
        }

        return super.delete(id).put("schedules", schedules).put("nextWord", nextWord)
                    .put("totalCount", totalCount);
    }

    @Override
    public R update(WordEntity entity) {

        String oldWord = dao.getObject(entity.getId()).getWord();
        String newWord = entity.getWord();
        if (!Objects.equals(oldWord, newWord)) {
            WordEntity word = dao.getObjectByWord(entity.getWord());
            if (word != null) {
                return R.err("数据库已有该单词！");
            }
        }

        // 获取并移除队列的头, 若队列为空, 则返回null
        WordEntityExtend todayWord = todayWords.poll();
        assert todayWord != null;

        String sound = entity.getSound();
        String translation = entity.getTranslation();
        String remark = entity.getRemark();

        if (!StringUtils.isEmpty(newWord)) {
            todayWord.setWord(newWord);
        }
        if (!StringUtils.isEmpty(sound)) {
            todayWord.setSound(sound);
        }
        if (!StringUtils.isEmpty(translation)) {
            todayWord.setTranslation(translation);
        }
        if (!StringUtils.isEmpty(remark)) {
            todayWord.setRemark(remark);
        }

        todayWords.offer(todayWord);
        // 获取但不移除队列的头, 若队列为空, 则返回null
        nextWord = todayWords.peek();

        int totalCount = 0;
        for (int oneSchedule : schedules) {
            totalCount += oneSchedule;
        }
        return super.update(entity).put("schedules", schedules).put("nextWord", nextWord)
                    .put("totalCount", totalCount);
    }

    @Override
    public R next(int know) {

        if (!finished) {
            if (todayWords == null) {
                // 若今日单词为空, 则需要初始化今日单词, 并返回级别和下一个背诵单词
                // 查询今日单词, 以系统时间为准, 不以数据库时间为准
                P p = new P("date", DateUtils.format(new Date(), DAY_DATE_PATTERN));
                List<WordEntityExtend> words = dao.listObjectsExtend(p);
                if (words.size() == 0) {
                    // 今日没有单词, 直接完成
                    finished = true;
                } else {
                    todayWords = new ArrayBlockingQueue<>(words.size());
                    for (WordEntityExtend word : words) {
                        todayWords.offer(word);
                    }
                    // 获取但不移除队列的头, 若队列为空, 则返回null
                    nextWord = todayWords.peek();
                    List<Map<String, Object>> counts = dao.count(p);
                    for (Map<String, Object> count : counts) {
                        // 若有进度, 则改变数量, 没有则还是默认的0
                        schedules[Integer.parseInt(count.get("schedule").toString())] =
                            Integer.parseInt(count.get("count").toString());
                    }
                }
            } else {
                // 若已初始化今日单词, 则需返回级别和下一个背诵单词
                // 2或其他代表初始加载
                if (know == 0) {
                    // 获取并移除队列的头, 若队列为空, 则返回null
                    WordEntityExtend word = todayWords.poll();
                    assert word != null;
                    Integer schedule = word.getSchedule();
                    // 当前进度减一
                    schedules[schedule]--;
                    // 不认识进度加一
                    schedules[4]++;
                    word.setSchedule(4);
                    // 插入队列的最后
                    todayWords.offer(word);
                    // 获取但不移除下一个单词
                    nextWord = todayWords.peek();
                } else if (know == 1) {
                    // 获取并移除队列的头, 若队列为空, 则返回null
                    WordEntityExtend word = todayWords.poll();
                    assert word != null;
                    Integer schedule = word.getSchedule();
                    // 当前进度减一
                    schedules[schedule]--;
                    switch (schedule) {
                        case 0:
                        case 1:
                            // 下一个进度加一
                            schedules[schedule + 1]++;
                            // 修改进度为下一进度
                            word.setSchedule(schedule + 1);
                            // 插入队列的最后
                            todayWords.offer(word);
                            // 获取但不移除下一个单词
                            nextWord = todayWords.peek();
                            break;
                        case 2:
                            // 下一个进度加一
                            schedules[schedule + 1]++;
                            if (todayWords.size() == 0) {
                                // 完成并重置, 但保留进度
                                finished = true;
                                todayWords = null;
                                nextWord = null;
                            } else {
                                // 获取但不移除下一个单词
                                nextWord = todayWords.peek();
                            }
                            break;
                        case 4:
                            // 已背诵1遍进度加一
                            schedules[1]++;
                            word.setSchedule(1);
                            // 插入队列的最后
                            todayWords.offer(word);
                            // 获取但不移除下一个单词
                            nextWord = todayWords.peek();
                            break;
                        default:
                    }
                }
            }
        }
        int totalCount = 0;
        for (int schedule : schedules) {
            totalCount += schedule;
        }

        return R.ok().put("schedules", schedules).put("nextWord", nextWord)
                .put("totalCount", totalCount);
    }

    @Override
    public BlockingQueue<WordEntityExtend> getTodayWords() {

        return todayWords;
    }

    @Override
    public void init() {

        todayWords = null;
        nextWord = null;
        finished = false;
        schedules = new int[]{0, 0, 0, 0, 0};
    }
}