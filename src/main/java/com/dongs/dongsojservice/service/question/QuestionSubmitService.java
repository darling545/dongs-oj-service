package com.dongs.dongsojservice.service.question;

import com.dongs.dongsojservice.model.dto.questionrequest.QuestionSubmitAddRequest;
import com.dongs.dongsojservice.model.pojo.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dongs.dongsojservice.model.pojo.User;

/**
* @author 1
* @description 针对表【question_submit(题目提交表)】的数据库操作Service
* @createDate 2024-08-02 17:18:47
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {


    /**
     * 提交代码
     * @param questionSubmitAddRequest
     * @param loginuser
     * @return
     */
    Long doSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginuser);

}
