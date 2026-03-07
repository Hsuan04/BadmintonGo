package com.court.badmintongo.bean.vo;

import com.court.badmintongo.bean.po.SessionInfoPo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
    }


