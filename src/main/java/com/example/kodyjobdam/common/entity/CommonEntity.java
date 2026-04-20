package com.example.kodyjobdam.common.entity;

import com.example.kodyjobdam.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor()
@AllArgsConstructor
@Table(name="reservation")
public class CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    private LocalDate date;

    private String period;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private String content;

    private Long teacher_id;

    @Enumerated(EnumType.STRING) //DB에 이 ENUM을 문자열로 저장해줘
    private StateEnum state = StateEnum.WAITING;

    @Enumerated(EnumType.STRING)
    private KindEnum kind;

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public void setTeacher_id(Long teacher_id) {
        this.teacher_id = teacher_id;
    }
}
