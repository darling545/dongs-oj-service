package com.dongs.dongsojservice.judge.codesandbox.impl;


import com.dongs.dongsojservice.judge.codesandbox.CodeSandbox;
import com.dongs.dongsojservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.dongs.dongsojservice.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用第三方【其他人】开发的接口）
 *
 * @author dongs
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    /**
     * @param executeCodeRequest 执行代码的请求类
     * @return 返回执行代码的响应值
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
