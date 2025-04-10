package smu.capstone.intrastructure.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.intrastructure.jwt.TokenType;
import smu.capstone.intrastructure.redis.domain.RefreshTokenCache;
import smu.capstone.intrastructure.redis.repository.RefreshTokenCacheRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_TOKEN;
import static smu.capstone.common.errorcode.AuthExceptionCode.REFRESH_TOKEN_NOT_EXIST;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private static final String PAYLOAD_VALUE = "accountId";
    private final RefreshTokenCacheRepository refreshTokenCacheRepository;
    private final TokenProvider tokenProvider;

    public String refreshAccessToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);

        refreshTokenCacheRepository.findById(refreshToken).orElseThrow(() ->
                new RestApiException(INVALID_TOKEN)
        );

        return tokenProvider.createAccessTokenByRefreshToken(refreshToken);
    }

    public void deleteRefreshToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);
//        log.info("refreshToken={}", refreshToken);
        RefreshTokenCache refreshTokenCache = refreshTokenCacheRepository.findById(refreshToken)
                .orElseThrow(() -> new RestApiException(REFRESH_TOKEN_NOT_EXIST));
//        log.info("refreshToken={}", refreshTokenCache.getRefreshToken());
        refreshTokenCacheRepository.delete(refreshTokenCache);
    }

    public String getUserid(String token) {
        return tokenProvider.parseTokenClaims(TokenType.ACCESS_TOKEN, token)
                .get(PAYLOAD_VALUE, String.class);
    }
}