package com.example.AILearning;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class AppTool {


    @Tool(description = "Get the current date and time")
    public String getCurrentDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    @Tool(description = "Get the weather for a given city")
    public String getWeather(String city) {
        // In real app this would call a weather API
        // For now return mock data
        return switch (city.toLowerCase()) {
            case "chennai"  -> "32°C, Humid, Partly Cloudy";
            case "mumbai"   -> "29°C, Humid, Overcast";
            case "bangalore"-> "24°C, Pleasant, Clear Sky";
            case "delhi"    -> "38°C, Hot, Sunny";
            default         -> "Weather data not available for " + city;
        };
    }

    @Tool(description = "Get order details for a customer by customer ID")
    public String getOrderDetails(String customerId) {
        // In real app this would query your database
        return switch (customerId) {
            case "1234" -> "Order #001: MacBook Pro - Delivered. " +
                    "Order #002: iPhone 15 - In Transit.";
            case "5678" -> "Order #003: Samsung TV - Processing.";
            default     -> "No orders found for customer " + customerId;
        };
    }}
