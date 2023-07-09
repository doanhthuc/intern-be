package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.GenerateQuizRequestDTO;
import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.dto.QuizDTO;
import com.mgmtp.easyquizy.dto.QuizDtoDetail;
import com.mgmtp.easyquizy.exception.DuplicatedQuestionException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuizService {

    QuizDtoDetail createQuiz(QuizDtoDetail quizDtoDetail) throws RecordNotFoundException, DuplicatedQuestionException;

    Page<QuizDTO> getAllQuizOfEvent(Long eventId, String keyword, int offset, int limit) throws RecordNotFoundException;

    QuizDtoDetail getQuizById(Long id) throws RecordNotFoundException;

    QuizDtoDetail updateQuiz(QuizDtoDetail quizDtoDetail) throws RecordNotFoundException, DuplicatedQuestionException;

    void deleteQuizById(Long id) throws RecordNotFoundException;

    List<QuestionListViewDTO> generateQuiz(GenerateQuizRequestDTO generateQuizRequestDTO);
}
