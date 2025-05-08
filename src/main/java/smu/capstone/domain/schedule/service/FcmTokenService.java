package smu.capstone.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;


@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final UserRepository userRepository;

    public void saveToken(UserEntity user, String token) {
        user.setFcmToken(token);
        userRepository.save(user);
    }
}

