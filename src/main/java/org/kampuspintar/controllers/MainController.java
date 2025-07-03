package org.kampuspintar.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.kampuspintar.services.*;
import org.kampuspintar.models.*;

import java.net.URL;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.List;

public class MainController implements Initializable {

    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    // Tab Components
    @FXML private TabPane mainTabPane;
    
    // Dashboard Components
    @FXML private Label todayStudyTimeLabel;
    @FXML private Label currentGPALabel;
    @FXML private ProgressBar weeklyProgressBar;
    @FXML private Label weeklyGoalLabel;
    
    // Todo Components
    @FXML private TextField newTodoField;
    @FXML private Button addTodoButton;
    @FXML private ListView<TodoItem> todoListView;
    
    // Pomodoro Components
    @FXML private Label pomodoroTimeLabel;
    @FXML private Button startPomodoroButton;
    @FXML private Button pausePomodoroButton;
    @FXML private Button resetPomodoroButton;
    @FXML private ProgressBar pomodoroProgressBar;
    @FXML private Label pomodoroSessionLabel;
    
    // GPA Components
    @FXML private TextField courseNameField;
    @FXML private TextField creditsField;
    @FXML private ComboBox<String> gradeComboBox;
    @FXML private Button addCourseButton;
    @FXML private ListView<Course> courseListView;
    @FXML private Label gpaCalculatorLabel;
    @FXML private Label totalCreditsLabel;
    
    // Analytics Components
    @FXML private LineChart<String, Number> studyChart;
    @FXML private PieChart subjectChart;
    @FXML
    private void clearChat(ActionEvent event) {
        // Logika untuk membersihkan chat
        System.out.println("Chat dibersihkan.");
    }

    @FXML
    private void showAbout(ActionEvent event) {
        // Logika untuk menampilkan informasi "Tentang"
        System.out.println("Menampilkan info tentang aplikasi.");
    }
    // Services
    private GeminiService geminiService;
    private TodoService todoService;
    private PomodoroService pomodoroService;
    private GPAService gpaService;
    private StudyAnalyticsService analyticsService;
    private ChatHistoryService chatHistoryService;
    
