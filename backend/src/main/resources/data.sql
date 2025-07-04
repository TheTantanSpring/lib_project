-- Library Management System Sample Data (Spring Boot Version)

-- Sample Library Data
INSERT INTO libraries (name, address, phone, latitude, longitude, opening_hours, description, created_at, updated_at) VALUES
('Seoul Central Library', '123 Teheran-ro, Gangnam-gu, Seoul', '02-1234-5678', 37.5665, 126.9780, '09:00-18:00', 'Main library in Seoul', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Gangnam District Library', '456 Yeoksam-dong, Gangnam-gu, Seoul', '02-2345-6789', 37.5013, 127.0396, '09:00-21:00', 'Gangnam district library', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('National Central Library', '201 Banpo-daero, Seocho-gu, Seoul', '02-3456-7890', 37.4981, 127.0276, '09:00-18:00', 'National library', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Seocho District Library', '789 Seocho-daero, Seocho-gu, Seoul', '02-4567-8901', 37.4837, 127.0324, '09:00-20:00', 'Seocho district library', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mapo District Library', '321 World Cup-ro, Mapo-gu, Seoul', '02-5678-9012', 37.5663, 126.9779, '09:00-19:00', 'Mapo district library', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample User Data (Password is BCrypt hashed value - 'password123')
-- Adjusted to match User entity structure: username, password, email, full_name, phone, created_at, updated_at
INSERT INTO users (username, password, email, full_name, phone, created_at, updated_at) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'user1@example.com', 'John Kim', '010-1234-5678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'user2@example.com', 'Jane Lee', '010-2345-6789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'user3@example.com', 'Mike Park', '010-3456-7890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@library.com', 'Admin User', '010-9999-9999', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Book Data
INSERT INTO books (library_id, title, author, isbn, publisher, publication_year, category, total_copies, available_copies, status, created_at, updated_at) VALUES
(1, 'Spring Boot in Action', 'Craig Walls', '978-1234567890', 'Manning', 2023, 'Computer/IT', 3, 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Kotlin Programming', 'Venkat Subramaniam', '978-1234567891', 'Pragmatic Bookshelf', 2023, 'Computer/IT', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Java Complete Guide', 'Joshua Bloch', '978-1234567892', 'Addison-Wesley', 2022, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Database Design', 'Martin Fowler', '978-1234567893', 'Addison-Wesley', 2022, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Web Development Essentials', 'David Flanagan', '978-1234567894', 'O''Reilly', 2023, 'Computer/IT', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'React Native Guide', 'Brent Vatne', '978-1234567895', 'Packt', 2022, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'JavaScript Complete Guide', 'Douglas Crockford', '978-1234567896', 'O''Reilly', 2022, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Python Basics', 'Mark Lutz', '978-1234567897', 'O''Reilly', 2023, 'Computer/IT', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Machine Learning Introduction', 'Andrew Ng', '978-1234567898', 'MIT Press', 2023, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Algorithm Problem Solving', 'Robert Sedgewick', '978-1234567899', 'Addison-Wesley', 2022, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Git Collaboration', 'Scott Chacon', '978-1234567900', 'Apress', 2023, 'Computer/IT', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Docker and Kubernetes', 'Kelsey Hightower', '978-1234567901', 'O''Reilly', 2022, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Cloud Native Applications', 'Cornelia Davis', '978-1234567902', 'Manning', 2023, 'Computer/IT', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 