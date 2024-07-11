package com.dongs.dongsojservice.model.vo.question;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dongs.dongsojservice.model.dto.questionrequest.JudgeCase;
import com.dongs.dongsojservice.model.dto.questionrequest.JudgeConfig;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.vo.user.UserVo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 题目返回封装类
 *
 * @author dongs
 */
@Data
public class QuestionVo implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建题目人的信息
     */
    private UserVo userVO;

    /**
     * 题目答案
     */
    private String answer;


    /**
     * 包装类转对象
     * @param questionVo
     * @return
     */
    public static Question voToObj(QuestionVo questionVo){
        if (questionVo == null){
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVo,question);
        List<String> tagList = questionVo.getTags();
        if (tagList != null){
            question.setQuestionTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig judgeConfig = questionVo.getJudgeConfig();
        if (judgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        return question;
    }


    /**
     * 对象转包装类
     * @param question
     * @return
     */
    public static QuestionVo objToVo(Question question){
        if (question == null){
            return null;
        }
        QuestionVo questionVo = new QuestionVo();
        BeanUtils.copyProperties(question,questionVo);
        String tags = question.getQuestionTags();
        String judgeConfig = question.getJudgeConfig();
        String judgeCase = question.getJudgeCase();
        if (StrUtil.isNotBlank(tags)){
            questionVo.setTags(JSONUtil.toList(tags,String.class));
        }
        if (StrUtil.isNotBlank(judgeConfig)){
            questionVo.setJudgeConfig(JSONUtil.toBean(judgeConfig,JudgeConfig.class));
        }
        if (StrUtil.isNotBlank(judgeCase)){
            questionVo.setJudgeCase(JSONUtil.toList(judgeCase, JudgeCase.class));
        }
        return questionVo;
    }



    private static final long serialVersionUID = -2275702502899164647L;
}
