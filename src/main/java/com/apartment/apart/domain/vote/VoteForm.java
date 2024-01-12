package com.apartment.apart.domain.vote;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VoteForm {

    @NotEmpty(message = "제목을 입력해주세요")
    @Size(max = 200)
    private String title;
    @NotEmpty(message = "내용을 입력해주세요")
    private String content;
    @NotEmpty(message = "시작일을 선택해주세요")
    private String start;
    @NotEmpty(message = "종료일을 선택해주세요")
    private String end;


}
