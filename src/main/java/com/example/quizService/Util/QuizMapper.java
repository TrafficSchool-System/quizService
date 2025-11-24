package com.example.quizService.Util;

import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Entity.Question;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizMapper {

    public static QuizQuestionDTO toDTO(Question q) {
        List<String> answers = new ArrayList<>();
        answers.add(q.getCorrectAnswer());
        answers.add(q.getWrongAnswer1());
        answers.add(q.getWrongAnswer2());
        answers.add(q.getWrongAnswer3());
        Collections.shuffle(answers);

        int correctIndex = answers.indexOf(q.getCorrectAnswer());

        return new QuizQuestionDTO(
                q.getId(),
                q.getQuestion(),
                q.getSfi(),
                answers,
                correctIndex,
                q.getImage(),
                q.getExplanationForStudent()
        );
    }
}
