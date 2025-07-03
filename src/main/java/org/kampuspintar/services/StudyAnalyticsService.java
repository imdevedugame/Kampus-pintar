package org.kampuspintar.services;

import org.kampuspintar.database.SupabaseConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyAnalyticsService {
    
    private String userId;
    private LocalDateTime currentSessionStart;
    private String currentSessionId;
    
    public StudyAnalyticsService(String userId) {
        this.userId = userId;
    }
    
    public void startStudySession(String subject) {
        currentSessionStart = LocalDateTime.now();
        currentSessionId = java.util.UUID.randomUUID().toString();
        
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("id", currentSessionId);
            data.put("user_id", userId);
            data.put("subject", subject);
            data.put("start_time", currentSessionStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            data.put("session_type", "pomodoro");
            data.put("completed", false);
            
            SupabaseConfig.insert("study_sessions", data);
            
        } catch (Exception e) {
            System.err.println("❌ Error memulai sesi belajar: " + e.getMessage());
        }
    }
    
    public void endStudySession(String notes) {
        if (currentSessionStart == null || currentSessionId == null) return;
        
        LocalDateTime endTime = LocalDateTime.now();
        int durationMinutes = (int) java.time.Duration.between(currentSessionStart, endTime).toMinutes();
        
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("end_time", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            data.put("duration_minutes", durationMinutes);
            data.put("notes", notes);
            data.put("completed", true);
            
            String filter = "id=eq." + currentSessionId + "&user_id=eq." + userId;
            SupabaseConfig.update("study_sessions", filter, data);
            
            // Update daily analytics
            updateDailyAnalytics(LocalDate.now(), durationMinutes);
            
        } catch (Exception e) {
            System.err.println("❌ Error mengakhiri sesi belajar: " + e.getMessage());
        }
        
        currentSessionStart = null;
        currentSessionId = null;
    }
    
    private void updateDailyAnalytics(LocalDate date, int studyMinutes) {
        try {
            // Cek apakah sudah ada data untuk hari ini
            String filter = "user_id=eq." + userId + "&date=eq." + date.toString();
            List<Map> existing = SupabaseConfig.select("study_analytics", Map.class, filter);
            
            if (existing.isEmpty()) {
                // Insert data baru
                Map<String, Object> data = new HashMap<>();
                data.put("user_id", userId);
                data.put("date", date.toString());
                data.put("study_time_minutes", studyMinutes);
                data.put("pomodoro_sessions", 1);
                data.put("subject", "Umum");
                
                SupabaseConfig.insert("study_analytics", data);
            } else {
                // Update data yang sudah ada
                Map<String, Object> existingData = existing.get(0);
                int currentTime = ((Number) existingData.get("study_time_minutes")).intValue();
                int currentSessions = ((Number) existingData.get("pomodoro_sessions")).intValue();
                
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("study_time_minutes", currentTime + studyMinutes);
                updateData.put("pomodoro_sessions", currentSessions + 1);
                
                SupabaseConfig.update("study_analytics", filter, updateData);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error update analitik harian: " + e.getMessage());
        }
    }
    
    public int getTodayStudyTime() {
        try {
            String today = LocalDate.now().toString();
            String filter = "user_id=eq." + userId + "&date=eq." + today;
            List<Map> results = SupabaseConfig.select("study_analytics", Map.class, filter);
            
            int totalTime = 0;
            for (Map<String, Object> row : results) {
                totalTime += ((Number) row.get("study_time_minutes")).intValue();
            }
            
            return totalTime;
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil waktu belajar hari ini: " + e.getMessage());
            return 0;
        }
    }
    
    public int getWeeklyStudyTime() {
        try {
            LocalDate weekAgo = LocalDate.now().minusDays(7);
            String filter = "user_id=eq." + userId + "&date=gte." + weekAgo.toString();
            List<Map> results = SupabaseConfig.select("study_analytics", Map.class, filter);
            
            int totalTime = 0;
            for (Map<String, Object> row : results) {
                totalTime += ((Number) row.get("study_time_minutes")).intValue();
            }
            
            return totalTime;
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil waktu belajar mingguan: " + e.getMessage());
            return 0;
        }
    }
    
    public List<Integer> getWeeklyData() {
        List<Integer> weeklyData = new ArrayList<>();
        
        try {
            LocalDate today = LocalDate.now();
            
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String filter = "user_id=eq." + userId + "&date=eq." + date.toString();
                List<Map> results = SupabaseConfig.select("study_analytics", Map.class, filter);
                
                int dailyTime = 0;
                for (Map<String, Object> row : results) {
                    dailyTime += ((Number) row.get("study_time_minutes")).intValue();
                }
                
                weeklyData.add(dailyTime);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil data mingguan: " + e.getMessage());
            // Return default data if error
            for (int i = 0; i < 7; i++) {
                weeklyData.add(0);
            }
        }
        
        return weeklyData;
    }
    
    public Map<String, Integer> getSubjectDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        
        try {
            LocalDate monthAgo = LocalDate.now().minusDays(30);
            String filter = "user_id=eq." + userId + "&date=gte." + monthAgo.toString();
            List<Map> results = SupabaseConfig.select("study_analytics", Map.class, filter);
            
            for (Map<String, Object> row : results) {
                String subject = (String) row.get("subject");
                if (subject == null) subject = "Umum";
                
                int time = ((Number) row.get("study_time_minutes")).intValue();
                distribution.merge(subject, time, Integer::sum);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil distribusi mata pelajaran: " + e.getMessage());
        }
        
        return distribution;
    }
    
    public String generateWeeklyReport() {
        int weeklyTime = getWeeklyStudyTime();
        int todayTime = getTodayStudyTime();
        int weeklyGoal = 1200; // 20 jam per minggu
        
        double weeklyProgress = (double) weeklyTime / weeklyGoal * 100;
        
        StringBuilder report = new StringBuilder();
        report.append("Halo! Ini laporan belajar kamu minggu ini nih.\n\n");
        
        report.append("Kamu udah belajar total ").append(weeklyTime).append(" menit minggu ini. ");
        if (weeklyProgress >= 100) {
            report.append("Wah keren banget! Kamu udah melebihi target mingguan loh! ");
        } else if (weeklyProgress >= 80) {
            report.append("Bagus banget! Tinggal sedikit lagi buat mencapai target mingguan. ");
        } else if (weeklyProgress >= 60) {
            report.append("Lumayan nih, tapi masih bisa ditingkatkan lagi. ");
        } else {
            report.append("Hmm, sepertinya minggu ini agak santai ya? ");
        }
        
        report.append("Hari ini kamu udah belajar ").append(todayTime).append(" menit.\n\n");
        
        // Tambahkan motivasi berdasarkan performa
        if (weeklyProgress >= 100) {
            report.append("Kamu udah jadi role model buat mahasiswa lain nih!");
        } else if (weeklyProgress >= 80) {
            report.append("Sedikit lagi buat jadi mahasiswa teladan!");
        } else {
            report.append("Aku yakin minggu depan kamu bisa lebih baik lagi!");
        }
        
        return report.toString();
    }
}
