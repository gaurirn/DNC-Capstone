//package com.training.dunningcuring.config;
//
//import com.training.dunningcuring.ai.DunningTools;
//import dev.langchain4j.memory.chat.MessageWindowChatMemory;
//import dev.langchain4j.model.chat.ChatLanguageModel;
//import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
//import dev.langchain4j.service.AiServices;
//import dev.langchain4j.store.memory.chat.ChatMemoryStore;
//import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AiConfig {
//
//    @Value("${gemini.api.key:${GOOGLE_API_KEY:}}")
//    private String geminiApiKey;
//
//    @Value("${gemini.api.model:gemini-2.0-flash}")
//    private String geminiModel;
//
//    @Value("${langchain.cache.enabled:true}")
//    private boolean cacheEnabled;
//
//    @Bean
//    public ChatMemoryStore chatMemoryStore() {
//        return new InMemoryChatMemoryStore();
//    }
//
//    @Bean
//    public ChatLanguageModel chatLanguageModel() {
//        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
//            throw new RuntimeException("GOOGLE_API_KEY (or gemini.api.key) is not set. Set it as env var or in application.properties.");
//        }
//
//        return GoogleAiGeminiChatModel.builder()
//                .apiKey(geminiApiKey)
//                .modelName(geminiModel)
//                .temperature(0.4)
//                .maxOutputTokens(1024)
//                .build();
//    }
//
//    @Bean
//    public ChatAssistant chatAssistant(ChatLanguageModel chatLanguageModel,
//                                       ChatMemoryStore chatMemoryStore,
//                                       DunningTools dunningTools) {
//
//        return AiServices.builder(ChatAssistant.class)
//                .chatLanguageModel(chatLanguageModel)
//                .chatMemoryProvider(chatId -> MessageWindowChatMemory.builder()
//                        .chatMemoryStore(chatMemoryStore)
//                        .maxMessages(20)
//                        .id(chatId)
//                        .build())
//                .tools(dunningTools)
//                .build();
//    }
//}
