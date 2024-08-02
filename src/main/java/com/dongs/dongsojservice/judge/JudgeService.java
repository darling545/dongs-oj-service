package com.dongs.dongsojservice.judge;


import com.dongs.dongsojservice.model.pojo.QuestionSubmit;

/**
 * 判题服务接口
 *
 * @author dongs
 */
public interface JudgeService {


    /**
     * 判题
     *
     * @param questionSubmitId 提交题目的id
     * @return 暂时返回脱敏题目提交信息
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
