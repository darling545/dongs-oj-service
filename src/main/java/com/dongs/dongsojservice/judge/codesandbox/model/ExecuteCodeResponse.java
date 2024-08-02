package com.dongs.dongsojservice.judge.codesandbox.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 代码沙箱返回的执行信息响应体
 * @author dongs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 返回执行用例
     */
    private List<String> outputList;

    /**
     * 代码沙箱的状态信息（程序之外的信息）
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 程序执行信息（程序内部信息）
     */
    private JudgeInfo judgeInfo;
}
