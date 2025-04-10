package smu.capstone.domain.member.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class LoginUserUtil {
    public Long getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return -1L;
    }
}
