package org.kampuspintar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kampuspintar.database.SupabaseConfig;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Test koneksi database
        System.out.println("🔄 Menguji koneksi Supabase...");
        if (SupabaseConfig.testConnection()) {
            System.out.println("✅ Koneksi Supabase berhasil!");
        } else {
            System.err.println("❌ Koneksi Supabase gagal! Aplikasi akan tetap berjalan dengan fitur terbatas.");
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load());
        
        // Apply modern styling
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        
        // Configure stage
        primaryStage.setTitle("🎓 Kampus Pintar Pro - AI Study Assistant");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // Add application icon (optional)
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        } catch (Exception e) {
            System.out.println("Icon tidak ditemukan, menggunakan default");
        }
        
        primaryStage.show();
        primaryStage.centerOnScreen();
        
        // Cleanup on close
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("👋 Aplikasi ditutup. Sampai jumpa!");
            System.exit(0);
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
