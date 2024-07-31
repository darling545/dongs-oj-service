package com.dongs.dongsojservice.model.dto.questionrequest;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionAddRequest implements Serializable {


    /**
     * 标题
     */
    private String questionTitle;

    /**
     * 内容
     */
    private String questionContent;

    /**
     * 标签列表
     */
    private List<String> questionTags;

    /**
     * 题目等级(简单、中等、困难)
     */
    private String questionLevel;


    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 题目答案
     */
    private String questionAnswer;


    private static final long serialVersionUID = 6634100952360452047L;
}
