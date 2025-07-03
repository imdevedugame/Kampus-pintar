package org.kampuspintar.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoItem {
    private String id;
    private String task;
    private String description;
    private LocalDate dueDate;
    private String priority;
    private String category;
    private boolean completed;
    
    public TodoItem(String task, LocalDate dueDate) {
        this.task = task;
        this.dueDate = dueDate;
        this.priority = "medium";
        this.category = "academic";
        this.completed = false;
    }
    
    public TodoItem(String id, String task, String description, LocalDate dueDate, String priority, String category, boolean completed) {
        this.id = id;
        this.task = task;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.completed = completed;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    @Override
    public String toString() {
        String status = completed ? "✅" : "⏳";
        String priorityIcon = switch (priority.toLowerCase()) {
            case "high" -> "🔴";
            case "medium" -> "🟡";
            case "low" -> "🟢";
            default -> "⚪";
        };
        return status + " " + priorityIcon + " " + task + " (Due: " + dueDate.format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
    }
}
