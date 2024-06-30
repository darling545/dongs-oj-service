package com.dongs.dongsojservice.service.user.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.exception.BusinessException;
import com.dongs.dongsojservice.model.dto.userrequest.UserQueryRequest;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.model.vo.LoginUserVo;
import com.dongs.dongsojservice.model.vo.UserVo;
import com.dongs.dongsojservice.service.user.UserService;
import com.dongs.dongsojservice.mapper.user.UserMapper;
import com.dongs.dongsojservice.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dongs.dongsojservice.constant.CommonConstant.SORT_ORDER_ASC;
import static com.dongs.dongsojservice.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 1
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-06-17 14:55:49
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final String SALT = "dongs";

    @Resource
    private UserMapper userMapper;



    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword) {
        // 1、校验
        if (StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名或密码为空！");
        }
        // 1.1 校验用户名和密码的长度
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户名错误！");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码错误！");
        }
        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3、对比
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        queryWrapper.eq("user_password",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 查看是否用户存在
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名或者密码不存在");
        }
        StpUtil.login(user.getId());
        // 记录用户登录状态
        StpUtil.getSession().set(USER_LOGIN_STATE,user);
        // 5、返回信息
        return this.getLoginUserVo(user);
    }

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkUserPassword) {
        // 注册的账户名是否已经存在与数据库中
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkUserPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户过短");
        }
        if (userPassword.length() < 8 || checkUserPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (!userPassword.equals(checkUserPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户是否重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复,请更换其他账户名!");
            }
            // 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 注册入数据库
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败,数据库错误!");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVo getLoginUserVo(User user) {
        LoginUserVo loginUserVo = new LoginUserVo();
        if (user == null){
            return null;
        }
        BeanUtils.copyProperties(user,loginUserVo);
        // 设置token返回给前端
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        loginUserVo.setToken(saTokenInfo.getTokenValue());
        return loginUserVo;
    }

    /**
     * 用户注销
     * @return
     */
    @Override
    public Boolean userLogout() {
        // 判断用户是否登陆中
        if(StpUtil.getLoginIdDefaultNull() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户尚未登陆");
        }
        StpUtil.logout();
        // 移除登录态
        return true;
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    @Override
    public User getLoginUser() {
        try{
            // 判断是否登录
            Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
            User currentUser = (User) userObj;
            if (currentUser == null || currentUser.getId() == null){
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"未获取到用户信息");
            }
            return currentUser;
        }catch (NotLoginException e){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,ErrorCode.NOT_LOGIN_ERROR.getMessage());
        }
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }

        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public UserVo getUserVo(User user) {
        if (user == null) {
            return null;
        }
        UserVo userVO = new UserVo();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVo> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVo).collect(Collectors.toList());
    }


}




