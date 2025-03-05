package smu.capstone.domain.user.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import smu.capstone.domain.user.entity.Role;
import smu.capstone.domain.user.entity.UserEntity;
import smu.capstone.domain.user.entity.UserType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequestDto {

    @NotBlank
    private String username;
    @Email
    private String email;
    @NotBlank
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    //    private String checkedPassword;
    private Role role;

    public UserSignUpRequestDto(String username, String email, String password, UserType userType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    @Builder
    public UserEntity toEntity() {
        return UserEntity.builder().
                username(username).
                email(email).
                password(password).
                role(Role.ROLE_USER).
                createdAt(LocalDateTime.now()).
                userType(userType).
                build();
    }
}
