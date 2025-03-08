package smu.capstone.web.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.web.security.dto.CustomUserDetails;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        Optional<UserEntity> userData = userRepository.findByEmail(email);
        System.out.println("CustomUserDetailsService");
        if (userData.isPresent()) {
            return new CustomUserDetails(userData.get());
        }
        return null;
    }
}
