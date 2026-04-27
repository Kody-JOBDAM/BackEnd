package com.example.kodyjobdam.common.dto.request;

import com.example.kodyjobdam.User.User;
import com.example.kodyjobdam.common.entity.CommonEntity;
import com.example.kodyjobdam.common.entity.KindEnum;
import com.example.kodyjobdam.common.entity.StateEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDTO {

    private String title;

    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String period;

    private KindEnum kind;

    public CommonEntity toEntity(User user) { //title, content 이런건 DTO에 있는 값
        return CommonEntity.builder()
                .title(title)
                .content(content)
                .date(date)
                .period(period)
                .kind(kind)
                .state(StateEnum.WAITING)
                .user(user)
                .build();
    }
}
