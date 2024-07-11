package com.dongs.dongsojservice.service.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongs.dongsojservice.model.dto.questionrequest.QuestionQueryRequest;
import com.dongs.dongsojservice.model.pojo.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dongs.dongsojservice.model.vo.question.QuestionVo;

/**
* @author dongs
* @description 针对表【question(题目信息表)】的数据库操作Service
* @createDate 2024-07-03 10:39:59
*/
public interface QuestionService extends IService<Question> {


    /**
     * 获取查询条件
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);


    /**
     * 分页获取题目封装
     * @param questionPage 未封装的题目信息
     * @return
     */
    Page<QuestionVo> getQuestionVoPage(Page<Question> questionPage);
}
