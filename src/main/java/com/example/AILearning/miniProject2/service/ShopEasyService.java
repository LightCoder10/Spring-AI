package com.example.AILearning.miniProject2.service;


import com.example.AILearning.miniProject2.SupportTicket;
import com.example.AILearning.miniProject2.advisors.SafetyAdvisorEasyShop;
import com.example.AILearning.miniProject2.tools.ShopEasyTools;
import com.google.common.primitives.Bytes;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Component
public class ShopEasyService {

    private final ChatClient chatClient;
    private final ShopEasyTools shopEasyTools;
    private final SafetyAdvisorEasyShop advisorEasyShop;
    private final ChatMemory chatMemory;

    public ShopEasyService(ChatClient.Builder chatClient, ShopEasyTools shopEasyTools, SafetyAdvisorEasyShop advisorEasyShop, ChatMemory chatMemory) {
        this.chatClient = chatClient.build();
        this.shopEasyTools = shopEasyTools;
        this.advisorEasyShop = advisorEasyShop;
        this.chatMemory = chatMemory;
    }


    //creating a stateful customer chat with memory using advisors


    public Flux<String> chatBot( String message, String conversationId,
                                 String customerId ) {

        return chatClient.prompt().system("you are an chat bot for an ecommerce platform shop easy" +
                "when the customer texts you you should greet them first and answer their questions based on the" +
                "customer request, you should also remember the conversation between them while chatting ")
                .user(
                        u -> u.text(message).text(conversationId).text(customerId)

                ).advisors(
                        advisorEasyShop,
                        MessageChatMemoryAdvisor.builder(chatMemory).conversationId(conversationId).build(),
                new SimpleLoggerAdvisor()

                ).tools(shopEasyTools).stream().content();

    }

    public Flux<String> chatBotWithImage(String message, String conversationId, String customerId, MultipartFile image) throws IOException {
        byte[] imageBytes = image.getBytes();
        var imageType = MimeTypeUtils.parseMimeType(image.getContentType());
        return chatClient.prompt().system("you will analyze the image and the texts shared by the user for this" +
                "ecommerce website and reply accordingly as an expert professional support person")
                .user( u-> u.text(message)
                        .media(new Media(imageType, new ByteArrayResource(imageBytes))).text(conversationId).text(customerId))
                .advisors(
                        advisorEasyShop,
                        MessageChatMemoryAdvisor.builder(chatMemory).conversationId(conversationId).build(),
                        new SimpleLoggerAdvisor()

                ).tools(shopEasyTools).stream().content();

    }


    public SupportTicket supportTicketCreate(String customerId, String issue) {
        return chatClient.prompt().user(u->u.text(issue).text(customerId)).advisors( advisorEasyShop,

                new SimpleLoggerAdvisor()

        ).tools(shopEasyTools).call().entity(SupportTicket.class);
    }




}
