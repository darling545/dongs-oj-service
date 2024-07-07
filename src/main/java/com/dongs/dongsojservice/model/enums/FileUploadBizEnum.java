package com.dongs.dongsojservice.model.enums;


import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务枚举类
 *
 * @author dongs
 */
public enum FileUploadBizEnum {


    USER_AVATAR("用户头像","user_avatar");


    private String text;

    private String value;

    FileUploadBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 获取值列表
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
    public static FileUploadBizEnum getEnumByValue(String value){
        if (ObjectUtils.isEmpty(value)){
            return null;
        }
        for (FileUploadBizEnum fileUploadBizEnum : FileUploadBizEnum.values()){
            if (fileUploadBizEnum.value.equals(value)){
                return fileUploadBizEnum;
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
