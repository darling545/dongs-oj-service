package com.dongs.dongsojservice.model.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserRoleEnum {

    ROLE_USER("普通用户","user"),
    ROLE_ADMIN("管理员","admin"),
    ROLE_BAN("封号","ban");


    private String text;

    private String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     *
     * 获取value值列表
     * @return
     */
    public static List<String> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }


    /**
     * 根据value值获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value){
        if (ObjectUtils.isEmpty(value)){
            return null;
        }

        for (UserRoleEnum anEnum : UserRoleEnum.values()){
            if (anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

}
