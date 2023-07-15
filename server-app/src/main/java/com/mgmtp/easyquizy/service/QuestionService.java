package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.question.QuestionDTO;
import com.mgmtp.easyquizy.dto.question.QuestionListViewDTO;
import com.mgmtp.easyquizy.exception.InvalidFieldsException;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.model.question.Difficulty;
import org.springframework.data.domain.Page;

public interface QuestionService {
    QuestionDTO getQuestionById(Long id) throws RecordNotFoundException;

    QuestionDTO createQuestion(QuestionDTO questionDTO) throws RecordNotFoundException;

    QuestionDTO updateQuestion(QuestionDTO questionDTO) throws RecordNotFoundException, InvalidFieldsException;

    void deleteQuestionById(Long id) throws RecordNotFoundException;

    Page<QuestionListViewDTO> getAllQuestions(String keyword, Difficulty difficulty, Integer categoryId, int offset, int limit);
}
