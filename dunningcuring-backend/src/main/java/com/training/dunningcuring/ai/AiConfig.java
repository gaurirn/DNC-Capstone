package com.training.dunningcuring.ai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // --- SHARED BEANS ---

    @Bean
    public ChatMemoryStore chatMemoryStore() {
        // A single, shared memory store for all chat sessions
        return new InMemoryChatMemoryStore();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model}") String modelName) { // <-- THIS IS THE FIX (Part 1)

        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("$")) {
            throw new RuntimeException(
                    "Error: 'gemini.api.key' is not set. " +
                            "Please make sure your GOOGLE_API_KEY environment variable is set correctly."
            );
        }

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName) // <-- THIS IS THE FIX (Part 2)
                .temperature(0.3)
                .maxOutputTokens(1000)
                .build();
    }

    // --- CUSTOMER CHATBOT ---

    @Bean
    public DunningChatAssistant customerChatAssistant(ChatLanguageModel chatLanguageModel,
                                                      ChatMemoryStore chatMemoryStore,
                                                      DunningTools dunningTools) { // <-- Customer Tools

        return AiServices.builder(DunningChatAssistant.class) // <-- Customer Interface
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatId -> MessageWindowChatMemory.builder()
                        .chatMemoryStore(chatMemoryStore)
                        .maxMessages(20)
                        .id(chatId)
                        .build())
                .tools(dunningTools) // <-- Wire Customer Tools
                .build();
    }

    // --- ADMIN CHATBOT ---

    @Bean
    public AdminChatAssistant adminChatAssistant(ChatLanguageModel chatLanguageModel,
                                                 ChatMemoryStore chatMemoryStore,
                                                 com.training.dunningcuring.ai.AdminTools adminTools) { // <-- Admin Tools

        return AiServices.builder(AdminChatAssistant.class) // <-- Admin Interface
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatId -> MessageWindowChatMemory.builder()
                        .chatMemoryStore(chatMemoryStore)
                        .maxMessages(20)
                        .id(chatId)
                        .build())
                .tools(adminTools) // <-- Wire Admin Tools
                .build();
    }
}