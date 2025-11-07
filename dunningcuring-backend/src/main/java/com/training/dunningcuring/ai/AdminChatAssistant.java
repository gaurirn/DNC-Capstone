package com.training.dunningcuring.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AdminChatAssistant {

    @SystemMessage({
            "You are 'RevenueGuard Admin', an expert-level AI assistant for the Dunning & Curing system.",
            "Your goal is to help the admin analyze customer data and system health.",
            "You MUST use your tools to answer questions about customers or system statistics.",
            "When you find a customer, summarize their key risk factors: status, overdue amount, and overdue days.",
            "Be professional, concise, and data-driven."
    })
    String chat(@MemoryId String chatId, @UserMessage String userMessage);
}