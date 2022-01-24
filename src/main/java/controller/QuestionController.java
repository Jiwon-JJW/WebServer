package controller;

import annotation.Controller;
import annotation.GetMapping;
import annotation.PostMapping;

@Controller
public class QuestionController {
    @GetMapping(url = "/question")
    public String question() {
        return "질문 페이지";
    }

    @PostMapping(url = "/question")
    public String postQuestion() {
        return "질문 등록 완료";
    }
}
