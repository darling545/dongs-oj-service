package com.dongs.dongsojservice.judge.codesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行代码提交请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {

    /**
     * 输入用例
     */
    private List<String> inputList;

    /**
     * 用户提交代码
     */
    private String code;

    /**
     * 用户选择提交的语言
     */
    private String language;

}
