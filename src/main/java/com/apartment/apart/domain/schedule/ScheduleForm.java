package com.apartment.apart.domain.schedule;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleForm {
    @NotEmpty(message = "일정을 입력해주세요")
    private String title;
    @NotEmpty(message = "시작일을 선택해주세요")
    private String start;
    @NotEmpty(message = "종료일을 선택해주세요")
    private String end;
}
