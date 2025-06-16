package smu.capstone.intrastructure.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.intrastructure.jwt.service.TokenProvider;
import smu.capstone.intrastructure.jwt.TokenType;
import smu.capstone.intrastructure.jwt.service.TokenService;

import java.io.IOException;
import java.util.Enumeration;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_TOKEN;

/**
 * 들어오는 토큰을 filter
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final TokenService  tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 인증이 필요 없는 경로라면 필터 패스
        if (path.startsWith("/api/user-auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = tokenProvider.getAccessToken(request);
            String refreshToken = tokenProvider.getRefreshToken(request);

            if (tokenService.isBlackListed(accessToken)) {
                throw new RestApiException(INVALID_TOKEN);
            }

            tokenProvider.validateToken(TokenType.ACCESS_TOKEN, accessToken);
            tokenProvider.validateToken(TokenType.REFRESH_TOKEN, refreshToken);

            Authentication authentication = tokenProvider.createAuthenticationByAccessToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (RestApiException e) {
            log.info("[JwtFilter] Token validation failed: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
