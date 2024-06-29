package com.dongs.dongsojservice.service.user;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dongs.dongsojservice.model.dto.userrequest.UserQueryRequest;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.model.vo.LoginUserVo;
import com.dongs.dongsojservice.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 1
* @description 针对表【user】的数据库操作Service
* @createDate 2024-06-17 14:55:49
*/
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request request
     * @return 用户脱敏信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkUserPassword 确认密码
     * @return 成功的条数
     */
    Long userRegister(String userAccount,String userPassword,String checkUserPassword);


    /**
     * 获取登录用户的脱敏信息
     * @param user 用户全部信息
     * @return 用户脱敏的信息
     */
    LoginUserVo getLoginUserVo(User user);

    /**
     * 用户注销
     * @param request 获取用户登录态
     * @return 是否成功
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    UserVo getUserVo(User user);


    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVo> getUserVO(List<User> userList);
}
