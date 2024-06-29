package com.dongs.dongsojservice.model.dto.userrequest;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {


    private String userAccount;

    private String userPassword;

    private String checkUserPassword;
}
