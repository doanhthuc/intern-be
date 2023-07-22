package com.mgmtp.easyquizy.model.kahoot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizUserId implements Serializable {
    @Column(name = "kahoot_user_id", nullable = false)
    private String kahootUserId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;
}
