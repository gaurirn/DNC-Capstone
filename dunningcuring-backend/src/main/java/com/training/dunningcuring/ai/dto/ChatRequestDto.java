package com.training.dunningcuring.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {
    // We only need the message from the user
    private String message;
}