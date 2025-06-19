package smu.capstone.domain.chatAI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String role;     // "assistant"
    private String content;  // GPT 가 생성한 텍스트
}
