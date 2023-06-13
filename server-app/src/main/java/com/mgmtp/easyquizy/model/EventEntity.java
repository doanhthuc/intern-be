package com.mgmtp.easyquizy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "events")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "location")
    private String location;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quizzes")
    @JsonIgnore
    private List<QuizEntity> quizEntity;

}
