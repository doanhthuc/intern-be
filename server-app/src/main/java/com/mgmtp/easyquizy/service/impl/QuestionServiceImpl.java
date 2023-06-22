package com.mgmtp.easyquizy.service.impl;

import com.mgmtp.easyquizy.dto.QuestionListViewDTO;
import com.mgmtp.easyquizy.mapper.QuestionMapper;
import com.mgmtp.easyquizy.model.question.Difficulty;
import com.mgmtp.easyquizy.model.question.QuestionEntity;
import com.mgmtp.easyquizy.repository.QuestionRepository;
import com.mgmtp.easyquizy.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository repository;
    @Autowired
    private QuestionMapper questionMapper;

    public Page<QuestionListViewDTO> getAllQuestions(
            String keyword, Difficulty difficulty, Integer categoryId, int offset, int limit) {
        int pageNo = offset / limit;
        Specification<QuestionEntity> filterSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get("title"), "%" + keyword + "%"));
            }
            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryEntity").get("id"), categoryId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo, limit);

        Page<QuestionEntity> page = repository.findAll(filterSpec, pageable);
        return page.map(questionMapper::questionToQuestionListViewDTO);
    }
}
