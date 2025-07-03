package org.kampuspintar.models;

public class Course {
    private String id;
    private String name;
    private String courseCode;
    private int credits;
    private double gradePoint;
    private String letterGrade;
    private String semester;
    private String academicYear;
    private boolean isCompleted;
    
    public Course(String name, int credits, double gradePoint) {
        this.name = name;
        this.credits = credits;
        this.gradePoint = gradePoint;
        this.letterGrade = calculateLetterGrade(gradePoint);
        this.semester = "Current";
        this.academicYear = "2024/2025";
        this.isCompleted = true;
    }
    
    public Course(String id, String name, String courseCode, int credits, double gradePoint, 
                  String letterGrade, String semester, String academicYear, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.credits = credits;
        this.gradePoint = gradePoint;
        this.letterGrade = letterGrade;
        this.semester = semester;
        this.academicYear = academicYear;
        this.isCompleted = isCompleted;
    }
    
    private String calculateLetterGrade(double gradePoint) {
        if (gradePoint >= 4.0) return "A";
        if (gradePoint >= 3.7) return "A-";
        if (gradePoint >= 3.3) return "B+";
        if (gradePoint >= 3.0) return "B";
        if (gradePoint >= 2.7) return "B-";
        if (gradePoint >= 2.3) return "C+";
        if (gradePoint >= 2.0) return "C";
        if (gradePoint >= 1.7) return "C-";
        if (gradePoint >= 1.3) return "D+";
        if (gradePoint >= 1.0) return "D";
        return "F";
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public double getGradePoint() { return gradePoint; }
    public void setGradePoint(double gradePoint) { 
        this.gradePoint = gradePoint;
        this.letterGrade = calculateLetterGrade(gradePoint);
    }
    
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    @Override
    public String toString() {
        return name + " (" + credits + " SKS) - " + letterGrade + " (" + gradePoint + ")";
    }
}
