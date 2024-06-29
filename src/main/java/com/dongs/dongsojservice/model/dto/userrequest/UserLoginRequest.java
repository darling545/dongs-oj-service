package com.dongs.dongsojservice.model.dto.userrequest;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户登陆请求
 * @author Dongs
 */
@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;


    private String userPassword;

    private static final long serialVersionUID = 3191241716373120793L;

}
