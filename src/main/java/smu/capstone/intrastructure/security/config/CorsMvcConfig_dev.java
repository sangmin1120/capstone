package smu.capstone.intrastructure.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("dev")
@Configuration
public class CorsMvcConfig_dev implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 나중에 수정해야됨
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 필요한 메서드 허용
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
