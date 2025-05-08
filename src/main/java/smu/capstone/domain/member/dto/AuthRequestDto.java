package smu.capstone.domain.member.dto;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import smu.capstone.domain.member.entity.Authority;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.entity.UserType;

public class AuthRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Login {
        String accountId;
        String password;

        String fcmToken; // fcm 토큰 추가
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SignUp {
        String accountId;
        String email;
        String password;
        String check_password;
        String certificationKey;
        String username;
        String phoneNumber; // 010-1111-1111
        String birth; // 생년월일 year.month.day
        UserType userType;

        public UserEntity toDto(PasswordEncoder passwordEncoder) {
            return UserEntity.builder()
                    .accountId(accountId)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .phoneNumber(phoneNumber)
                    .birth(birth)
                    .userType(userType)
                    .authority(Authority.ROLE_USER)
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DupAccountId {
        String accountId;
    }
}
