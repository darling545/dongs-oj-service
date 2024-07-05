package com.dongs.dongsojservice.controller.question;

import com.dongs.dongsojservice.common.BaseResponse;
import com.dongs.dongsojservice.service.question.QuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * 题目管理
 *
 * @author dongs
 */
@RestController
@RequestMapping("/question")
public class QuestionController {


    @Resource
    private QuestionService questionService;


    // begin 题目管理（增删改）

    public BaseResponse<Long>


    // end 题目管理（增删改）

    // begin 题目管理（新增）TODO 新增题目时测试用例自动生成但是修改的时候不会显示全部测试用例（只显示前5个），可以通过按钮进行调节是否修改


    // end 题目管理（新增）
}
