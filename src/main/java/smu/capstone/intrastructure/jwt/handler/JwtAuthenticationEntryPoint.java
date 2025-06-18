package smu.capstone.intrastructure.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.response.BaseResponse;

import java.io.IOException;


/**
 * 인증 예외가 발생했을 때, 실행되는 클래스
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Authentication error: {}", authException.getMessage());
        BaseResponse<Void> baseResponse = BaseResponse.fail(new RestApiException(CommonStatusCode.UNAUTHORIZED));

        response.setStatus(baseResponse.getStatusCode().status().value());
        response.setContentType("application/json;charset=UTF-8");
        String result = mapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);
    }
}
