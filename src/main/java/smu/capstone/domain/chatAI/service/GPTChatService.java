package smu.capstone.domain.chatAI.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import smu.capstone.domain.chatAI.dto.GPTMessage;
import smu.capstone.domain.chatAI.gpt.GPTClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GPTChatService {

    @Qualifier("gptRedisTemplate")
    private final RedisTemplate<String, Object> gptRedisTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    private final GPTClient gptClient;

    private static final int MAX_HISTORY = 20;


    public GPTMessage chat(String userId, String prompt) {
        String key = "gpt:chat:" + userId;
        List<Object> rawMessages = gptRedisTemplate.opsForList().range(key, 0, -1);
        List<GPTMessage> history = rawMessages.stream()
                .filter(o -> o instanceof GPTMessage)
                .map(o -> (GPTMessage) o)
                .toList();

        // [1] 오늘 날짜 포함된 system 지침 생성
        String systemPrompt = PromptBuilder.buildSystemPrompt(LocalDate.now());

        // [2~5] 최종 메시지 배열 생성
        List<GPTMessage> messages = new ArrayList<>();
        messages.add(new GPTMessage("system", systemPrompt)); // [1]
        messages.add(new GPTMessage("user", "다음은 지난 대화 기록이야.")); // [2]
        messages.addAll(history); // [3]
        messages.add(new GPTMessage("user", "이번엔 사용자의 질문이야. 단계에 맞는 자연스러운 질문을 해줘.")); // [4]
        messages.add(new GPTMessage("user", prompt)); // [5]

        // 디버깅용 전체 대화 흐름 출력
        System.out.println("=== GPT 호출 직전 전체 메시지 흐름 ===");
        for (GPTMessage msg : messages) {
            System.out.printf("%s: %s%n", msg.getRole(), msg.getContent());
        }
        // GPT 호출
        GPTMessage gptResponse = gptClient.callGPT(messages);

        // Redis에는 사용자 질문 + GPT 응답만 저장
        gptRedisTemplate.opsForList().rightPush(key, new GPTMessage("user", prompt));
        gptRedisTemplate.opsForList().rightPush(key, gptResponse);

        return gptResponse;
    }


}
