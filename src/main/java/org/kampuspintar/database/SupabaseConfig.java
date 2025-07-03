package org.kampuspintar.database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SupabaseConfig {
    
    // Konfigurasi Supabase REST API
    private static final String SUPABASE_URL = "https://xtverbkbkjpflpmiiccd.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inh0dmVyYmtia2pwZmxwbWlpY2NkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5MjQ0NzQsImV4cCI6MjA2NDUwMDQ3NH0.FkQ8SuFkGrorl-oLzN0Hoi75Qxb0LaDptoF48Et8PUU";
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    private static final Gson gson = new Gson();
    
    // GET request
    public static <T> List<T> select(String table, Class<T> clazz) throws IOException, InterruptedException {
        return select(table, clazz, null);
    }
    
    public static <T> List<T> select(String table, Class<T> clazz, String filter) throws IOException, InterruptedException {
        String url = SUPABASE_URL + "/rest/v1/" + table;
        if (filter != null && !filter.isEmpty()) {
            url += "?" + filter;
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), TypeToken.getParameterized(List.class, clazz).getType());
        } else {
            System.err.println("❌ Supabase select error: " + response.statusCode() + " - " + response.body());
            throw new IOException("Supabase request failed: " + response.statusCode());
        }
    }
    
    // POST request (INSERT)
    public static JsonObject insert(String table, Object data) throws IOException, InterruptedException {
        String url = SUPABASE_URL + "/rest/v1/" + table;
        String jsonData = gson.toJson(data);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            JsonArray array = gson.fromJson(response.body(), JsonArray.class);
            return array.size() > 0 ? array.get(0).getAsJsonObject() : new JsonObject();
        } else {
            System.err.println("❌ Supabase insert error: " + response.statusCode() + " - " + response.body());
            throw new IOException("Supabase insert failed: " + response.statusCode());
        }
    }
    
    // PATCH request (UPDATE)
    public static void update(String table, String filter, Object data) throws IOException, InterruptedException {
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + filter;
        String jsonData = gson.toJson(data);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 204) {
            System.err.println("❌ Supabase update error: " + response.statusCode() + " - " + response.body());
            throw new IOException("Supabase update failed: " + response.statusCode());
        }
    }
    
    // DELETE request
    public static void delete(String table, String filter) throws IOException, InterruptedException {
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + filter;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .DELETE()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 204) {
            System.err.println("❌ Supabase delete error: " + response.statusCode() + " - " + response.body());
            throw new IOException("Supabase delete failed: " + response.statusCode());
        }
    }
    
    // Test koneksi
    public static boolean testConnection() {
        try {
            String url = SUPABASE_URL + "/rest/v1/";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", SUPABASE_ANON_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
            
        } catch (Exception e) {
            System.err.println("❌ Test koneksi Supabase gagal: " + e.getMessage());
            return false;
        }
    }
}
