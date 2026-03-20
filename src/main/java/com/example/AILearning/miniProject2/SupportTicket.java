package com.example.AILearning.miniProject2;

import java.time.LocalDateTime;

public record SupportTicket(
        String ticketId,
        String customerId,
        String issue,
        String priority,
        String estimatedResolutionTime



) {
}
