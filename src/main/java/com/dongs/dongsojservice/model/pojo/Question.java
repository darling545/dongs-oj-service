package com.dongs.dongsojservice.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目信息表
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * 题目ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目标题
     */
    @TableField(value = "question_title")
    private String questionTitle;

    /**
     * 题目内容
     */
    @TableField(value = "question_content")
    private String questionContent;

    /**
     * 创建者ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 题目标签列表(json数组)
     */
    @TableField(value = "question_tags")
    private String questionTags;

    /**
     * 题目答案
     */
    @TableField(value = "qusetion_answer")
    private String qusetionAnswer;

    /**
     * 提交数
     */
    @TableField(value = "submit_num")
    private Integer submitNum;

    /**
     * 通过数
     */
    @TableField(value = "accepted_num")
    private Integer acceptedNum;

    /**
     * 判题用例(json数组)
     */
    @TableField(value = "judge_case")
    private String judgeCase;

    /**
     * 判题配置(json数组)
     */
    @TableField(value = "judge_config")
    private String judgeConfig;

    /**
     * 点赞数
     */
    @TableField(value = "thumb_num")
    private Integer thumbNum;

    /**
     * 收藏数
     */
    @TableField(value = "favour_num")
    private Integer favourNum;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 题目等级(简单、中等、困难)
     */
    @TableField(value = "question_level")
    private String questionLevel;

    /**
     * 是否删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
