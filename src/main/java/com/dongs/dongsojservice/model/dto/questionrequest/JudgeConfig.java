package com.dongs.dongsojservice.model.dto.questionrequest;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JudgeConfig {


    /**
     * 时间限制（ms）
     */
    private Long timeLimit;

    /**
     * 内存限制（kb）
     */
    private Long memoryLimit;

    /**
     * 堆栈限制（kb）
     */
    private Long stackLimit;
}
