package com.harvest.bagain.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinReq {

    @NotNull(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotNull(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotNull(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 3, max = 12, message = "닉네임은 3자에서 12자 사이여야 합니다.")
    private String nickname;

    @NotNull(message = "전화번호는 필수 입력 항목입니다.")
    private String phoneNumber;
    
    private String postalCode;
    private String address;
    private String detailAddress;
    private String photoFilename;
}