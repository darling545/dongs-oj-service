package com.dongs.dongsojservice.model.vo;


import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;

import java.util.Date;

/**
 * 用户脱敏信息
 */
@Data
public class LoginUserVo {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户权限
     */
    private String userRole;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户等级
     */
    private String userLevel;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;


}
