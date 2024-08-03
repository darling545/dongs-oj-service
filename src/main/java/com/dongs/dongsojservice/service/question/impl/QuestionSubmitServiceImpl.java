package com.dongs.dongsojservice.service.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.exception.BusinessException;
import com.dongs.dongsojservice.model.dto.questionrequest.QuestionSubmitAddRequest;
import com.dongs.dongsojservice.model.enums.QuestionSubmitLanguageEnum;
import com.dongs.dongsojservice.model.enums.QuestionSubmitStatusEnum;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.pojo.QuestionSubmit;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.rabbitmq.MyMessageProducer;
import com.dongs.dongsojservice.service.question.QuestionService;
import com.dongs.dongsojservice.service.question.QuestionSubmitService;
import com.dongs.dongsojservice.mapper.question.QuestionSubmitMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 1
* @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
* @createDate 2024-08-02 17:18:47
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{


    @Resource
    private QuestionService questionService;

    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 提交代码
     * @param questionSubmitAddRequest
     * @param loginuser
     * @return
     */
    @Override
    public Long doSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginuser) {
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编程语言错误");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交
        long userId = loginuser.getId();
        // 每个用户串行提交
        // 锁必须要包裹住事务方法
//        QuestionSubmitService questionSubmitService = (QuestionSubmitService) AopContext.currentProxy();
//        synchronized (String.valueOf(userId).intern()) {
//            return questionSubmitService.doQuestionSubmitInner(userId, questionId);
//        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        // todo 使用枚举值进行代替僵硬的的确定值 √
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        myMessageProducer.sendMessage("code_exchange","my_routingKey",String.valueOf(questionSubmitId));
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        Integer submitNum = question.getSubmitNum();
        submitNum=submitNum+1;
        question.setSubmitNum(submitNum);
        questionService.updateById(question);
        return questionSubmitId;
    }
}




