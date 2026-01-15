

package com.example.quizService.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quizService.Entity.ExcelImportFile;
import com.example.quizService.Entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  // Hämta alla frågor med visst ämne (subject som int)
  List<Question> findBySubject(int subject);
  
  // Hämta alla frågor med visst språk (lang som String)
  List<Question> findByLang(String lang); 

  // Hämta alla frågor med viss excelId
  List<Question> findByExcelId (Integer excelId);

  List<Question> findByExcelImportFile(ExcelImportFile excelImportFile);


  boolean existsByQuestionAndCorrectAnswerAndSubjectAndLang(
        String question, String correctAnswer, int subject, String lang
        );
  
}