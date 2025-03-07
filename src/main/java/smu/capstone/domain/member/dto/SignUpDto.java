package smu.capstone.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import smu.capstone.domain.member.entity.Role;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.entity.UserType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    @NotBlank
    private String username;
    @Email
    private String email;
    @NotBlank
    private String password;
    private UserType userType;
    //    private String checkedPassword;
    private Role role;

    public SignUpDto(String username, String email, String password, UserType userType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(role != null ? role : Role.ROLE_USER) // 기본값 설정
                .createdAt(LocalDateTime.now())
                .userType(userType)
                .build();
    }
}
