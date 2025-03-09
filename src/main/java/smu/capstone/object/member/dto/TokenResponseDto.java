package smu.capstone.object.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.capstone.web.jwt.TokenType;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponseDto {
    TokenType tokenType;
    String token;
}
