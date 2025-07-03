-- ================================
-- KAMPUS PINTAR PRO DATABASE SCHEMA
-- ================================

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    university VARCHAR(100),
    major VARCHAR(100),
    semester INTEGER DEFAULT 1,
    target_gpa DECIMAL(3,2) DEFAULT 3.50,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Chat History Table
CREATE TABLE IF NOT EXISTS chat_history (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    response TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'academic',
    is_academic_topic BOOLEAN DEFAULT true,
    session_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Study Sessions Table
CREATE TABLE IF NOT EXISTS study_sessions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    subject VARCHAR(100),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    duration_minutes INTEGER,
    session_type VARCHAR(20) DEFAULT 'pomodoro',
    notes TEXT,
    completed BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Courses Table
CREATE TABLE IF NOT EXISTS courses (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20),
    credits INTEGER NOT NULL,
    grade_point DECIMAL(3,2) NOT NULL,
    letter_grade VARCHAR(5),
    semester VARCHAR(20),
    academic_year VARCHAR(10),
    is_completed BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. Todo Items Table
CREATE TABLE IF NOT EXISTS todo_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    task TEXT NOT NULL,
    description TEXT,
    due_date DATE,
    priority VARCHAR(20) DEFAULT 'medium',
    category VARCHAR(50) DEFAULT 'academic',
    completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. Study Analytics Table
CREATE TABLE IF NOT EXISTS study_analytics (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    subject VARCHAR(100) DEFAULT 'Umum',
    study_time_minutes INTEGER DEFAULT 0,
    pomodoro_sessions INTEGER DEFAULT 0,
    tasks_completed INTEGER DEFAULT 0,
    focus_score INTEGER DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Insert sample user
INSERT INTO users (id, username, email, full_name, university, major, semester) 
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'mahasiswa_demo', 'demo@kampuspintar.com', 'Mahasiswa Demo', 'Universitas Indonesia', 'Teknik Informatika', 5)
ON CONFLICT (id) DO NOTHING;

-- Insert sample data
INSERT INTO courses (user_id, course_name, course_code, credits, grade_point, letter_grade, semester, academic_year) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Algoritma dan Struktur Data', 'CS101', 3, 3.7, 'A-', 'Semester 3', '2023/2024'),
('550e8400-e29b-41d4-a716-446655440000', 'Basis Data', 'CS102', 3, 3.3, 'B+', 'Semester 3', '2023/2024'),
('550e8400-e29b-41d4-a716-446655440000', 'Pemrograman Web', 'CS103', 3, 4.0, 'A', 'Semester 4', '2023/2024')
ON CONFLICT DO NOTHING;

INSERT INTO todo_items (user_id, task, description, due_date, priority, category) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Tugas Algoritma Sorting', 'Implementasi Quick Sort dan Merge Sort', CURRENT_DATE + INTERVAL '3 days', 'high', 'academic'),
('550e8400-e29b-41d4-a716-446655440000', 'Laporan Praktikum Database', 'Membuat ERD dan implementasi', CURRENT_DATE + INTERVAL '7 days', 'medium', 'academic'),
('550e8400-e29b-41d4-a716-446655440000', 'Presentasi Web Development', 'Demo aplikasi web final project', CURRENT_DATE + INTERVAL '10 days', 'high', 'academic')
ON CONFLICT DO NOTHING;

INSERT INTO study_analytics (user_id, date, subject, study_time_minutes, pomodoro_sessions) VALUES
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE - INTERVAL '6 days', 'Algoritma', 120, 5),
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE - INTERVAL '5 days', 'Database', 90, 4),
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE - INTERVAL '4 days', 'Web Development', 150, 6),
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE - INTERVAL '3 days', 'Algoritma', 100, 4),
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE - INTERVAL '2 days', 'Database', 80, 3),
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE - INTERVAL '1 days', 'Web Development', 110, 5),
('550e8400-e29b-41d4-a716-446655440000', CURRENT_DATE, 'Algoritma', 60, 2)
ON CONFLICT DO NOTHING;
