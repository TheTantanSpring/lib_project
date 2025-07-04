-- Library Management System Sample Data Insert Script

-- Sample Library Data
INSERT INTO libraries (name, address, phone, latitude, longitude, opening_hours, description, created_at, updated_at) VALUES
('서울시립도서관', '서울특별시 강남구 테헤란로 123', '02-1234-5678', 37.5665, 126.9780, '09:00-18:00', '서울시 대표 도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('강남구립도서관', '서울특별시 강남구 역삼동 456', '02-2345-6789', 37.5013, 127.0396, '09:00-21:00', '강남구 지역 도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('국립중앙도서관', '서울특별시 서초구 반포대로 201', '02-3456-7890', 37.4981, 127.0276, '09:00-18:00', '국립 도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('서초구립도서관', '서울특별시 서초구 서초대로 789', '02-4567-8901', 37.4837, 127.0324, '09:00-20:00', '서초구 지역 도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('마포구립도서관', '서울특별시 마포구 월드컵로 321', '02-5678-9012', 37.5663, 126.9779, '09:00-19:00', '마포구 지역 도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Sample Book Data
INSERT INTO books (library_id, title, author, isbn, publisher, publication_year, category, total_copies, available_copies) VALUES
(1, '스프링 부트 실전 활용', '김철수', '978-1234567890', '한빛미디어', 2023, '컴퓨터/IT', 3, 3),
(1, '코틀린 프로그래밍', '이영희', '978-1234567891', '길벗', 2023, '컴퓨터/IT', 2, 2),
(1, '자바 완전 정복', '박민수', '978-1234567892', '정보문화사', 2022, '컴퓨터/IT', 1, 1),
(2, '데이터베이스 설계', '박민수', '978-1234567893', '정보문화사', 2022, '컴퓨터/IT', 1, 1),
(2, '웹 개발의 정석', '최지영', '978-1234567894', '위키북스', 2023, '컴퓨터/IT', 2, 2),
(2, '리액트 네이티브', '정수민', '978-1234567895', '인사이트', 2022, '컴퓨터/IT', 1, 1),
(3, '자바스크립트 완벽 가이드', '정수민', '978-1234567896', '인사이트', 2022, '컴퓨터/IT', 1, 1),
(3, '파이썬 기초', '김철수', '978-1234567897', '한빛미디어', 2023, '컴퓨터/IT', 2, 2),
(3, '머신러닝 입문', '이영희', '978-1234567898', '길벗', 2023, '컴퓨터/IT', 1, 1),
(4, '알고리즘 문제 해결', '박민수', '978-1234567899', '정보문화사', 2022, '컴퓨터/IT', 1, 1),
(4, '깃허브로 시작하는 협업', '최지영', '978-1234567900', '위키북스', 2023, '컴퓨터/IT', 2, 2),
(5, '도커와 쿠버네티스', '정수민', '978-1234567901', '인사이트', 2022, '컴퓨터/IT', 1, 1),
(5, '클라우드 네이티브', '김철수', '978-1234567902', '한빛미디어', 2023, '컴퓨터/IT', 1, 1)
ON CONFLICT DO NOTHING;

-- Sample User Data (Password is BCrypt hashed value - 'password123')
INSERT INTO users (email, password, name, phone, address) VALUES
('user1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '김사용자', '010-1234-5678', '서울시 강남구'),
('user2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '이사용자', '010-2345-6789', '서울시 서초구'),
('user3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '박사용자', '010-3456-7890', '서울시 마포구'),
('admin@library.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '관리자', '010-9999-9999', '서울시 강남구')
ON CONFLICT DO NOTHING;

-- Sample Loan Data
INSERT INTO loans (user_id, book_id, loan_date, due_date, status) VALUES
(1, 1, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '9 days', 'ACTIVE'),
(2, 3, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '11 days', 'ACTIVE'),
(3, 5, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '13 days', 'ACTIVE')
ON CONFLICT DO NOTHING;

-- Sample Reservation Data
INSERT INTO reservations (user_id, book_id, reservation_date, status) VALUES
(1, 7, CURRENT_TIMESTAMP - INTERVAL '2 days', 'PENDING'),
(2, 9, CURRENT_TIMESTAMP - INTERVAL '1 day', 'PENDING'),
(3, 11, CURRENT_TIMESTAMP, 'PENDING')
ON CONFLICT DO NOTHING; 