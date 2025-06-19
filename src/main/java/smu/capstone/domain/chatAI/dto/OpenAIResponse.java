package smu.capstone.domain.chatAI.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private GPTMessage message;
        private int index;
        private Object logprobs;
        private String finish_reason;
    }
}

