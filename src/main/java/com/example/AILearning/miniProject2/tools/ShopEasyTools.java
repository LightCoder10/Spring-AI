package com.example.AILearning.miniProject2.tools;


import com.example.AILearning.miniProject2.SupportTicket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ShopEasyTools {

//private final ChatClient client;
//p
//
//    public ShopEasyTools(ChatClient.Builder client) {
//        this.client = client.build();
//    }


    @Tool(description = "to get the order status of the customer with customer id")
    public String getOrderStatus(String orderId) {



        return  switch (orderId) {
            case "101" -> "PACKING IN PROGRESS";
            case "102" -> "DELIVERED";
            case "103" -> "IN TRANSIT";
            default -> "such id doesn't exist";
        };

    }



    @Tool(description = "to get customer order details with customer id")
    public List<String> getCustomerOrders(String customerId) {
        return switch (customerId) {
            case "a001" -> List.of("shaving cream", "brush", "tooth paste");
            case "1002" -> List.of("colgate", "Ps5", "coffee powder");
            default -> List.of("No orders for this Id");


        };

    }

    @Tool(description = "to check if the order is eligible for refund or not using order id")
    public boolean checkRefundEligibility(String orderId) {
        return  switch (orderId) {
            case "101" -> true;
            case "102" -> false;
            case "103" -> true;
            default -> false;
        };
    }


    @Tool(description = "this method is used for support ticket creation")
    public String supportTicketCreation(String customerId, String issue) {
        SupportTicket supportTicket = new SupportTicket(
                "TKT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),customerId,
                issue, determinePriority(issue),"24 hrs");
        return supportTicket.toString();
    }


    private String determinePriority(String issue) {
        if (issue.toLowerCase().contains("refund") ||
                issue.toLowerCase().contains("payment")) return "HIGH";
        if (issue.toLowerCase().contains("damaged")) return "HIGH";
        return "MEDIUM";
    }



}
