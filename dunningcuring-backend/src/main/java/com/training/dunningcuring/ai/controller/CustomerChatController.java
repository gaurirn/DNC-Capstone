package com.training.dunningcuring.ai.controller;

import com.training.dunningcuring.ai.DunningChatAssistant;
import com.training.dunningcuring.ai.dto.ChatRequestDto;
import com.training.dunningcuring.ai.dto.ChatResponseDto;
import dev.langchain4j.store.memory.chat.ChatMemoryStore; // <-- NEW IMPORT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // <-- NEW IMPORT

@RestController
@RequestMapping("/api/me/chat") // <-- Use the customer-specific /api/me path
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_CUSTOMER')") // <-- Use our project's role
public class CustomerChatController {

    private final DunningChatAssistant chatAssistant;
    private final ChatMemoryStore chatMemoryStore; // <-- Inject memory store

    /**
     * Sends a message to the AI assistant.
     * The Chat ID is securely generated from the logged-in user's name.
     */
    @PostMapping
    public ResponseEntity<ChatResponseDto> sendMessage(@RequestBody ChatRequestDto request, Principal principal) {
        try {
            // --- THIS IS THE FIX ---
            // The Chat ID MUST be tied to the authenticated user.
            String username = principal.getName();
            String chatId = "customer-" + username;
            // --- END OF FIX ---

            log.info("Processing chat message for user: {}, chatId: {}", username, chatId);

            // Call the AI
            String response = chatAssistant.chat(chatId, request.getMessage());

            log.info("AI response generated for chatId: {}", chatId);
            return ResponseEntity.ok(new ChatResponseDto(response, chatId));

        } catch (Exception e) {
            log.error("Error processing chat message for user: " + principal.getName(), e);
            return ResponseEntity.status(500).body(
                    new ChatResponseDto("Sorry, I encountered an error. Please try again.", null)
            );
        }
    }

    /**
     * Clears the chat history for the currently logged-in user.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHistory(Principal principal) {
        String chatId = "customer-" + principal.getName();
        try {
            chatMemoryStore.deleteMessages(chatId);
            log.info("Cleared chat history for chatId={}", chatId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing chat history for chatId={}", chatId, e);
            return ResponseEntity.status(500).build();
        }
    }
}