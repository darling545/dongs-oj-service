package com.dongs.dongsojservice.judge.codesandbox.model;

import lombok.Data;

/**
 * 判题信息
 */
//TODO 判题信息配制 1.17.25
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 执行内存(KB)
     */
    private Long memory;

    /**
     * 消耗时间(ms)
     */
    private Long time;
}
