package com.dongs.dongsojservice.service.question.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongs.dongsojservice.constant.CommonConstant;
import com.dongs.dongsojservice.model.dto.questionrequest.QuestionQueryRequest;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.model.vo.question.QuestionVo;
import com.dongs.dongsojservice.mapper.question.QuestionMapper;
import com.dongs.dongsojservice.service.question.QuestionService;
import com.dongs.dongsojservice.service.user.UserService;
import com.dongs.dongsojservice.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author dongs
 * @description 针对表【question(题目信息表)】的数据库操作Service实现
 * @createDate 2024-07-03 10:39:59
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private UserService userService;



    /**
     * 获取题目查询条件
     *
     * @param questionQueryRequest 题目查询请求类
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String questionTitle = questionQueryRequest.getQuestionTitle();
        List<String> questionTags = questionQueryRequest.getQuestionTags();
        String questionLevel = questionQueryRequest.getQuestionLevel();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.like(ObjectUtils.isNotEmpty(questionTitle), "question_title", questionTitle);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionLevel), "question_level", questionLevel);
        // 根据标签进行模糊查询
        if (CollectionUtils.isNotEmpty(questionTags)) {
            for (String tag : questionTags) {
                queryWrapper.like("question_tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    /**
     * 对题目信息列表进行封装
     * @param questionPage 未封装的题目信息
     * @return 封装好的题目信息
     */
    @Override
    public Page<QuestionVo> getQuestionVoPage(Page<Question> questionPage) {
        List<Question> questionRecords = questionPage.getRecords();
        Page<QuestionVo> questionVoPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionRecords)) {
            return questionVoPage;
        }
        // 进行联合信息查询
        // 获取用户的ID（只需要保存唯一的用户ID即可）
        Set<Long> userIdSet = questionRecords.stream().map(Question::getUserId).collect(Collectors.toSet());
        // 获取用户信息
        Map<Long,List<User>> userIdListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        List<QuestionVo> questionVoList = questionRecords.stream().map(question -> {
            // 通过把question当中的user找出，赋值并转化成vo对象，赋值给QuestionVo
            QuestionVo questionVo = QuestionVo.objToVo(question);
            // 找出question中的userId
            Long userId = question.getUserId();
            User user = null;
            // 如果userId在题目Map中，就把对应的user信息赋值给user对象，用于给题目封装类进行赋值
            if (userIdListMap.containsKey(userId)){
                user = userIdListMap.get(userId).get(0);
            }
            questionVo.setUserVO(userService.getUserVo(user));
            questionVo.setJudgeConfig(null);
            return questionVo;
        }).collect(Collectors.toList());
        // 将封装好的信息填充到要返回的题目信息封装类中
        questionVoPage.setRecords(questionVoList);
        return questionVoPage;
    }
}




