package smu.capstone.domain.chatAI.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.chatAI.dto.ChatRequest;
import smu.capstone.domain.chatAI.dto.GPTMessage;
import smu.capstone.domain.chatAI.service.GPTChatService;


@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class GPTChatController {

    private final GPTChatService gptChatService;

    @PostMapping("/chat")
    public ResponseEntity<GPTMessage> chat(@RequestBody ChatRequest request) {
        GPTMessage response = gptChatService.chat(request.getUserId(), request.getPrompt());
        return ResponseEntity.ok(response);
    }
}

