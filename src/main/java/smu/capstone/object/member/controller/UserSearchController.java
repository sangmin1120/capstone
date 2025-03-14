package smu.capstone.object.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.member.dto.UserSearchDto;
import smu.capstone.object.member.service.UserSearchService;

@RestController
@RequestMapping("/api/user-search")
@RequiredArgsConstructor
@Slf4j
public class UserSearchController {

    private final UserSearchService userSearchService;

    //아이디 찾기
    @PostMapping("/id")
    public BaseResponse<String> id_search(@RequestBody UserSearchDto.SearchIdRequest searchIdRequest) {

        log.info("Searching user with email {}", searchIdRequest.getEmail());
        return BaseResponse.ok(userSearchService.searchId(searchIdRequest));
    }

    //비밀번호 찾기
}
