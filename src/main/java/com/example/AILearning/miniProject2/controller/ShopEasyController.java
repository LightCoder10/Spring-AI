package com.example.AILearning.miniProject2.controller;


import com.example.AILearning.miniProject2.SupportTicket;
import com.example.AILearning.miniProject2.service.ShopEasyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
public class ShopEasyController {


    private final ShopEasyService shopEasyService;

    public ShopEasyController(ShopEasyService shopEasyService) {
        this.shopEasyService = shopEasyService;
    }

    @PostMapping("/support/chat")
    public Flux<String> chatBot(@RequestParam String message, @RequestParam String conversationId,
                                @RequestParam String customerId) {
        return shopEasyService.chatBot(message,conversationId,customerId);
    }

    @PostMapping("/support/chat-image")
    public Flux<String> chatBotWithImage(@RequestParam String message, @RequestParam String conversationId,
                                         @RequestParam String customerId, @RequestBody MultipartFile image) throws IOException {
        return shopEasyService.chatBotWithImage(message,conversationId,customerId,image);
    }


    @PostMapping("/ticket")
    public SupportTicket createSupportTicket(@RequestParam String customerId, @RequestParam String issue) {
        return shopEasyService.supportTicketCreate(customerId,issue);
    }

}
