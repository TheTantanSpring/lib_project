-- 테스트용 샘플 데이터
-- 도서관 데이터
INSERT INTO libraries (id, name, address, phone, latitude, longitude, opening_hours, description, created_at, updated_at) VALUES
(1, '서울시립도서관', '서울특별시 강남구 테헤란로 123', '02-1234-5678', 37.5665, 126.9780, '09:00-18:00', '서울시에서 운영하는 시립도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '강남구립도서관', '서울특별시 강남구 역삼동 456', '02-2345-6789', 37.4979, 127.0276, '09:00-20:00', '강남구에서 운영하는 구립도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '서초구립도서관', '서울특별시 서초구 서초동 789', '02-3456-7890', 37.4838, 127.0324, '09:00-18:00', '서초구에서 운영하는 구립도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '마포구립도서관', '서울특별시 마포구 합정동 321', '02-4567-8901', 37.5546, 126.9140, '09:00-19:00', '마포구에서 운영하는 구립도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '국립중앙도서관', '서울특별시 서초구 반포대로 201', '02-5678-9012', 37.5032, 127.0374, '09:00-18:00', '국립중앙도서관', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 사용자 데이터
INSERT INTO users (id, username, password, email, full_name, phone, created_at, updated_at) VALUES
(1, 'testuser1', 'password123', 'test1@example.com', '김테스트', '010-1234-5678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'testuser2', 'password456', 'test2@example.com', '이테스트', '010-2345-6789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'testuser3', 'password789', 'test3@example.com', '박테스트', '010-3456-7890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 도서 데이터
INSERT INTO books (id, library_id, title, author, isbn, publisher, publication_year, category, description, total_copies, available_copies, status, created_at, updated_at) VALUES
-- 서울시립도서관 (ID: 1)
(1, 1, '스프링 부트 실전 활용', '김철수', '978-1234567890', '테크출판사', 2023, '컴퓨터/IT', 'Spring Boot 실전 가이드', 3, 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, '코틀린 프로그래밍', '이영희', '978-2345678901', '코딩출판사', 2022, '컴퓨터/IT', 'Kotlin 완전 정복', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, '자바 완전 정복', '박민수', '978-3456789012', '프로그래밍출판사', 2021, '컴퓨터/IT', 'Java 심화 학습서', 4, 4, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 강남구립도서관 (ID: 2)
(4, 2, '데이터베이스 설계', '박민수', '978-4567890123', '데이터출판사', 2023, '컴퓨터/IT', 'DB 설계 완전 가이드', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 2, '웹 개발의 정석', '최지영', '978-5678901234', '웹출판사', 2022, '컴퓨터/IT', '웹 개발 기초부터 실전까지', 3, 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 2, '리액트 네이티브', '정수민', '978-6789012345', '모바일출판사', 2023, '컴퓨터/IT', 'React Native 완전 정복', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 서초구립도서관 (ID: 3)
(7, 3, '자바스크립트 완벽 가이드', '정수민', '978-7890123456', 'JS출판사', 2022, '컴퓨터/IT', 'JavaScript 심화 학습', 3, 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 3, '파이썬 기초', '김철수', '978-8901234567', '파이썬출판사', 2021, '컴퓨터/IT', 'Python 기초부터 실전까지', 4, 4, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 3, '머신러닝 입문', '이영희', '978-9012345678', 'AI출판사', 2023, '컴퓨터/IT', 'ML 기초 및 실습', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 마포구립도서관 (ID: 4)
(10, 4, '알고리즘 문제 해결', '박민수', '978-0123456789', '알고리즘출판사', 2022, '컴퓨터/IT', '알고리즘 문제 해결 기법', 3, 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 4, '네트워크 보안', '최지영', '978-1234567891', '보안출판사', 2023, '컴퓨터/IT', '네트워크 보안 완전 가이드', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 4, '클라우드 컴퓨팅', '정수민', '978-2345678902', '클라우드출판사', 2023, '컴퓨터/IT', '클라우드 기술 완전 정복', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 국립중앙도서관 (ID: 5)
(13, 5, '클라우드 네이티브', '김철수', '978-3456789013', '클라우드출판사', 2023, '컴퓨터/IT', '클라우드 네이티브 애플리케이션', 1, 1, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 5, '데이터 사이언스', '이영희', '978-4567890124', '데이터출판사', 2022, '컴퓨터/IT', '데이터 사이언스 완전 정복', 3, 3, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 5, '소프트웨어 아키텍처', '박민수', '978-5678901235', '아키텍처출판사', 2023, '컴퓨터/IT', '소프트웨어 아키텍처 설계', 2, 2, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 