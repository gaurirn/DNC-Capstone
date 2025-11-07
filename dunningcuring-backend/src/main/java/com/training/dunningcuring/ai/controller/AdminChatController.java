package com.training.dunningcuring.ai.controller;

import com.training.dunningcuring.ai.AdminChatAssistant; // <-- Admin Assistant
import com.training.dunningcuring.ai.dto.ChatRequestDto;
import com.training.dunningcuring.ai.dto.ChatResponseDto;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/admin/chat") // <-- Admin-only path
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')") // <-- Admin-only role
public class AdminChatController {

    // Inject the Admin assistant
    private final AdminChatAssistant chatAssistant;
    private final ChatMemoryStore chatMemoryStore;

    @PostMapping
    public ResponseEntity<ChatResponseDto> sendMessage(@RequestBody ChatRequestDto request, Principal principal) {
        try {
            String username = principal.getName();
            String chatId = "admin-" + username; // Separate chat ID for admin

            log.info("Processing ADMIN chat message for user: {}, chatId: {}", username, chatId);

            String response = chatAssistant.chat(chatId, request.getMessage());

            log.info("AI response generated for admin chatId: {}", chatId);
            return ResponseEntity.ok(new ChatResponseDto(response, chatId));

        } catch (Exception e) {
            log.error("Error processing admin chat message for user: " + principal.getName(), e);
            return ResponseEntity.status(500).body(
                    new ChatResponseDto("Sorry, I encountered an error. Please try again.", null)
            );
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHistory(Principal principal) {
        String chatId = "admin-" + principal.getName();
        try {
            chatMemoryStore.deleteMessages(chatId);
            log.info("Cleared chat history for admin chatId={}", chatId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing chat history for admin chatId={}", chatId, e);
            return ResponseEntity.status(500).build();
        }
    }
}