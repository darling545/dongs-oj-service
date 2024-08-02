package com.dongs.dongsojservice.judge.strategy;

import com.dongs.dongsojservice.judge.codesandbox.model.JudgeInfo;

/**
 * 判题策略接口定义
 *
 * @author dongs
 */
public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
