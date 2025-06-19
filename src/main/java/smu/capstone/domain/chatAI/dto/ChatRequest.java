package smu.capstone.domain.chatAI.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String userId;
    private String prompt;
}
