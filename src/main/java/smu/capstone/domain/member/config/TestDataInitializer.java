package smu.capstone.domain.member.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import smu.capstone.domain.member.entity.Authority;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.entity.UserType;
import smu.capstone.domain.member.respository.UserRepository;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // testUser1이 없으면 test ID 생성
        if (userRepository.findByAccountId("testUser1").isEmpty()) {
            UserEntity testUser = UserEntity.builder()
                    .accountId("testUser1")
                    .email("testUser1@test.com")
                    .password(passwordEncoder.encode("@testUser1"))
                    .username("테스트 유저 1")
                    .isDeleted(false)
                    .userType(UserType.PATIENT)
                    .authority(Authority.ROLE_USER)
                    .build();

            userRepository.save(testUser);
        }

        // testUser2이 없으면 test ID 생성
        if (userRepository.findByAccountId("testUser2").isEmpty()) {
            UserEntity testUser = UserEntity.builder()
                    .accountId("testUser2")
                    .email("testUser2@test.com")
                    .password(passwordEncoder.encode("@testUser2"))
                    .username("테스트 유저 2")
                    .isDeleted(false)
                    .userType(UserType.PATIENT)
                    .authority(Authority.ROLE_USER)
                    .build();

            userRepository.save(testUser);
        }
    }
}
