package com.dongs.dongsojservice.judge.strategy;




import com.dongs.dongsojservice.judge.codesandbox.model.JudgeInfo;
import com.dongs.dongsojservice.model.dto.questionrequest.JudgeCase;
import com.dongs.dongsojservice.model.pojo.Question;
import com.dongs.dongsojservice.model.pojo.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 判题上下文类（用于在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCase;

    private Question question;

    private QuestionSubmit questionSubmit;

}
