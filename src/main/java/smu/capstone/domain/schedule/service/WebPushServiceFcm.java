package smu.capstone.domain.schedule.service;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@Slf4j
public class WebPushServiceFcm {

    private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/capstone1-b400b/messages:send";

    // 1. AccessToken 발급
    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("firebase/service-account-key.json").getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    // 2. Web Push 전송
    public void sendPush(String targetToken, String title, String body) {
        try {
            String accessToken = getAccessToken();

            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            JSONObject messageObject = new JSONObject();
            messageObject.put("token", targetToken);
            messageObject.put("notification", notification);

            message.put("message", messageObject);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FCM_ENDPOINT))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json; UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(message.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Web Push 전송 완료. response = {}", response.body());

        } catch (Exception e) {
            log.error("Web Push 전송 실패", e);
        }
    }
}

