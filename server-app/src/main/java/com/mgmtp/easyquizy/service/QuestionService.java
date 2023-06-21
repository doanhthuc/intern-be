package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.model.question.Difficulty;
import org.springframework.data.domain.Page;

public interface QuestionService {
    Page<QuestionListViewDTO> getAllQuestions(String keyword, Difficulty difficulty, Integer categoryId, int offset, int limit);
}
