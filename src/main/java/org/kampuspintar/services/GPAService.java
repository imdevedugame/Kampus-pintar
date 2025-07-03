package org.kampuspintar.services;

import org.kampuspintar.database.SupabaseConfig;
import org.kampuspintar.models.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPAService {
    
    private String userId;
    
    public GPAService(String userId) {
        this.userId = userId;
    }
    
    public void addCourse(Course course) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("course_name", course.getName());
            data.put("course_code", course.getCourseCode());
            data.put("credits", course.getCredits());
            data.put("grade_point", course.getGradePoint());
            data.put("letter_grade", course.getLetterGrade());
            data.put("semester", course.getSemester());
            data.put("academic_year", course.getAcademicYear());
            data.put("is_completed", true);
            
            SupabaseConfig.insert("courses", data);
            
        } catch (Exception e) {
            System.err.println("❌ Error menambah mata kuliah: " + e.getMessage());
        }
    }
    
    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        try {
            String filter = "user_id=eq." + userId + "&order=semester.desc,course_name.asc";
            List<Map> results = SupabaseConfig.select("courses", Map.class, filter);
            
            for (Map<String, Object> row : results) {
                Course course = new Course(
                    (String) row.get("id"),
                    (String) row.get("course_name"),
                    (String) row.get("course_code"),
                    ((Number) row.get("credits")).intValue(),
                    ((Number) row.get("grade_point")).doubleValue(),
                    (String) row.get("letter_grade"),
                    (String) row.get("semester"),
                    (String) row.get("academic_year"),
                    (Boolean) row.get("is_completed")
                );
                courses.add(course);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error mengambil mata kuliah: " + e.getMessage());
        }
        
        return courses;
    }
    
    public double getCurrentGPA() {
        try {
            List<Course> courses = getCourses();
            double totalGradePoints = 0.0;
            int totalCredits = 0;
            
            for (Course course : courses) {
                if (course.isCompleted()) {
                    totalGradePoints += course.getGradePoint() * course.getCredits();
                    totalCredits += course.getCredits();
                }
            }
            
            return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
            
        } catch (Exception e) {
            System.err.println("❌ Error menghitung GPA: " + e.getMessage());
            return 0.0;
        }
    }
    
    public int getTotalCredits() {
        try {
            List<Course> courses = getCourses();
            int totalCredits = 0;
            
            for (Course course : courses) {
                if (course.isCompleted()) {
                    totalCredits += course.getCredits();
                }
            }
            
            return totalCredits;
            
        } catch (Exception e) {
            System.err.println("❌ Error menghitung total SKS: " + e.getMessage());
            return 0;
        }
    }
    
    public String getGPAAnalysis() {
        double gpa = getCurrentGPA();
        
        if (gpa >= 3.7) {
            return "Wah keren banget! IPK kamu udah masuk kategori cum laude nih. Pertahankan terus ya, kamu pasti bisa lulus dengan predikat terbaik!";
        } else if (gpa >= 3.3) {
            return "Bagus banget! IPK kamu udah sangat memuaskan. Sedikit lagi bisa mencapai cum laude, semangat terus belajarnya!";
        } else if (gpa >= 3.0) {
            return "IPK kamu udah cukup baik kok. Kalau mau naik lagi, coba fokus ke mata kuliah yang masih bisa diperbaiki nilainya ya!";
        } else if (gpa >= 2.7) {
            return "IPK kamu masih bisa ditingkatkan nih. Jangan khawatir, masih banyak kesempatan untuk memperbaiki nilai. Aku akan bantu kamu buat strategi belajar yang lebih efektif!";
        } else {
            return "Oke, IPK kamu memang perlu perhatian khusus. Tapi jangan menyerah ya! Kita bisa sama-sama bikin rencana belajar yang lebih terstruktur. Aku yakin kamu bisa bangkit!";
        }
    }
}
