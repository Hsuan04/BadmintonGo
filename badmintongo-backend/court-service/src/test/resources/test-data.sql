-- 1. 清空所有相關資料表並重置 ID 序列
TRUNCATE TABLE court_image, court_open_info, court_info RESTART IDENTITY CASCADE;

-- 2. 新增場地資訊(court_info)
-- 包含：運動中心、學校、私人場地；狀態：1 (審核中), 2 (開放)
INSERT INTO court_info (court_id, name, category, sport_type, address, status, description) VALUES
(1, '大安運動中心', '運動中心', 1, '台北市大安區辛亥路三段157巷', 2, '專業羽球場，木地板'),
(2, '松山國小體育館', '學校', 1, '台北市松山區八德路四段746號', 2, '交通方便，近捷運站'),
(3, '北投羽球王', '私人場地', 1, '台北市北投區承德路七段', 2, '冷氣開放，燈光充足'),
(4, '信義區運動公園', '運動中心', 2, '台北市信義區松勤路', 1, '目前整修中，暫不開放'),
(5, '內湖科學園區球館', '私人場地', 1, '台北市內湖區瑞光路', 2, '地板防滑性佳');

-- 3. 新增場地開放時間 (court_open_info)
INSERT INTO court_open_info (court_id, day_of_week, is_open, open_time, close_time) VALUES
(1, 1, true, '08:00:00', '22:00:00'),
(1, 2, true, '08:00:00', '22:00:00'),
-- 松山國小 (ID: 2)
(2, 6, true, '09:00:00', '18:00:00'),
(2, 7, false, NULL, NULL),
-- 北投羽球王 (ID: 3)
(3, 1, true, '06:00:00', '23:00:00'),
-- 內湖球館 (ID: 5)
(5, 1, true, '10:00:00', '22:00:00');

-- 4. 新增場地圖片 (court_image)
INSERT INTO court_image (court_id, image_key, is_primary) VALUES
(1, 'da-an-main.jpg', true),
(1, 'da-an-detail.jpg', false),
(2, 'songshan-school.jpg', true),
(3, 'beitou-king.png', true),
(5, 'neihu-pro.jpg', true);