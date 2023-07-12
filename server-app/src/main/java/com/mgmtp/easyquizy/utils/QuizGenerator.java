package com.mgmtp.easyquizy.utils;

import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.model.question.QuestionEntity;

import java.util.*;
import java.util.stream.Collectors;

public class QuizGenerator {
    /**
     * Generates a quiz by selecting questions based on constraints using a greedy selection strategy.
     *
     * @param questions                The list of available questions.
     * @param totalTime                The total time limit for the quiz.
     * @param categoryPercentages      A map containing category IDs as keys and corresponding percentages as values,
     *                                 representing the maximum time percentage allowed for each category.
     * @param maxDifficultyPercentages A map containing Difficulties as keys and corresponding percentages as values,
     *                                 representing the maximum time percentage allowed for each difficulty.
     * @return A list of QuestionEntity objects representing the generated quiz.
     */
    public static List<QuestionEntity> generateQuiz(List<QuestionEntity> questions, int totalTime, Map<Long, Double> categoryPercentages) {

        //  Set the upper bound for each difficulty
        Map<Difficulty, Double> maxDifficultyPercentages = new HashMap<>();
        maxDifficultyPercentages.put(Difficulty.EASY, 0.6);
        maxDifficultyPercentages.put(Difficulty.MEDIUM, 0.35);
        maxDifficultyPercentages.put(Difficulty.HARD, 0.15);

        //  Use a category time counter to count the time of each category
        Map<Long, Integer> categoryTimeCount = new HashMap<>();
        categoryPercentages.forEach((categoryId, percentage) -> {
            categoryTimeCount.put(categoryId, 0);
        });

        //  Use a difficulty time counter to count the time of each difficulty
        Map<Difficulty, Integer> difficultyTimeCount = new HashMap<>();
        maxDifficultyPercentages.forEach((difficulty, percentage) -> {
            difficultyTimeCount.put(difficulty, 0);
        });

        // Calculate the upper time bound for each category
        Map<Long, Integer> upperBoundCategoryTime = new HashMap<>();
        categoryPercentages.forEach((categoryId, percentage) -> {
            upperBoundCategoryTime.put(categoryId, (int) (totalTime * percentage));
        });

        //  Calculate the upper time bound for each difficulty
        Map<Difficulty, Integer> upperBoundDifficultyTime = new HashMap<>();
        maxDifficultyPercentages.forEach((difficulty, percentage) -> {
            upperBoundDifficultyTime.put(difficulty, (int) (totalTime * percentage));
        });

        //  Shuffle the questions
        Collections.shuffle(questions);

        List<QuestionEntity> quiz = new ArrayList<>();
        int quizTotalTime = 0;

        //  Loop through questions and add them to the quiz if that questions meet the constraints
        for (QuestionEntity questionEntity : questions) {
            Difficulty difficulty = questionEntity.getDifficulty();
            Long categoryId = questionEntity.getCategory().getId();

            //  Check if the difficulty time count do not exceed the limit
            int currentDifficultyTimeCount = difficultyTimeCount.get(difficulty);
            boolean isDifficultyTimeWithinBounds = (currentDifficultyTimeCount < upperBoundDifficultyTime.get(difficulty));

            //  Check if the category time count do not exceed the limit
            int currentCategoryTimeCount = categoryTimeCount.get(categoryId);
            boolean isCategoryTimeWithinBounds = (currentCategoryTimeCount < upperBoundCategoryTime.get(categoryId));

            //  Add the question to the list if category time count and difficulty time count do not exceed the limit
            if (isCategoryTimeWithinBounds && isDifficultyTimeWithinBounds) {
                quiz.add(questionEntity);
                //  Update the time counters
                difficultyTimeCount.merge(difficulty, questionEntity.getTimeLimit(), Integer::sum);
                categoryTimeCount.merge(categoryId, questionEntity.getTimeLimit(), Integer::sum);
                quizTotalTime += questionEntity.getTimeLimit();
            }
            //  Check if the current time limit of the quiz is exceeded
            if (quizTotalTime >= totalTime) {
                break;
            }
        }
        // Check if the quiz total time is good enough
        if (quizTotalTime < totalTime * 0.9) {
            // Find the list of questions that are not in the quiz
            List<QuestionEntity> questionsNotInQuiz = questions.stream()
                    .filter(question -> !quiz.contains(question))
                    .collect(Collectors.toList());
            //  Sort the list of questions by difficulty in ascending order to prioritize easier questions
            questionsNotInQuiz.sort(Comparator.comparing(QuestionEntity::getDifficulty));
            //  If the quiz time limit is not reached, add more questions to the quiz
            for (QuestionEntity questionEntity : questionsNotInQuiz) {
                quiz.add(questionEntity);
                quizTotalTime += questionEntity.getTimeLimit();
                if (quizTotalTime >= totalTime) {
                    break;
                }
            }
        }

        // Shuffle the quiz to avoid any patterns in questions’ ordering caused by the algorithm
        Collections.shuffle(quiz);

        return quiz;
    }
}
