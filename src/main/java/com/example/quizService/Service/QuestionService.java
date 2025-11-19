package com.example.quizService.Service;

import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Entity.Question;
import com.example.quizService.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService implements QuestionServiceInterface {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    @Override
    public List<QuizQuestionDTO> getQuestionsBySubject(int subject, int limit) {

        // Hämta alla frågor från subject
        List<Question> all = questionRepository.findBySubject(subject);
        
        // Kontrollera om listan är tom
        if (all.isEmpty()) return Collections.emptyList();
        
        // Slumpa ordningen på frågorna
        Collections.shuffle(all);

        // Om användaren begär mer frågor än det som finns -> ge alla
        int actualLimit = Math.min(limit, all.size()); 

        // Plocka ut exakt så många som ska användas
        List<Question> selected = all.stream()
            .limit(actualLimit)
            .collect(Collectors.toList()); 
        
        // Bygg DTO objekten
        List<QuizQuestionDTO> result = new ArrayList<>();
        
        // Loopar igenom varje fråga som valts ut från databasen 
        for (Question q : selected){

            // Skapa en ny lista som ska innehålla alla svarsalternativen.
            List<String> answers = new ArrayList<>(); 

            // Lägg till det rätta svaret först
            answers.add(q.getCorrectAnswer());
            
            // Lägg till de tre felaktiga svarsalternativen
            answers.add(q.getWrongAnswer1()); 
            answers.add(q.getWrongAnswer2()); 
            answers.add(q.getWrongAnswer3());
            
            // Blanda ordning på svaren svaren
            Collections.shuffle(answers);

            // Hitta vilket index som är rätt svar 
            int correctIndex = answers.indexOf(q.getCorrectAnswer());
            
            // Skapa ett nytt DTO-objekt som ska skickas till frontend
            // Innehåller frågans id, text, svarsalternativ, rätt svar-index, bild och förklaring
            QuizQuestionDTO dto = new QuizQuestionDTO(
                q.getId(), 
                q.getQuestion(), 
                answers, 
                correctIndex, 
                q.getImage(),
                q.getExplanationForStudent()
            );

            // Lägg till färdiga frågeobjektet i listan som ska retuneras
            result.add(dto); 
        }

        return result; 
    }

    @Override
    public List<Question> getQuestionsByLang(String lang) {
        return questionRepository.findByLang(lang);
    }

    @Override
    public Question getQuestionById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.orElse(null);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    
}
