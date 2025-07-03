package org.kampuspintar.services;

import org.kampuspintar.database.SupabaseConfig;
import org.kampuspintar.models.TodoItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoService {
    
    private String userId;
    
    public TodoService(String userId) {
        this.userId = userId;
    }
    
    public void addTodo(TodoItem todo) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("task", todo.getTask());
            data.put("description", todo.getDescription());
            data.put("due_date", todo.getDueDate().toString());
            data.put("priority", todo.getPriority());
            data.put("category", todo.getCategory());
            data.put("completed", false);
            
            SupabaseConfig.insert("todo_items", data);
            
        } catch (Exception e) {
            System.err.println("❌ Error menambah todo: " + e.getMessage());
        }
    }
    
    public List<TodoItem> getTodos() {
        List<TodoItem> todos = new ArrayList<>();
        try {
            String filter = "user_id=eq." + userId + "&order=due_date.asc";
            List<Map> results = SupabaseConfig.select("todo_items", Map.class, filter);
            
            for (Map<String, Object> row : results) {
                TodoItem todo = new TodoItem(
                    (String) row.get("id"),
                    (String) row.get("task"),
                    (String) row.get("description"),
                    LocalDate.parse((String) row.get("due_date")),
                    (String) row.get("priority"),
                    (String) row.get("category"),
                    (Boolean) row.get("completed")
                );
                todos.add(todo);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil todos: " + e.getMessage());
        }
        
        return todos;
    }
    
    public void markTodoCompleted(String todoId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("completed", true);
            data.put("completed_at", LocalDate.now().toString());
            
            String filter = "id=eq." + todoId + "&user_id=eq." + userId;
            SupabaseConfig.update("todo_items", filter, data);
            
        } catch (Exception e) {
            System.err.println("❌ Error menandai todo selesai: " + e.getMessage());
        }
    }
    
    public void deleteTodo(String todoId) {
        try {
            String filter = "id=eq." + todoId + "&user_id=eq." + userId;
            SupabaseConfig.delete("todo_items", filter);
            
        } catch (Exception e) {
            System.err.println("❌ Error menghapus todo: " + e.getMessage());
        }
    }
    
    public int getTodoStats(String status) {
        try {
            String filter = "user_id=eq." + userId;
            
            switch (status) {
                case "completed":
                    filter += "&completed=eq.true";
                    break;
                case "pending":
                    filter += "&completed=eq.false";
                    break;
                case "overdue":
                    String today = LocalDate.now().toString();
                    filter += "&due_date=lt." + today + "&completed=eq.false";
                    break;
            }
            
            List<Map> results = SupabaseConfig.select("todo_items", Map.class, filter);
            return results.size();
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil statistik todo: " + e.getMessage());
            return 0;
        }
    }
}
