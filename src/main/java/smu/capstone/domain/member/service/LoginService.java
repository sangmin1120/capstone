package smu.capstone.domain.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.dto.TokenResponseDto;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.intrastructure.jwt.service.TokenProvider;
import smu.capstone.intrastructure.redis.domain.RefreshTokenCache;
import smu.capstone.intrastructure.redis.repository.RefreshTokenCacheRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_ID_OR_PASSWORD;
import static smu.capstone.intrastructure.jwt.TokenType.ACCESS_TOKEN;
import static smu.capstone.intrastructure.jwt.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final RefreshTokenCacheRepository refreshTokenCacheRepository;

    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Transactional
    public TokenResponseDto login(HttpServletResponse response, AuthRequestDto.Login authRequestDto) {


        String userid = authRequestDto.getAccountId();
        UserEntity user = userRepository.findByAccountId(userid).orElseThrow(() ->
                new RestApiException(INVALID_ID_OR_PASSWORD));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new RestApiException(INVALID_ID_OR_PASSWORD);
        }

        String accessToken = tokenProvider.createToken(ACCESS_TOKEN, user.getId(), userid, user.getAuthority().name());
        Authentication authentication = tokenProvider.createAuthenticationByAccessToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshToken = tokenProvider.createToken(REFRESH_TOKEN, user.getId(), userid, user.getAuthority().name());
        saveRefreshToken(refreshToken);
        setRefreshTokenCookie(response, refreshToken);

        return TokenResponseDto.builder()
                .tokenType(ACCESS_TOKEN)
                .token(accessToken)
                .build();
    }

    private void saveRefreshToken(String refreshToken) {
        refreshTokenCacheRepository.save(RefreshTokenCache.builder()
                .refreshToken(refreshToken)
                .expiration(tokenProvider.getExpirationSeconds(REFRESH_TOKEN, refreshToken))
                .build());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        String cookieValue = "Bearer " + refreshToken;
        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(REFRESH_TOKEN.getHeader(), cookieValue);
        int expirationSeconds = (int) tokenProvider.getExpirationSeconds(REFRESH_TOKEN, refreshToken);
        cookie.setMaxAge(expirationSeconds);

        if (isProd()) {
            setRefreshTokenCookieWhenProd(response, cookie);
        }

        if (!isProd()) {
            setRefreshTokenCookieWhenLocal(response, cookie);
        }
    }

    private boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    private void setRefreshTokenCookieWhenProd(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("rehab.me");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookieWhenLocal(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }
}
