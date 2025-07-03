package org.kampuspintar.models;

import java.time.LocalDateTime;

public class ChatMessage {
    private String userMessage;
    private String aiResponse;
    private boolean isAcademicTopic;
    private LocalDateTime timestamp;
    
    public ChatMessage(String userMessage, String aiResponse, boolean isAcademicTopic, LocalDateTime timestamp) {
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.isAcademicTopic = isAcademicTopic;
        this.timestamp = timestamp;
    }
    
    // Getters and setters
    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
    
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    
    public boolean isAcademicTopic() { return isAcademicTopic; }
    public void setAcademicTopic(boolean academicTopic) { isAcademicTopic = academicTopic; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
