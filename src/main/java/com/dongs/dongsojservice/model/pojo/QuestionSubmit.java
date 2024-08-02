package com.dongs.dongsojservice.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目提交表
 * @TableName question_submit
 */
@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * 提交题目ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编程语言
     */
    @TableField(value = "language")
    private String language;

    /**
     * 用户提交代码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 判题信息(json对象)
     */
    @TableField(value = "judge_info")
    private String judgeInfo;

    /**
     * 判题状态(0-待判题 1-判题中 2-成功 3-失败)
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 题目id
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 创建用户id
     */
    @TableField(value = "user_id")
    private Long userId;

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
     * 是否删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
