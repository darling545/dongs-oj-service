package com.dongs.dongsojservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.dongs.dongsojservice.config.JsonConfig;
import com.dongs.dongsojservice.judge.codesandbox.model.JudgeInfo;
import com.dongs.dongsojservice.model.dto.questionrequest.JudgeCase;
import com.dongs.dongsojservice.model.dto.questionrequest.JudgeConfig;
import com.dongs.dongsojservice.model.enums.JudgeInfoMessageEnum;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.pojo.QuestionSubmit;

import java.util.List;
import java.util.Optional;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy{

    /**
     * 默认判题策略
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCase();
        Question question = judgeContext.getQuestion();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L) / 1024;
        Long time = judgeInfo.getTime();
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);
        // 判断代码沙箱中返回的数据是否和预期的输出用例个数相同
        if (outputList.size() != inputList.size()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 判断输出用例和测试用例是否相同
        for (int i = 0; i < judgeCaseList.size(); i++){
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 判题限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (time > needTimeLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
