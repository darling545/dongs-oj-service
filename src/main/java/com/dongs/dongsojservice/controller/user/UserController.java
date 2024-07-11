package com.dongs.dongsojservice.controller.user;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongs.dongsojservice.annotation.AuthCheck;
import com.dongs.dongsojservice.common.BaseResponse;
import com.dongs.dongsojservice.common.DeleteRequest;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.common.ResultUtils;
import com.dongs.dongsojservice.exception.BusinessException;
import com.dongs.dongsojservice.exception.ThrowUtils;
import com.dongs.dongsojservice.model.dto.userrequest.UserLoginRequest;
import com.dongs.dongsojservice.model.dto.userrequest.UserQueryRequest;
import com.dongs.dongsojservice.model.dto.userrequest.UserRegisterRequest;
import com.dongs.dongsojservice.model.dto.userrequest.UserUpdateRequest;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.model.vo.LoginUserVo;
import com.dongs.dongsojservice.model.vo.user.UserVo;
import com.dongs.dongsojservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.dongs.dongsojservice.constant.UserConstant.ADMIN_ROLE;

/**
 * 用户接口
 * @author Dongs
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;


    // begin 用户登录、注册、注销

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest){
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码信息为空！！！");
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAllBlank(userAccount,userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码信息为空！！！");
        }

        LoginUserVo loginUserVo = userService.userLogin(userAccount,userPassword);
        return ResultUtils.success(loginUserVo);

    }


    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册信息不能为空");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkUserPassword = userRegisterRequest.getCheckUserPassword();

        if (StringUtils.isAllBlank(userAccount,userPassword,checkUserPassword)){
            return null;
        }
        long result = userService.userRegister(userAccount,userPassword,checkUserPassword);
        return ResultUtils.success(result);
    }


    /**
     * 用户注销
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(){
        boolean result = userService.userLogout();
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登陆用户信息
     * @return
     */
    @GetMapping("get/login")
    public BaseResponse<LoginUserVo> getLoginUser(){
        User user = userService.getLoginUser();
        return ResultUtils.success(userService.getLoginUserVo(user));
    }

    // end 用户登录、注册、注销


    // begin 用户管理（增删改查）
    @PostMapping("/list/page")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Page<User>> selectUserInfoByPage(@RequestBody UserQueryRequest userQueryRequest){
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current,size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }


    /**
     * 分页获取用户封装列表
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVo>> selectUserVoByPage(@RequestBody UserQueryRequest userQueryRequest){
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空!");
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20,ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current,size),userService.getQueryWrapper(
                userQueryRequest));
        Page<UserVo> userVoPage = new Page<>(current,size,userPage.getTotal());
        List<UserVo> userVoList = userService.getUserVO(userPage.getRecords());
        userVoPage.setRecords(userVoList);
        return ResultUtils.success(userVoPage);
    }


    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest){
        if (deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空!");
        }
        boolean flag = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(flag);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserById(@RequestBody UserUpdateRequest userUpdateRequest){
        if (userUpdateRequest == null || userUpdateRequest.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        log.info("修改开始");
        boolean flag = userService.updateById(user);
        ThrowUtils.throwIf(!flag,ErrorCode.OPERATION_ERROR);
        log.info("修改结束");
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVo> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVo(user));
    }



    // end 用户管理（增删改查）

    /**
     * 修改用户个人信息
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update/own")
    public BaseResponse<Boolean> updateUserByIdOwner(@RequestBody UserUpdateRequest userUpdateRequest){
        if (userUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser();
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        user.setId(loginUser.getId());
        boolean flag = userService.updateById(user);
        ThrowUtils.throwIf(!flag,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
