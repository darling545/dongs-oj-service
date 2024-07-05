package com.dongs.dongsojservice.service.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.service.question.QuestionService;
import com.dongs.dongsojservice.mapper.question.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author dongs
* @description 针对表【question(题目信息表)】的数据库操作Service实现
* @createDate 2024-07-03 10:39:59
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{



}




