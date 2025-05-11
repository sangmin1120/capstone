package smu.capstone.intrastructure.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.common.response.BaseResponse;

import java.io.IOException;

import static smu.capstone.common.errorcode.CommonStatusCode.FORBIDDEN;

/**
 * 접근 불가할 때,동작하는 클래스
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        BaseResponse<Void> baseResponse = BaseResponse.fail(new RestApiException(FORBIDDEN));

        response.setStatus(baseResponse.getStatusCode().status().value());
        response.setContentType("application/json;charset=UTF-8");
        String result = mapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);
    }
}