package com.dongs.dongsojservice.judge.codesandbox;


import com.dongs.dongsojservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.dongs.dongsojservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 静态工厂类
 */
public class CodeSandBoxFactory {


    public static CodeSandbox newInstance(String type){
        if ("remote".equals(type)) {
            return new RemoteCodeSandbox();
        } else if ("thirdParty".equals(type)) {
            return new ThirdPartyCodeSandbox();
        } else {
            return new RemoteCodeSandbox();
        }
    }
}
