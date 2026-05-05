package com.example.quizService.features.quiz.service;

import com.example.quizService.features.quiz.dto.QuizQuestionDTO;
import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GetQuestionsBySubjectsUseCase - Get quiz questions by subjects
 * 
 * Responsibility:
 * - Fetch questions for specified subjects
 * - Distribute questions evenly across subjects
 * - Shuffle questions for randomness
 * - Convert to DTO (hide correct answer, shuffle answer order)
 * 
 * Business Rules:
 * - Questions distributed evenly across subjects
 * - Remainder questions distributed to first subjects
 * - Questions shuffled twice: within subject, then total
 * - Empty subjects are skipped (no error thrown)
 * 
 * Dependencies:
 * - QuestionRepository - fetch questions by subject
 * 
 * Flow:
 * 1. Calculate questions per subject (even distribution)
 * 2. For each subject:
 * - Fetch all questions for that subject
 * - Shuffle questions
 * - Select required number
 * - Convert to DTO
 * 3. Shuffle all selected questions together
 * 4. Return quiz questions
 * 
 * Example:
 * - subjects=[1, 2, 3], limit=10
 * - perSubject=3, remainder=1
 * - Subject 1: 4 questions (3+1)
 * - Subject 2: 3 questions
 * - Subject 3: 3 questions
 * - Total: 10 questions
 */
@Service
public class GetQuestionsBySubjectsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetQuestionsBySubjectsUseCase.class);

    private final QuestionRepository questionRepository;

    public GetQuestionsBySubjectsUseCase(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Execute: Get quiz questions by subjects
     * 
     * @param subjects List of subject IDs to include
     * @param limit    Total number of questions to return
     * @return List of quiz questions (DTO format)
     */
    public List<QuizQuestionDTO> execute(List<Integer> subjects, int limit) {
        log.debug("Quiz session subjects={} limit={}", subjects, limit);

        if (subjects == null || subjects.isEmpty())
            return Collections.emptyList();

        // Step 1: Load a shuffled question pool for each subject that actually has
        // data.
        // Only subjects WITH data count toward distribution — empty subjects never
        // waste quota.
        List<List<Question>> pools = new ArrayList<>();
        for (int subjectId : subjects) {
            List<Question> pool = new ArrayList<>(questionRepository.findBySubject(subjectId));
            if (!pool.isEmpty()) {
                Collections.shuffle(pool);
                pools.add(pool);
                log.debug("Subject={}: {} questions available", subjectId, pool.size());
            } else {
                log.debug("Subject={}: no questions, skipped", subjectId);
            }
        }

        if (pools.isEmpty())
            return Collections.emptyList();

        // Step 2: Round-robin pick from pools until the limit is reached or all pools
        // are exhausted.
        // This guarantees even distribution AND automatically fills unused quota when a
        // subject
        // runs out of questions (the remaining slots are filled from subjects with more
        // questions).
        List<Question> selected = new ArrayList<>();
        int round = 0;
        boolean anyProgress;
        do {
            anyProgress = false;
            for (List<Question> pool : pools) {
                if (selected.size() >= limit)
                    break;
                if (round < pool.size()) {
                    selected.add(pool.get(round));
                    anyProgress = true;
                }
            }
            round++;
        } while (selected.size() < limit && anyProgress);

        // Step 3: Final shuffle so questions from different subjects are interleaved
        // randomly
        Collections.shuffle(selected);

        log.debug("Returning {} questions", selected.size());

        return selected.stream().map(this::mapToDTO).collect(Collectors.toList());
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