    private boolean isProcessing = false;
    private String currentUserId = "550e8400-e29b-41d4-a716-446655440000"; // Demo user ID

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupUI();
        setupComponents();
        loadInitialData();
        addWelcomeMessage();
    }
    
    private void initializeServices() {
        try {
            geminiService = new GeminiService();
            todoService = new TodoService(currentUserId);
            pomodoroService = new PomodoroService();
            gpaService = new GPAService(currentUserId);
            analyticsService = new StudyAnalyticsService(currentUserId);
            chatHistoryService = new ChatHistoryService(currentUserId);
            
            System.out.println("✅ Semua services berhasil diinisialisasi");
            
        } catch (Exception e) {
            System.err.println("❌ Error inisialisasi services: " + e.getMessage());
            showAlert("Error", "Gagal menginisialisasi services. Beberapa fitur mungkin tidak berfungsi.");
        }
    }

    private void setupUI() {
        // Configure chat area
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatContainer.setSpacing(15);
        chatContainer.setPadding(new Insets(20));

        // Configure input
        messageInput.setOnAction(e -> sendMessage());
        messageInput.textProperty().addListener((obs, oldText, newText) -> {
            sendButton.setDisable(newText.trim().isEmpty() || isProcessing);
        });
        sendButton.setOnAction(e -> sendMessage());
        sendButton.setDisable(true);

        // Hide loading indicator initially
        loadingIndicator.setVisible(false);
        updateStatus("Siap membantu dengan AI yang pintar! 🤖");
    }
    
    private void setupComponents() {
        setupTodoList();
        setupPomodoroTimer();
        setupGPACalculator();
        setupAnalytics();
    }
    
    private void setupTodoList() {
        addTodoButton.setOnAction(e -> addTodoItem());
        newTodoField.setOnAction(e -> addTodoItem());
        refreshTodoList();
    }
    
    private void setupPomodoroTimer() {
        pomodoroTimeLabel.setText("25:00");
        pomodoroSessionLabel.setText("Sesi 1 dari 4");
        pomodoroProgressBar.setProgress(0.0);
        
        startPomodoroButton.setOnAction(e -> startPomodoro());
        pausePomodoroButton.setOnAction(e -> pausePomodoro());
        resetPomodoroButton.setOnAction(e -> resetPomodoro());
        
        pomodoroService.setOnTimeUpdate(this::updatePomodoroDisplay);
        pomodoroService.setOnSessionComplete(this::onPomodoroComplete);
    }
    
    private void setupGPACalculator() {
        gradeComboBox.setItems(FXCollections.observableArrayList(
            "A (4.0)", "A- (3.7)", "B+ (3.3)", "B (3.0)", "B- (2.7)",
            "C+ (2.3)", "C (2.0)", "C- (1.7)", "D+ (1.3)", "D (1.0)", "F (0.0)"
        ));
        
        addCourseButton.setOnAction(e -> addCourse());
        refreshGPADisplay();
    }
    
    private void setupAnalytics() {
        updateAnalyticsDisplay();
    }
    
    private void loadInitialData() {
        refreshTodoList();
        refreshGPADisplay();
        updateAnalyticsDisplay();
    }
    
    private void addWelcomeMessage() {
        String welcomeText = "Halo! Aku Kampus Pintar Pro, asisten belajar AI kamu! 🎓\n\n" +
                "Aku siap bantu kamu dengan:\n" +
                "💬 Chat AI yang fokus akademik\n" +
                "📝 Manajemen tugas dan deadline\n" +
                "🍅 Timer Pomodoro untuk belajar efektif\n" +
                "📊 Kalkulator dan analisis IPK\n" +
                "📈 Tracking progress belajar\n\n" +
                "Semua data kamu tersimpan aman di cloud dan bisa diakses kapan aja. " +
                "Yuk mulai ngobrol atau coba fitur-fitur canggih di tab sebelah! 😊";
        addBotMessage(welcomeText);
    }

    @FXML
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty() || isProcessing) return;

        addUserMessage(message);
        messageInput.clear();
        setProcessingState(true);

        // Check for special commands
        if (handleSpecialCommands(message)) {
            setProcessingState(false);
            return;
        }

        Task<String> apiTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return geminiService.generateResponse(message);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    String response = getValue();
                    addBotMessage(response);
                    
                    // Save to chat history
                    chatHistoryService.saveChatMessage(message, response, true);
                    
                    setProcessingState(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    addBotMessage("Aduh maaf, lagi ada gangguan koneksi nih. Coba lagi ya 😊");
                    setProcessingState(false);
                });
            }
        };

        new Thread(apiTask).start();
    }
    
    private boolean handleSpecialCommands(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("mulai pomodoro") || lowerMessage.contains("start pomodoro")) {
            startPomodoro();
            addBotMessage("Oke! Timer Pomodoro udah dimulai! 25 menit focus time ya! 🍅⏰");
            return true;
        }
        
        if (lowerMessage.contains("laporan belajar") || lowerMessage.contains("analytics")) {
            showAnalyticsReport();
            return true;
        }
        
        if (lowerMessage.contains("hapus chat") || lowerMessage.contains("clear chat")) {
            clearChat();
            return true;
        }
        
        return false;
    }

    // Todo Methods
    private void addTodoItem() {
        String task = newTodoField.getText().trim();
        if (!task.isEmpty()) {
            TodoItem todo = new TodoItem(task, LocalDate.now().plusDays(1));
            todoService.addTodo(todo);
            refreshTodoList();
            newTodoField.clear();
            
            addBotMessage("Oke, tugas \"" + task + "\" udah aku simpan! Jangan lupa dikerjain ya! 📝✅");
        }
    }
    
    private void refreshTodoList() {
        try {
            List<TodoItem> todos = todoService.getTodos();
            todoListView.setItems(FXCollections.observableArrayList(todos));
        } catch (Exception e) {
            System.err.println("❌ Error loading todos: " + e.getMessage());
        }
    }

    // Pomodoro Methods
    @FXML
    private void startPomodoro() {
        try {
            analyticsService.startStudySession("Sesi Pomodoro");
            pomodoroService.start();
            startPomodoroButton.setDisable(true);
            pausePomodoroButton.setDisable(false);
        } catch (Exception e) {
            System.err.println("❌ Error starting pomodoro: " + e.getMessage());
        }
    }
    
    private void pausePomodoro() {
        pomodoroService.pause();
        startPomodoroButton.setDisable(false);
        pausePomodoroButton.setDisable(true);
    }
    
    private void resetPomodoro() {
        pomodoroService.reset();
        startPomodoroButton.setDisable(false);
        pausePomodoroButton.setDisable(true);
        updatePomodoroDisplay(25 * 60, 1);
    }
    
    private void updatePomodoroDisplay(int secondsLeft, int session) {
        Platform.runLater(() -> {
            int minutes = secondsLeft / 60;
            int seconds = secondsLeft % 60;
            pomodoroTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
            pomodoroSessionLabel.setText("Sesi " + session + " dari 4");
            pomodoroProgressBar.setProgress(1.0 - (double) secondsLeft / (25 * 60));
        });
    }
    
    private void onPomodoroComplete(int session) {
        Platform.runLater(() -> {
            try {
                analyticsService.endStudySession("Selesai sesi pomodoro " + session);
                updateAnalyticsDisplay();
                
                if (session % 4 == 0) {
                    addBotMessage("Wah keren! Kamu udah selesai 4 sesi Pomodoro! 🎉 Saatnya long break 15-30 menit. Kamu deserve it! 💪");
                } else {
                    addBotMessage("Great job! Sesi " + session + " selesai! 🍅 Break 5 menit dulu ya, terus lanjut sesi berikutnya! 😊");
                }
            } catch (Exception e) {
                System.err.println("❌ Error completing pomodoro: " + e.getMessage());
            }
        });
    }

    // GPA Methods
    private void addCourse() {
        String courseName = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        String gradeText = gradeComboBox.getValue();
        
        if (!courseName.isEmpty() && !creditsText.isEmpty() && gradeText != null) {
            try {
                int credits = Integer.parseInt(creditsText);
                double gradePoint = parseGradePoint(gradeText);
                
                Course course = new Course(courseName, credits, gradePoint);
                gpaService.addCourse(course);
                
                courseNameField.clear();
                creditsField.clear();
                gradeComboBox.setValue(null);
                
                refreshGPADisplay();
                
                double newGPA = gpaService.getCurrentGPA();
                String analysis = gpaService.getGPAAnalysis();
                
                addBotMessage("Mata kuliah \"" + courseName + "\" udah ditambah! IPK kamu sekarang: " + 
                           String.format("%.2f", newGPA) + ". " + analysis + " 📊");
                
            } catch (NumberFormatException e) {
                showAlert("Error", "SKS harus berupa angka!");
            } catch (Exception e) {
                System.err.println("❌ Error adding course: " + e.getMessage());
                showAlert("Error", "Gagal menambah mata kuliah. Coba lagi ya!");
            }
        }
    }
    
    private double parseGradePoint(String gradeText) {
        return Double.parseDouble(gradeText.substring(gradeText.indexOf("(") + 1, gradeText.indexOf(")")));
    }
    
    private void refreshGPADisplay() {
        try {
            double gpa = gpaService.getCurrentGPA();
            int totalCredits = gpaService.getTotalCredits();
            
            currentGPALabel.setText(String.format("%.2f", gpa));
            gpaCalculatorLabel.setText(String.format("%.2f", gpa));
            totalCreditsLabel.setText(String.valueOf(totalCredits));
            
            // Load courses
            List<Course> courses = gpaService.getCourses();
            courseListView.setItems(FXCollections.observableArrayList(courses));
            
        } catch (Exception e) {
            System.err.println("❌ Error loading GPA data: " + e.getMessage());
        }
    }

    // Analytics Methods
    private void updateAnalyticsDisplay() {
        try {
            int todayTime = analyticsService.getTodayStudyTime();
            int weeklyTime = analyticsService.getWeeklyStudyTime();
            int weeklyGoal = 1200; // 20 hours
            
            todayStudyTimeLabel.setText(todayTime + " menit");
            weeklyGoalLabel.setText("Target: " + weeklyGoal + " menit");
            
            double progress = (double) weeklyTime / weeklyGoal;
            weeklyProgressBar.setProgress(Math.min(progress, 1.0));
            
            updateStudyChart();
            updateSubjectChart();
            
        } catch (Exception e) {
            System.err.println("❌ Error updating analytics: " + e.getMessage());
        }
    }
    
    private void updateStudyChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Waktu Belajar (menit)");
            
            List<Integer> weeklyData = analyticsService.getWeeklyData();
            String[] days = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
            
            for (int i = 0; i < days.length && i < weeklyData.size(); i++) {
                series.getData().add(new XYChart.Data<>(days[i], weeklyData.get(i)));
            }
            
            if (studyChart != null) {
                studyChart.getData().clear();
                studyChart.getData().add(series);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error updating study chart: " + e.getMessage());
        }
    }
    
    private void updateSubjectChart() {
        try {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            analyticsService.getSubjectDistribution().forEach((subject, time) -> {
                if (time > 0) {
                    pieChartData.add(new PieChart.Data(subject, time));
                }
            });
            
            if (subjectChart != null) {
                subjectChart.setData(pieChartData);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error updating subject chart: " + e.getMessage());
        }
    }
    
    private void showAnalyticsReport() {
        try {
            String report = analyticsService.generateWeeklyReport();
            addBotMessage(report);
        } catch (Exception e) {
            addBotMessage("Error pas generate laporan analytics. Coba lagi ya!");
        }
    }

    // UI Helper Methods
    private void addUserMessage(String message) {
        VBox messageContainer = createMessageContainer(message, true);
        addMessageWithAnimation(messageContainer);
    }

    private void addBotMessage(String message) {
        VBox messageContainer = createMessageContainer(message, false);
        addMessageWithAnimation(messageContainer);
    }

    private VBox createMessageContainer(String message, boolean isUser) {
        VBox container = new VBox(5);
        container.setMaxWidth(500);

        VBox messageBubble = new VBox(8);
        messageBubble.setPadding(new Insets(12, 16, 12, 16));

        TextFlow textFlow = new TextFlow();
        Text messageText = new Text(message);
        messageText.getStyleClass().add(isUser ? "user-message-text" : "bot-message-text");
        textFlow.getChildren().add(messageText);

        Label timestamp = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timestamp.getStyleClass().add("timestamp");

        messageBubble.getChildren().addAll(textFlow, timestamp);

        if (isUser) {
            messageBubble.getStyleClass().add("user-message-bubble");
            container.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBubble.getStyleClass().add("bot-message-bubble");
            container.setAlignment(Pos.CENTER_LEFT);
        }

        container.getChildren().add(messageBubble);
        return container;
    }

    private void addMessageWithAnimation(VBox messageContainer) {
        messageContainer.setOpacity(0);
        messageContainer.setScaleX(0.8);
        messageContainer.setScaleY(0.8);

        chatContainer.getChildren().add(messageContainer);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), messageContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), messageContainer);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();

        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    private void setProcessingState(boolean processing) {
        isProcessing = processing;
        sendButton.setDisable(processing || messageInput.getText().trim().isEmpty());
        loadingIndicator.setVisible(processing);

        if (processing) {
            updateStatus("AI sedang berpikir...");
            messageInput.setPromptText("Tunggu, AI lagi mikir keras...");
        } else {
            updateStatus("Siap membantu dengan AI yang pintar! 🤖");
            messageInput.setPromptText("Tanya apa aja tentang kuliah dan belajar...");
            messageInput.requestFocus();
        }
    }

    private void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearChat() {
        chatContainer.getChildren().clear();
        addWelcomeMessage();
        updateStatus("Chat baru dimulai!");
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tentang Kampus Pintar Pro");
        alert.setHeaderText("Kampus Pintar Pro - AI Study Assistant v1.0");
        alert.setContentText("🤖 AI-powered study companion dengan database real-time\n" +
                "🗄️ Supabase untuk penyimpanan data cloud\n" +
                "📊 Real-time analytics dan progress tracking\n" +
                "💾 Chat history dan data persistence\n" +
                "🔄 Sinkronisasi otomatis semua fitur\n" +
                "🚀 Fitur-fitur canggih untuk produktivitas maksimal\n\n" +
                "Developed with ❤️ untuk masa depan pendidikan yang lebih smart!");
        alert.showAndWait();
    }
}
