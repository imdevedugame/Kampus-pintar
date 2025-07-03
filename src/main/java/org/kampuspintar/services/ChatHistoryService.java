package org.kampuspintar.services;

import org.kampuspintar.database.SupabaseConfig;
import org.kampuspintar.models.ChatMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatHistoryService {
    
    private String currentUserId;
    private String currentSessionId;
    
    public ChatHistoryService(String userId) {
        this.currentUserId = userId;
        this.currentSessionId = java.util.UUID.randomUUID().toString();
    }
    
    public void saveChatMessage(String userMessage, String aiResponse, boolean isAcademicTopic) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("user_id", currentUserId);
            data.put("message", userMessage);
            data.put("response", aiResponse);
            data.put("is_academic_topic", isAcademicTopic);
            data.put("session_id", currentSessionId);
            data.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            SupabaseConfig.insert("chat_history", data);
            
        } catch (Exception e) {
            System.err.println("❌ Error menyimpan chat history: " + e.getMessage());
        }
    }
    
    public List<ChatMessage> getChatHistory(int limit) {
        List<ChatMessage> messages = new ArrayList<>();
        try {
            String filter = "user_id=eq." + currentUserId + "&order=created_at.desc&limit=" + limit;
            List<Map> results = SupabaseConfig.select("chat_history", Map.class, filter);
            
            for (Map<String, Object> row : results) {
                ChatMessage msg = new ChatMessage(
                    (String) row.get("message"),
                    (String) row.get("response"),
                    (Boolean) row.get("is_academic_topic"),
                    LocalDateTime.parse((String) row.get("created_at"))
                );
                messages.add(msg);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil chat history: " + e.getMessage());
        }
        
        return messages;
    }
    
    public void clearChatHistory() {
        try {
            String filter = "user_id=eq." + currentUserId;
            SupabaseConfig.delete("chat_history", filter);
            System.out.println("🗑️ Chat history berhasil dihapus");
            
        } catch (Exception e) {
            System.err.println("❌ Error menghapus chat history: " + e.getMessage());
        }
    }
    
    public void startNewSession() {
        this.currentSessionId = java.util.UUID.randomUUID().toString();
    }
}
