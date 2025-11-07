package com.training.dunningcuring.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor; // <-- ADD THIS
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // <-- ADD THIS
public class ChatResponseDto {
    // Renamed "message" to "response" to match the controller
    private String response;
    private String chatId;
}