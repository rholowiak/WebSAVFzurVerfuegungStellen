package com.example.demo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Message(String text, String color, boolean showBackButton, LocalDateTime timestamp) {

    // Method to get formatted timestamp
    public String formattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return timestamp.format(formatter);
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public String toString() {
        return "Message{text='%s', color='%s', showBackButton=%s, timestamp='%s'}"
                .formatted(text, color, showBackButton, formattedTimestamp());
    }
}
