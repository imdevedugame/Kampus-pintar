package org.kampuspintar.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeminiService {
    
    private static final String API_KEY = "AIzaSyBV2w4uqT6FTrfKEv7FIkunNTz11EVM4hg";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    
    private final HttpClient httpClient;
    private final Gson gson;
    private final List<ConversationMessage> conversationHistory;
    
    // Keywords untuk mendeteksi topik akademik
    private final List<String> academicKeywords = Arrays.asList(
        "kuliah", "kampus", "mahasiswa", "dosen", "tugas", "ujian", "uts", "uas",
        "skripsi", "krs", "semester", "gpa", "ipk", "nilai", "belajar", "study",
        "presentasi", "laporan", "makalah", "praktikum", "akademik", "fakultas",
        "jurusan", "mata kuliah", "matkul", "kredit", "sks", "deadline", "kelas",
        "matematika", "fisika", "kimia", "biologi", "ekonomi", "akuntansi",
        "manajemen", "hukum", "psikologi", "teknik", "informatika", "komputer",
        "pemrograman", "algoritma", "database", "coding", "programming","belajar","kerjakan","bantu","Pelajaran","Matkul","Matakuliah","Prodi","Fakultas"
    );
    
    // Respon untuk topik non-akademik
    private final List<String> nonAcademicResponses = Arrays.asList(
        "Maaf ya, aku lebih fokus membahas hal-hal yang berkaitan dengan kampus dan akademik aja nih. Ada yang mau ditanyakan tentang kuliah atau belajar?",
        "Wah, topik itu di luar keahlianku sih. Aku lebih jago bahas masalah-masalah kampus dan akademik. Gimana kalau kita ngobrol tentang kuliah aja?",
        "Hmm, sepertinya itu bukan bidang yang aku kuasai deh. Aku kan asisten belajar khusus untuk mahasiswa. Ada masalah akademik yang bisa aku bantu?",
        "Maaf, aku cuma bisa bantu urusan kampus dan akademik aja ya. Kalau ada pertanyaan tentang kuliah, tugas, atau belajar, aku siap bantu!",
        "Aku lebih ahli urusan akademik nih. Yuk kita bahas hal-hal yang berkaitan dengan kuliah atau belajar aja. Ada yang lagi bikin bingung?"
    );
    
    public GeminiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.gson = new Gson();
        this.conversationHistory = new ArrayList<>();
        
        addSystemMessage();
    }
    
    private void addSystemMessage() {
        String systemPrompt = "Kamu adalah Kampus Pintar Assistant, teman virtual mahasiswa Indonesia yang sangat membantu dan ramah. " +
                "Kepribadianmu seperti teman sesama mahasiswa yang berpengalaman, supportif, dan punya banyak solusi praktis. " +
                "\n\nKarakteristik komunikasimu:" +
                "- Bicara santai seperti teman sebaya mahasiswa Indonesia, jangan formal" +
                "- Gunakan bahasa gaul yang natural tapi tetap sopan" +
                "- Jangan pakai bullet points atau numbering, ngobrol aja seperti chat biasa" +
                "- Berikan solusi yang praktis dan bisa langsung diterapkan" +
                "- Sharing pengalaman seolah kamu juga pernah mengalami hal serupa" +
                "- Jangan terdengar seperti bot atau AI formal" +
                "- Gunakan kata 'aku', 'kamu', 'nih', 'sih', 'dong', 'ya', 'loh', 'deh'" +
                "- Responsive terhadap emosi user, kalau dia stress kamu empati dan kasih semangat" +
                "- Sesekali pakai emoji yang relevan tapi jangan berlebihan" +
                "\n\nKamu membantu dengan:" +
                "- Masalah akademik seperti tugas, ujian, nilai" +
                "- Tips belajar efektif dan manajemen waktu" +
                "- Masalah dengan dosen, teman, atau organisasi kampus" +
                "- Research, skripsi, dan tugas akhir" +
                "- Kehidupan kampus secara umum" +
                "- Stress akademik dan motivasi belajar" +
                "- Perencanaan karir dan masa depan akademik" +
                "\n\nCara ngobrolmu:" +
                "- Jangan pakai format list atau poin-poin" +
                "- Ngobrol mengalir seperti chat dengan teman" +
                "- Kasih contoh konkret dari pengalaman" +
                "- Tanya balik kalau perlu klarifikasi" +
                "- Kasih semangat dan motivasi yang genuine" +
                "\n\nIngat: Kamu HANYA membahas topik akademik dan kampus. Kalau ditanya hal lain, dengan ramah arahkan kembali ke topik akademik.";
        
        conversationHistory.add(new ConversationMessage("system", systemPrompt));
    }
    
    public String generateResponse(String userMessage) throws IOException, InterruptedException {
        // Cek apakah pesan berkaitan dengan akademik/kampus
        boolean isAcademicTopic = isAcademicRelated(userMessage);
        
        String response;
        if (!isAcademicTopic) {
            // Pilih respons random untuk topik non-akademik
            response = nonAcademicResponses.get((int)(Math.random() * nonAcademicResponses.size()));
        } else {
            // Add user message to history
            conversationHistory.add(new ConversationMessage("user", userMessage));
            
            // Prepare request body
            JsonObject requestBody = createRequestBody();
            
            // Make API call
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "KampusPintar/1.0")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (httpResponse.statusCode() == 200) {
                response = extractResponseText(httpResponse.body());
                
                // Add bot response to history
                conversationHistory.add(new ConversationMessage("assistant", response));
                
                // Keep conversation history manageable (last 20 messages)
                if (conversationHistory.size() > 21) {
                    conversationHistory.subList(1, conversationHistory.size() - 20).clear();
                }
            } else {
                response = "Aduh maaf, lagi ada gangguan sistem nih. Tapi aku masih siap bantu masalah kampus kamu! Coba tanya lagi ya 😊";
            }
        }
        
        return response;
    }
    
    private boolean isAcademicRelated(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Cek apakah ada kata kunci akademik dalam pesan
        for (String keyword : academicKeywords) {
            if (lowerMessage.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        // Cek pola umum pertanyaan akademik
        if ((lowerMessage.contains("bagaimana") || lowerMessage.contains("gimana")) && 
            (lowerMessage.contains("belajar") || lowerMessage.contains("mengerjakan") || 
             lowerMessage.contains("ngerjain") || lowerMessage.contains("bikin") ||
             lowerMessage.contains("kuliah") || lowerMessage.contains("tugas"))) {
            return true;
        }
        
        return false;
    }
    
    private JsonObject createRequestBody() {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        
        // Add conversation history (excluding system message for Gemini)
        for (int i = 1; i < conversationHistory.size(); i++) {
            ConversationMessage msg = conversationHistory.get(i);
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            
            part.addProperty("text", msg.content);
            parts.add(part);
            content.add("parts", parts);
            
            // Map roles for Gemini API
            String role = msg.role.equals("assistant") ? "model" : "user";
            content.addProperty("role", role);
            
            contents.add(content);
        }
        
        requestBody.add("contents", contents);
        
        // Add generation configuration for more human-like responses
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 1.2);
        generationConfig.addProperty("topK", 50);
        generationConfig.addProperty("topP", 0.95);
        generationConfig.addProperty("maxOutputTokens", 2048);
        requestBody.add("generationConfig", generationConfig);
        
        return requestBody;
    }
    
    private String extractResponseText(String responseBody) {
        try {
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);
            
            if (response.has("candidates") && response.getAsJsonArray("candidates").size() > 0) {
                JsonObject candidate = response.getAsJsonArray("candidates").get(0).getAsJsonObject();
                
                if (candidate.has("content")) {
                    JsonObject content = candidate.getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");
                    
                    if (parts.size() > 0) {
                        return parts.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            }
            
            return "Waduh, aku agak bingung nih gimana jawabnya. Coba tanya lagi dengan kata-kata yang berbeda ya!";
            
        } catch (Exception e) {
            return "Oops, ada gangguan sistem nih! Tapi aku masih siap bantu masalah kampus kamu 😊";
        }
    }
    
    public void clearHistory() {
        conversationHistory.clear();
        addSystemMessage();
    }
    
    public int getHistorySize() {
        return conversationHistory.size() - 1; // Exclude system message
    }
    
    // Inner class for conversation messages
    private static class ConversationMessage {
        final String role;
        final String content;
        
        ConversationMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
