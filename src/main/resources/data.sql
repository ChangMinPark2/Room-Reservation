-- User 초기 데이터 (3개)
INSERT INTO tbl_user (name, phone_number) VALUES 
('홍길동', '010-1234-5678'),
('김길동', '010-2345-6789'),
('박길동', '010-3456-7890');

-- MeetingRoom 초기 데이터 (5개)
INSERT INTO tbl_meetingroom (name, capacity, half_hourly_rate, is_active) VALUES
('A회의실', 10, 25000, true),
('B회의실', 20, 40000, true),
('C회의실', 15, 30000, true),
('대회의실', 50, 75000, true),
('소회의실', 5, 15000, true);