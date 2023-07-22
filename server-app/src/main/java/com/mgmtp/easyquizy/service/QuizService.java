package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.question.QuestionListViewDTO;
import com.mgmtp.easyquizy.dto.quiz.GenerateQuizRequestDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDTO;
import com.mgmtp.easyquizy.dto.quiz.QuizDtoDetail;
import com.mgmtp.easyquizy.exception.DuplicatedQuestionException;
import com.mgmtp.easyquizy.exception.NoMatchEventIdException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuizService {

    QuizDTO createQuiz(QuizDTO quizDTO) throws RecordNotFoundException, DuplicatedQuestionException;

    Page<QuizDTO> getAllQuizOfEvent(Long eventId, String keyword, int offset, int limit) throws RecordNotFoundException;

    QuizDtoDetail getQuizById(Long id) throws RecordNotFoundException;

    QuizDTO updateQuiz(QuizDTO quizDTO) throws RecordNotFoundException, DuplicatedQuestionException, NoMatchEventIdException;

    void deleteQuizById(Long id, boolean deleteKahootQuiz) throws RecordNotFoundException;

    List<QuestionListViewDTO> generateQuiz(GenerateQuizRequestDTO generateQuizRequestDTO);
}
