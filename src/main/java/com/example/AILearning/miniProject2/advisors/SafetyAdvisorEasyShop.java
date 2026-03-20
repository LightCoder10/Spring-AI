package com.example.AILearning.miniProject2.advisors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SafetyAdvisorEasyShop implements CallAdvisor {
    private static final List<String> BLOCKED_WORDS = Arrays.asList("amazon","walmart", "flipkart", "shit" ,"fuck", "bitch");
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
      String message = chatClientRequest.prompt().getContents().toLowerCase();

      boolean isBlocked  = BLOCKED_WORDS.stream().anyMatch(message::contains);

      if(isBlocked) {
          return ChatClientResponse.builder()
                  .chatResponse(new ChatResponse(List.of(new Generation
                          (new AssistantMessage("sorry thats not a right word"))))).build();
      }

      return callAdvisorChain.nextCall(chatClientRequest);

    }

    @Override
    public String getName() {
        return "shopSafetyAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
