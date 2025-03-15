package smu.capstone.object.member.dto;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import smu.capstone.object.member.domain.Authority;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.domain.UserType;

import java.time.LocalDateTime;

public class AuthRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {
        String userid;
        String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SignUp {
        String userid;
        String email;
        String password;
        String check_password;
        String certificationKey;
        String username;
        UserType userType;

        public UserEntity toDto(PasswordEncoder passwordEncoder) {
            return UserEntity.builder()
                    .userid(userid)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .userType(userType)
                    .authority(Authority.ROLE_USER)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Modify {

        String newPassword;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VerificationMail {
        @Email
        String email;
        String verificationKey;
    }
}
