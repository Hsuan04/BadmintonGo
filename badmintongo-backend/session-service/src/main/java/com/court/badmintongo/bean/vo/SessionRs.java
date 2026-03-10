package com.court.badmintongo.bean.vo;

import com.court.badmintongo.bean.po.SessionInfoPo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 臨打資訊統一 Response
 */
public record SessionRs(
        String sessionId,
        String courtId,
        String courtName,
        LocalDate sessionDate,
        String startTime,
        String endTime,
        Integer maxParticipants,
        Integer currentParticipants,
        Integer waitlistCount,
        Integer status,
        String description,
        Integer minLevel,
        Integer maxLevel,
        String shuttlecockUsed,
        String organizer,
        LocalDateTime createdAt
) {
        /**
         * 靜態工廠方法：將 PO 轉換為 SessionRs
         */
        public static SessionRs from(SessionInfoPo po) {
            return new SessionRs(
                    po.getSessionId(),
                    po.getCourtId(),
                    po.getCourtName(),
                    po.getSessionDate(),
                    po.getStartTime() != null ? po.getStartTime().toString() : null,
                    po.getEndTime() != null ? po.getEndTime().toString() : null,
                    po.getMaxParticipants(),
                    po.getCurrentParticipants(),
                    po.getWaitlistCount(),
                    po.getStatus(),
                    po.getDescription(),
                    po.getMinLevel(),
                    po.getMaxLevel(),
                    po.getShuttlecockUsed(),
                    po.getOrganizer(),
                    po.getCreatedAt()
            );
        }
    /**
     * 使用 Redis 的即時數據更新 Rs 並回傳新實例
     * 這樣可以保持 Record 的不可變性，同時優化 Service 代碼
     */
    public SessionRs updateRealTimeData(Map<Object, Object> meta) {
        return new SessionRs(
                this.sessionId,
                this.courtId,
                this.courtName,
                this.sessionDate,
                this.startTime,
                this.endTime,
                this.maxParticipants,
                // 從 Redis 獲取數據，若不存在則保留原本的值
                Integer.parseInt(meta.getOrDefault("currentParticipants", String.valueOf(this.currentParticipants)).toString()),
                Integer.parseInt(meta.getOrDefault("waitlistCount", String.valueOf(this.waitlistCount)).toString()),
                Integer.parseInt(meta.getOrDefault("status", String.valueOf(this.status)).toString()),
                this.description,
                this.minLevel,
                this.maxLevel,
                this.shuttlecockUsed,
                this.organizer,
                this.createdAt
        );
    }
    }


