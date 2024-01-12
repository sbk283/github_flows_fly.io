package com.apartment.apart.domain.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateForm {

    @NotEmpty(message = "사용자ID는 필수 입력 항목입니다.")
    @Size(min = 3, max = 15, message = "사용자ID는 3자 이상 15자 이하로 입력해주세요.")
    private String username;

    @NotEmpty(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 3, max = 15, message = "사용자명은 3자 이상 15자 이하로 입력해주세요.")
    private String nickname;

    @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String Password1;

    @NotEmpty(message = "비밀번호 재확인은 필수입니다.")
    private String Password2;

    @NotEmpty(message = "전화번호는 필수 입력 항목입니다.")
    @Size(min = 11, max = 11, message = " 전화번호는 - 기호를 빼고 입력해주세요.")
    private String phone;

    @NotEmpty(message = "이메일은 필수 입력 항목입니다.")
    private String email;


    private int apartDong;

    private int apartHo;
}