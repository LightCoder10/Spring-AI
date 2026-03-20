package com.example.AILearning;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SafetyAdvisor implements CallAdvisor {

    private static final List<String> BLOCKED_TOPICS = List.of(
            "violence", "weapons", "hack", "illegal"
    );

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        String userMessage = chatClientRequest.prompt().getContents().toLowerCase();

        boolean isBlocked = BLOCKED_TOPICS.stream().anyMatch(userMessage::contains);


        if (isBlocked) {
            return ChatClientResponse.builder().chatResponse(
                    new ChatResponse(List.of(new Generation((new AssistantMessage("I cannot help it with that topic")))))

            ).build();
        }

        return callAdvisorChain.nextCall(chatClientRequest);

    }

    @Override
    public String getName() {
        return "SafetyAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
