package com.example.quizService.features.quiz.service;

import com.example.quizService.features.quiz.dto.FinalExamDTO;
import com.example.quizService.features.quiz.dto.QuizQuestionDTO;
import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GetFinalExamUseCase - Generate final exam
 * 
 * Responsibility:
 * - Fetch questions from all 5 subjects
 * - Distribute questions evenly across subjects
 * - Ensure minimum questions per subject
 * - Shuffle questions for randomness
 * - Return with exam duration
 * 
 * Business Rules:
 * - Total questions: 70
 * - Duration: 50 minutes
 * - Subjects: 1, 2, 3, 4, 5 (all subjects)
 * - Distribution: 14 questions per subject (70 / 5 = 14)
 * - If a subject has < 14 questions, throw error (insufficient questions)
 * - Questions shuffled within each subject, then shuffled together
 * 
 * Dependencies:
 * - QuestionRepository - fetch questions by subject
 * 
 * Flow:
 * 1. Define subjects and limits (14 per subject)
 * 2. For each subject:
 * - Fetch all questions
 * - Validate count (must have >= 14)
 * - Shuffle questions
 * - Select 14 questions
 * - Convert to DTO
 * 3. Shuffle all selected questions together
 * 4. Wrap in FinalExamDTO with duration
 * 5. Return final exam
 * 
 * Used by:
 * - QuizController (user endpoint)
 * - ExamService via service-to-service call
 */
@Service
public class GetFinalExamUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetFinalExamUseCase.class);

    private final QuestionRepository questionRepository;

    // Constants
    private static final int TOTAL_QUESTIONS = 70;
    private static final int DURATION_MINUTES = 50;
    private static final int[] SUBJECT_IDS = { 1, 2, 3, 4, 5 };
    private static final int QUESTIONS_PER_SUBJECT = TOTAL_QUESTIONS / SUBJECT_IDS.length; // 14

    public GetFinalExamUseCase(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Execute: Generate final exam
     * 
     * @return Final exam with 70 questions and 50-minute duration
     * @throws RuntimeException if any subject has insufficient questions
     */
    public FinalExamDTO execute() {
        log.info("Generating final exam: {} questions, {} min", TOTAL_QUESTIONS, DURATION_MINUTES);

        List<QuizQuestionDTO> result = new ArrayList<>();

        // Step 1: Fetch questions from all subjects
        for (int subjectId : SUBJECT_IDS) {
            log.debug("Processing subject={}", subjectId);

            // Fetch all questions for this subject
            List<Question> questionsForSubject = questionRepository.findBySubject(subjectId);

            // Step 2: Validate count
            if (questionsForSubject.size() < QUESTIONS_PER_SUBJECT) {
                String errorMsg = "Subject " + subjectId + " has only " + questionsForSubject.size()
                        + " questions but requires at least " + QUESTIONS_PER_SUBJECT;
                log.error("Insufficient questions: {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // Step 3: Shuffle and select
            Collections.shuffle(questionsForSubject);
            List<Question> selected = questionsForSubject.stream()
                    .limit(QUESTIONS_PER_SUBJECT)
                    .toList();

            log.debug("Selected {} questions from subject={}", selected.size(), subjectId);

            // Step 4: Convert to DTO
            for (Question q : selected) {
                result.add(mapToDTO(q));
            }
        }

        // Step 5: Final shuffle (mix all subjects together)
        Collections.shuffle(result);

        log.debug("Final exam generated: {} questions", result.size());

        // Step 6: Wrap in DTO with duration
        return new FinalExamDTO(result, DURATION_MINUTES);
    }

    /**
     * Map Question entity to QuizQuestionDTO
     * - Shuffles answers (4 total)
     * - Tracks correct answer index
     * - Image field passed through as-is (can be filename OR URL)
     */
    private QuizQuestionDTO mapToDTO(Question q) {
        // Build list of all 4 answers
        List<String> answers = new ArrayList<>();
        answers.add(q.getCorrectAnswer());
        answers.add(q.getWrongAnswer1());
        answers.add(q.getWrongAnswer2());
        answers.add(q.getWrongAnswer3());

        // Shuffle answers
        Collections.shuffle(answers);

        // Find index of correct answer after shuffle
        int correctIndex = answers.indexOf(q.getCorrectAnswer());

        return new QuizQuestionDTO(
                q.getId(),
                q.getQuestion(),
                q.getSfi(),
                answers,
                correctIndex,
                q.getImage(), // Can be filename OR URL - frontend handles both
                q.getExplanationForStudent());
    }
}
