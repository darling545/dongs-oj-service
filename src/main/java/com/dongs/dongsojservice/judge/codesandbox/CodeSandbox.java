package com.dongs.dongsojservice.judge.codesandbox;


import com.dongs.dongsojservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.dongs.dongsojservice.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 *
 * @author dongs
 */
public interface CodeSandbox {


    /**
     * 执行代码
     * @param executeCodeRequest 执行代码的请求类
     * @return 返回响应类
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}
