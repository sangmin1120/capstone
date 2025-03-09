package smu.capstone.web.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.web.jwt.redisdomain.RefreshTokenCache;
import smu.capstone.web.jwt.redisrepository.RefreshTokenCacheRepository;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_TOKEN;
import static smu.capstone.common.errorcode.AuthExceptionCode.REFRESH_TOKEN_NOT_EXIST;


@Service
@RequiredArgsConstructor
public class TokenService {
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
        RefreshTokenCache refreshTokenCache = refreshTokenCacheRepository.findById(refreshToken)
                .orElseThrow(() -> new RestApiException(REFRESH_TOKEN_NOT_EXIST));

        refreshTokenCacheRepository.delete(refreshTokenCache);
    }
}