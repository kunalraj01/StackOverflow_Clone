package com.stackoverflow.clone.controller;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.service.QuestionService;
import com.stackoverflow.clone.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/")
public class QuestionController {

    private QuestionService questionService;

    private TagService tagService;

    public QuestionController(QuestionService questionService,TagService tagService) {
        this.questionService=questionService;
        this.tagService=tagService;
    }

    @GetMapping("/")
    public String home(Model model){
        List<Question> questions = questionService.findAll();
        model.addAttribute("questions",questions);
        return "Home";
    }

    @GetMapping("/new-question")
    public String newQuestion(Model model){
        Question question = new Question();
        model.addAttribute("question",question);
        return "question/new-question";
    }

    @PostMapping("/save")
    public String saveQuestion(@ModelAttribute("question") Question question,
                               @RequestParam(value = "tagNames", required = false) String tagNames) {
        if (question.getId()!=null){
            question.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        Set<Tag> tags = tagService.findOrCreateTag(tagNames);
        question.setTags(tags);
        questionService.save(question);
        return "redirect:/question/"+question.getId();
    }

    @GetMapping("/question/{questionId}")
    public String viewQuestion(@PathVariable("questionId") Long questionId,
                               Model model){
        Question question = questionService.findById(questionId);
        model.addAttribute("question",question);
        return "question/view-question";
    }

    @PostMapping("/question/editQuestion/{questionId}")
    public String editPost(@PathVariable("questionId") Long questionId,
                           Model model) {
        Question question = questionService.findById(questionId);
        Set<Tag> tags = question.getTags();
        StringBuilder tagNamesBuilder = new StringBuilder();
        for (Tag tag : tags) {
            tagNamesBuilder.append(tag.getName()).append(" ");
        }
        String tagNames = tagNamesBuilder.toString().trim();
        model.addAttribute("question", question);
        model.addAttribute("tagNames", tagNames);
        return "question/edit-question";
    }

    @PostMapping("question/delete/{deleteId}")
    public String delete(@PathVariable("deleteId") Long deleteId) {
        questionService.deleteById(deleteId);
        return "redirect:/";
    }

}
