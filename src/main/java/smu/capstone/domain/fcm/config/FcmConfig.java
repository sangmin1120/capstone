    package smu.capstone.domain.fcm.config;

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

        @PostConstruct
        public void init() throws IOException {
            FirebaseOptions option = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
                    .setProjectId(projectId)
                    .build();

            // FirebaseApp 인스턴스가 초기화되지 않은 경우 초기화, 이미 존재하는 경우 해당 인스턴스를 가져옴
            FirebaseApp firebaseApp = FirebaseApp.getApps().isEmpty() ?
                    FirebaseApp.initializeApp(option) :
                    FirebaseApp.getInstance();

            log.info("Firebase application has been initialized");
        }

        @Bean
        FirebaseMessaging firebaseMessaging() {
            return FirebaseMessaging.getInstance(firebaseApp());
        }

        @Bean
        FirebaseApp firebaseApp() {
            return FirebaseApp.getInstance();
        }
    }