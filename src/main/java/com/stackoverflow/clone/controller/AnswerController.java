package com.stackoverflow.clone.controller;

import com.stackoverflow.clone.entity.Answer;
import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.service.AnswerService;
import com.stackoverflow.clone.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/answer")
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    public AnswerController(QuestionService questionService, AnswerService answerService){
        this.questionService = questionService;
        this.answerService = answerService;
    }
    @PostMapping("/save")
    public String saveAnswer(@RequestParam("questionId") Long questionId,
                             @RequestParam(value = "id", required = false) Long id,
                             @RequestParam("theAnswer") String theAnswer,
                             @ModelAttribute("answer") Answer answer){

        Question question = questionService.findById(questionId);
        if (id != null) {
            answer = answerService.findById(id);
            answer.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            answer.setTheAnswer(theAnswer);
        }
        answer.setQuestion(question);
        answerService.save(answer);
        return "redirect:/question/"+questionId;
    }

    @PostMapping("/updateAnswer")
    public String updateAnswer(@RequestParam("answerId") Long answerId,
                               Model model){
        Answer tempAnswer = answerService.findById(answerId);

        model.addAttribute("question", tempAnswer.getQuestion());
        model.addAttribute("tempAnswer", tempAnswer);
        return "answer/edit-answer";
    }

    @PostMapping("/delete")
    public String deleteAnswer(@RequestParam("questionId") Long questionId,
                               @RequestParam("answerId") Long answerId){
        answerService.deleteById(answerId);
        return "redirect:/question/"+questionId;
    }
}
