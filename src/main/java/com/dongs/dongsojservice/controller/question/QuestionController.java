package com.dongs.dongsojservice.controller.question;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongs.dongsojservice.annotation.AuthCheck;
import com.dongs.dongsojservice.common.BaseResponse;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.common.ResultUtils;
import com.dongs.dongsojservice.exception.ThrowUtils;
import com.dongs.dongsojservice.model.dto.questionrequest.QuestionQueryRequest;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.vo.question.QuestionVo;
import com.dongs.dongsojservice.service.question.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.dongs.dongsojservice.constant.UserConstant.ADMIN_ROLE;

/**
 * 题目接口管理
 *
 * @author dongs
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    // 缓存键的前缀，用于区分不同分页数据的缓存
    private static final String CACHE_KEY_PREFIX = "dataPage:";


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private QuestionService questionService;




    // begin 题目管理（删改查）

    /**
     * 分页获取题目信息（仅管理员可用）
     * @param questionQueryRequest 题目查询请求类
     * @return 题目分页数据
     */
    @RequestMapping("list/page")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Page<Question>> selectQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest){
        // 当前初始页
        long current = questionQueryRequest.getCurrent();
        // 单页数量大小
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current,size), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目封装信息
     * @param questionQueryRequest 题目查询请求类
     * @return 当前页的封装题目信息
     */
    @RequestMapping("list/page/vo")
    public BaseResponse<Page<QuestionVo>> selectQuestionVoByPage(@RequestBody QuestionQueryRequest questionQueryRequest){
        // 当前初始页（当前页）
        long current = questionQueryRequest.getCurrent();
        // 单页数量大小
        long size = questionQueryRequest.getPageSize();
        // 构造缓存键，格式为"dataPage:页码_每页大小"
        String cacheKey = CACHE_KEY_PREFIX + current + "_" + size;
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取缓存key的数据值
        String pageJson = stringRedisTemplate.opsForValue().get(cacheKey);
        // 初始化接收的值
        Page<Question> questionPage = null;
        Page<QuestionVo> questionVoPage = null;
        if (StrUtil.isBlank(pageJson)){
            // 1. 从数据库中取出对应的值
            questionPage = questionService.page(new Page<>(current,size),questionService.getQueryWrapper(questionQueryRequest));
            // 2. 赋值给包装类
            questionVoPage = questionService.getQuestionVoPage(questionPage);
            // 3. 缓存到redis中，并设置一定的过期时间
            stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(questionVoPage),1800, TimeUnit.SECONDS);
        }else {
            // 如果缓存中存在要查询的数据，直接走缓存中取数据
            log.info("加载缓存");
            questionVoPage = JSONUtil.toBean(pageJson, new TypeReference<Page<QuestionVo>>() {
            },true);
        }
        return ResultUtils.success(questionVoPage);
    }



    // end 题目管理（删改查）

    // begin 题目管理（新增）TODO 新增题目时测试用例自动生成但是修改的时候不会显示全部测试用例（只显示前5个），可以通过按钮进行调节是否修改


    // end 题目管理（新增）
}
