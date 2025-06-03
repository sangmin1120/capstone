package smu.capstone.domain.chatAI.gpt;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import smu.capstone.domain.chatAI.dto.GPTMessage;
import smu.capstone.domain.chatAI.dto.OpenAIRequest;
import smu.capstone.domain.chatAI.dto.OpenAIResponse;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GPTClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String apiKey;

    public GPTMessage callGPT(List<GPTMessage> messages) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        OpenAIRequest request = new OpenAIRequest("gpt-4", messages);
        HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OpenAIResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                OpenAIResponse.class
        );

        return response.getBody().getChoices().get(0).getMessage();
    }
}
