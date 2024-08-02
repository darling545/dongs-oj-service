package com.dongs.dongsojservice.judge.strategy;

import com.dongs.dongsojservice.judge.codesandbox.model.JudgeInfo;
import com.dongs.dongsojservice.model.pojo.QuestionSubmit;

/**
 * 判题管理
 *
 * @author dongs
 */
public class JudgeManager {


    /**
     * 策略设计模式
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
