-- Initialize the database schema for the Note Application

-- Use the notedb database
USE notedb;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create notes table
CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create tags table
CREATE TABLE IF NOT EXISTS tags (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    color VARCHAR(7) DEFAULT '#607D8B', -- Default color in hex
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_tag_per_user (name, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create note_tags junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS note_tags (
    note_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (note_id, tag_id),
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Create reminders table
CREATE TABLE IF NOT EXISTS reminders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    note_id INT NOT NULL,
    reminder_time DATETIME NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE
);

-- Insert test data for development (optional)
-- Uncomment if you want to have test data

/*
-- Create a test user (password: testpassword)
INSERT INTO users (username, email, password_hash, full_name) 
VALUES ('testuser', 'test@example.com', '$2a$10$XgNEHAr1E3JWAXjmQGfnZOEUZojLImJY8djrR2S8QglyK1ZhNO5Y.', 'Test User');

-- Create some test notes
INSERT INTO notes (user_id, title, content) 
VALUES 
(1, 'Welcome Note', 'Welcome to the Note App! This is a sample note.'),
(1, 'Shopping List', 'Milk\nEggs\nBread\nFruit'),
(1, 'Project Ideas', 'Here are some project ideas I want to work on:\n1. Mobile app\n2. Web service\n3. IOT device');

-- Create some test tags
INSERT INTO tags (name, user_id, color) 
VALUES 
('Personal', 1, '#4CAF50'),
('Work', 1, '#2196F3'),
('Urgent', 1, '#F44336');

-- Associate tags with notes
INSERT INTO note_tags (note_id, tag_id) 
VALUES 
(1, 1),
(2, 1),
(3, 2),
(3, 3);
*/
