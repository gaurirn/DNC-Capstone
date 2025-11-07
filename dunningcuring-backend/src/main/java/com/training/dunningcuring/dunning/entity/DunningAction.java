package com.training.dunningcuring.dunning.entity;

public enum DunningAction {
    SEND_SMS,
    SEND_EMAIL,
    NOTIFY_THROTTLE, // <-- ADD THIS NEW ACTION
    THROTTLE_DATA,
    BLOCK_VOICE,
    BLOCK_ALL_SERVICES
}