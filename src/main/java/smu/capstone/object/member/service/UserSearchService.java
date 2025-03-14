package smu.capstone.object.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.dto.UserSearchDto;
import smu.capstone.object.member.respository.UserRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_ID_OR_PASSWORD;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserRepository userRepository;

    public String searchId(UserSearchDto.SearchIdRequest searchIdRequest) {

        return userRepository.findByEmail(searchIdRequest.getEmail())
                .map(UserEntity::getUserid)
                .orElseThrow(() -> new RestApiException(INVALID_ID_OR_PASSWORD));
    }
}
