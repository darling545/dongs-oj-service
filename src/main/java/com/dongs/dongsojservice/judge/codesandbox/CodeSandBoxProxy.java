package com.dongs.dongsojservice.judge.codesandbox;


import com.dongs.dongsojservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.dongs.dongsojservice.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理类
 *
 * @author dongs
 */
@Slf4j
public class CodeSandBoxProxy implements CodeSandbox{


    private final CodeSandbox codeSandbox;


    public CodeSandBoxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("准备开始判题{}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("判题结束,{}", executeCodeRequest);
        return executeCodeResponse;
    }
}
