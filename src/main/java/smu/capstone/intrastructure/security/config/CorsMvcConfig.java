package smu.capstone.intrastructure.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost") // 나중에 수정해야됨
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 필요한 메서드 허용
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
