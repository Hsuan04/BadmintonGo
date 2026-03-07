-- 1. 插入場地資訊 (使用 ON CONFLICT 避免重複 ID 報錯)
INSERT INTO court_info (court_id, name, category, sport_type, address, status, description) VALUES
(1, '中山運動中心', '運動中心', 1, '台北市中山區中山北路二段44巷', 2, '市中心黃金地段，場地多'),
(2, '板橋國民運動中心', '運動中心', 2, '新北市板橋區智樂路6號', 2, '室內木地板籃球場，高度充足'),
(3, '師大附中體育館', '學校', 1, '台北市大安區信義路三段', 2, '優質木地板，光線適中'),
(4, '圓山網球俱樂部', '私人場地', 3, '台北市中山區中山北路四段', 2, '紅土網球場，專業教練指導'),
(5, '南港運動中心', '運動中心', 4, '台北市南港區玉成街', 2, '桌球桌數眾多，附設淋浴間'),
(6, '新莊體育館', '運動中心', 2, '新北市新莊區和興街66號', 2, '國際賽事標準場地'),
(7, '永和羽球館', '私人場地', 1, '新北市永和區民權路', 2, '24小時開放，彈性租借'),
(8, '天母網球場', '私人場地', 3, '台北市士林區忠誠路', 1, '下雨維修中'),
(9, '三重國小球場', '學校', 1, '新北市三重區三和路', 2, '社區友善場地'),
(10, '大直羽球樂園', '私人場地', 1, '台北市中山區樂群二路', 2, '挑高12米，無壓迫感')
ON CONFLICT (court_id) DO NOTHING; -- 如果 ID 已存在，則跳過

-- 2. 重要：更新 PostgreSQL 的 Sequence 序列
-- 確保之後透過 Java 新增資料時，ID 會從 11 開始，不會與這 10 筆衝突
SELECT setval('court_info_court_id_seq', (SELECT MAX(court_id) FROM court_info));

-- 3. 插入場地開放時間
INSERT INTO court_open_info (court_id, day_of_week, is_open, open_time, close_time) VALUES
(1, 1, true, '06:00:00', '22:00:00'),
(1, 2, true, '06:00:00', '22:00:00'),
(1, 3, true, '06:00:00', '22:00:00'),
(1, 4, true, '06:00:00', '22:00:00'),
(1, 5, true, '06:00:00', '22:00:00'),
(6, 6, true, '08:00:00', '21:00:00'),
(6, 7, true, '08:00:00', '18:00:00')
ON CONFLICT (court_id, day_of_week) DO NOTHING; -- 根據 V1 的 Unique Constraint 防止重複

-- 4. 插入場地圖片
INSERT INTO court_image (court_id, image_key, is_primary) VALUES
(1, 'zhongshan-01.jpg', true),
(1, 'zhongshan-02.jpg', false),
(2, 'banqiao-bball.png', true),
(3, 'hsnu-gym.jpg', true),
(7, 'yonghe-1.jpg', true),
(7, 'yonghe-2.jpg', false),
(7, 'yonghe-3.jpg', false),
(10, 'dazhi-badminton.png', true)
ON CONFLICT (image_id) DO NOTHING;