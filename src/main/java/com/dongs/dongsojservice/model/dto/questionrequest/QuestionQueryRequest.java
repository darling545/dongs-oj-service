package com.dongs.dongsojservice.model.dto.questionrequest;


import com.dongs.dongsojservice.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 题目查询请求类
 *
 * @author dongs
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目标题
     */
    private String questionTitle;


    /**
     * 题目标签列表(json数组)
     */
    private List<String> questionTags;


    /**
     * 提交数
     */
    private Integer submitNum;

    /**
     * 通过数
     */
    private Integer acceptedNum;


    /**
     * 题目等级(简单、中等、困难)
     */
    private String questionLevel;


    private static final long serialVersionUID = 1L;
}
