-- 臨打場次資訊表 (Session Info)
CREATE TABLE IF NOT EXISTS session_info (
    -- 使用 TSID (Long)，因此主鍵必須是 BIGINT，不可使用 SERIAL
    session_id           BIGINT PRIMARY KEY,

    -- 關聯場地 ID
    court_id             BIGINT,
    court_name           VARCHAR(255),

    -- 日期與時間 (對應 LocalDate 與 LocalTime)
    session_date         DATE,
    start_time           TIME,
    end_time             TIME,

    -- 人數控管欄位
    max_participants     INTEGER,
    current_participants INTEGER,
    waitlist_count       INTEGER,

    -- 狀態 (1:開放報名, 2:候補中, 3:已額滿, 4:已結束)
    status               INTEGER,

    -- 詳細說明與程度限制
    description          TEXT,
    min_level            INTEGER,
    max_level            INTEGER,
    shuttlecock_used     VARCHAR(100),
    organizer            VARCHAR(100),

    -- 審計欄位 (對應 LocalDateTime)
    -- created_at 不可更新
    created_at           TIMESTAMP,
    updated_at           TIMESTAMP
);

-- 加上註解 (Comment) 方便資料庫維護
COMMENT ON COLUMN session_info.session_id IS '臨打資料 ID (由 TSID 產生)';
COMMENT ON COLUMN session_info.status IS '1:開放報名, 2:候補中, 3:已額滿, 4:已結束';

-- 建立索引以優化查詢效能
-- 針對日期進行索引，因為使用者最常搜尋「某天的場次」
CREATE INDEX idx_session_date ON session_info(session_date);
-- 針對場地 ID 建立索引，方便查詢該場地所有歷史紀錄
CREATE INDEX idx_court_id ON session_info(court_id);