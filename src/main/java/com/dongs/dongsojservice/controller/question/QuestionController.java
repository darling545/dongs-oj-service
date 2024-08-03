package com.dongs.dongsojservice.controller.question;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongs.dongsojservice.annotation.AuthCheck;
import com.dongs.dongsojservice.common.BaseResponse;
import com.dongs.dongsojservice.common.DeleteRequest;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.common.ResultUtils;
import com.dongs.dongsojservice.exception.BusinessException;
import com.dongs.dongsojservice.exception.ThrowUtils;
import com.dongs.dongsojservice.manager.RedisLimiterManager;
import com.dongs.dongsojservice.model.dto.questionrequest.*;
import com.dongs.dongsojservice.model.enums.QuestionSubmitLanguageEnum;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.model.vo.question.QuestionVo;
import com.dongs.dongsojservice.service.question.QuestionService;
import com.dongs.dongsojservice.service.question.QuestionSubmitService;
import com.dongs.dongsojservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dongs.dongsojservice.constant.RedisConstant.CACHE_NULL_TTL;
import static com.dongs.dongsojservice.constant.RedisConstant.CACHE_QUESTION_TTL;
import static com.dongs.dongsojservice.constant.RedisKeyConstant.*;
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


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private QuestionSubmitService questionSubmitService;




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


    /**
     * 删除题目根据id
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionById(@RequestBody DeleteRequest deleteRequest){
        if (deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        User user = userService.getLoginUser();

        long questionId = deleteRequest.getId();
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null,ErrorCode.NOT_FOUND_ERROR);

        if (!question.getUserId().equals(user.getId()) && userService.isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean del = questionService.removeById(questionId);
        return ResultUtils.success(del);
    }


    /**
     * 根据id获取题目信息（脱敏）
     * @param id 题目id
     * @return 返回题目脱敏信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVo> getQuestionVoById(long id){
        // 获取当前登录用户
        User loginUser = userService.getLoginUser();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"题目参数错误");
        }
        // 设置缓存键
        String key = CACHE_QUESTION_KEY + id;
        // 查询redis中是否有当前缓存
        String questionJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(questionJson)){
            // 缓存命中，直接返回
            Question question = JSONUtil.toBean(questionJson,Question.class);
            return ResultUtils.success(questionService.getQuestionVO(question,loginUser));
        }
        Question question = questionService.getById(id);
        if (question == null){
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL + RandomUtil.randomLong(1,10),TimeUnit.MINUTES);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }else {
            // 存在就加入缓存
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(question),CACHE_QUESTION_TTL + RandomUtil.randomLong(1,3),TimeUnit.MINUTES);
        }
        return ResultUtils.success(questionService.getQuestionVO(question,loginUser));
    }

    // end 题目管理（删改查）

    // begin 题目管理（新增）TODO 新增题目时测试用例自动生成但是修改的时候不会显示全部测试用例（只显示前5个），可以通过按钮进行调节是否修改

    /**
     * 创建题目
     * @param questionAddRequest 新增题目请求类
     * @return 新增题目后的id值
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest){
        if (questionAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }

        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest,question);
        List<String> tags = questionAddRequest.getQuestionTags();
        if (tags != null){
            question.setQuestionTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCases = questionAddRequest.getJudgeCase();
        if (judgeCases != null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        questionService.validQuestion(question,true);
        User loginUser = userService.getLoginUser();
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        long saveQuestionId = question.getId();
        // 如果新增了题目，就重新刷新缓存，清楚CACHE_KEY_PREFIX为前缀的键
        stringRedisTemplate.delete(CACHE_KEY_PREFIX);
        return ResultUtils.success(saveQuestionId);
    }

    // end 题目管理（新增）


    // begin 题目语言

    /**
     * 返回对应的语言的枚举值给前端
     * @return 对应的语言枚举值
     */
    @GetMapping("/languages")
    public BaseResponse<List<String>> getAllLanguages(){
        List<String> statusMap = new ArrayList<>();
        for (QuestionSubmitLanguageEnum questionSubmitLanguageEnum : QuestionSubmitLanguageEnum.values()){
            statusMap.add(questionSubmitLanguageEnum.getValue());
        }
        return ResultUtils.success(statusMap);
    }
    // end 题目语言

    // begin 提交题目

    /**
     * 提交题目
     * @param questionSubmitAddRequest
     * @return
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest){
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 只有已登录用户才可以提交
        final User user = userService.getLoginUser();
        // 设置限流
        boolean rateLimit = redisLimiterManager.doRateLimit(REDIS_LIMIT_KEY_PREFIX + user.getId().toString());
        if (!rateLimit){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"提交过于频繁");
        }else {
            log.info("提交成功,题号：{},用户：{}", questionSubmitAddRequest.getQuestionId(), user.getId());
            Long result = questionSubmitService.doSubmit(questionSubmitAddRequest,user);
            stringRedisTemplate.delete(CACHE_KEY_PREFIX);
            return ResultUtils.success(result);
        }

    }
    // end 提交题目
}
