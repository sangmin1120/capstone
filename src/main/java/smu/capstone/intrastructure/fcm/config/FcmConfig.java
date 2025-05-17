    package smu.capstone.intrastructure.fcm.config;

    import com.google.auth.oauth2.GoogleCredentials;
    import com.google.firebase.FirebaseApp;
    import com.google.firebase.FirebaseOptions;
    import com.google.firebase.messaging.FirebaseMessaging;
    import jakarta.annotation.PostConstruct;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.io.ClassPathResource;

    import java.io.IOException;

    @Configuration
    @Slf4j
    public class FcmConfig {

        private final ClassPathResource firebaseResource;
        private final String projectId;

        public FcmConfig(
                @Value("${fcm.file_path}") String firebaseFilePath,
                @Value("${fcm.project_id}") String projectId
        ) {
            this.firebaseResource = new ClassPathResource(firebaseFilePath);
            this.projectId = projectId;
        }

        @Bean
        public FirebaseApp firebaseApp() throws IOException {
            // 이미 초기화된 FirebaseApp이 없으면 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
                        .setProjectId(projectId)
                        .build();
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        }

        @Bean
        public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
            return FirebaseMessaging.getInstance(firebaseApp);
        }

    }