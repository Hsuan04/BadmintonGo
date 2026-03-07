-- 1. 基礎場地表 (靜態資訊)
CREATE TABLE court_info (
    court_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20),
    sport_type SMALLINT,
    address TEXT NOT NULL,
    url TEXT,
    description TEXT,
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
COMMENT ON COLUMN court_info.status IS '1: 審核中, 2: 開放, 3: 關閉, 4: 刪除';

-- 2. 場地開放時間表
CREATE TABLE court_open_info (
    id SERIAL PRIMARY KEY,
    court_id BIGINT NOT NULL,
    day_of_week SMALLINT NOT NULL,
    is_open BOOLEAN DEFAULT TRUE,
    open_time TIME,
    close_time TIME,
    CONSTRAINT fk_court_info FOREIGN KEY(court_id) REFERENCES court_info(court_id) ON DELETE CASCADE,
    CONSTRAINT unique_court_day UNIQUE(court_id, day_of_week),
    CONSTRAINT check_day_range CHECK (day_of_week BETWEEN 1 AND 7)
);

-- 3. 場地圖片表
CREATE TABLE court_image (
    image_id BIGSERIAL PRIMARY KEY,
    court_id BIGINT NOT NULL,
    image_key VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_court_image_info FOREIGN KEY(court_id) REFERENCES court_info(court_id) ON DELETE CASCADE
);
CREATE INDEX idx_court_image_court_id ON court_image(court_id);