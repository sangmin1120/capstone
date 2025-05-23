package smu.capstone.intrastructure.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.intrastructure.jwt.service.TokenProvider;
import smu.capstone.intrastructure.jwt.TokenType;

import java.io.IOException;

/**
 * 들어오는 토큰을 filter
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = tokenProvider.getAccessToken(request);
            String refreshToken = tokenProvider.getRefreshToken(request);

            tokenProvider.validateToken(TokenType.ACCESS_TOKEN, accessToken);
            tokenProvider.validateToken(TokenType.REFRESH_TOKEN, refreshToken);

            Authentication authentication = tokenProvider.createAuthenticationByAccessToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (RestApiException ignored) {
        }
        filterChain.doFilter(request, response);
    }
}
