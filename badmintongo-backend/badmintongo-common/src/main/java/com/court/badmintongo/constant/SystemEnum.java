package com.court.badmintongo.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SystemEnum {

    @Getter
    @AllArgsConstructor
    public enum UserType {
        ADMIN("ADMIN", "管理者"),
        MEMBER("MEMBER", "正式會員"),
        GUEST("GUEST", "訪客");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum UserRole {
        ADMIN("ROLE_ADMIN", "管理者"),
        MEMBER("ROLE_MEMBER", "正式會員"),
        GUEST("ROLE_GUEST", "訪客");

        private final String code;
        private final String desc;

        public String getShortName() {
            return this.code.replace("ROLE_", "");
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CourtStatus {
        UNDER_REVIEW(1, "審核中"),
        OPEN(2, "開放中"),
        CLOSED(3, "關閉中");

        private final Integer code;
        private final String desc;

        /**
         * 透過 Code (1, 2, 3...) 取得中文描述 (審核中...)
         */
        public static String getDescByCode(Integer code) {
            if (code == null) return "-";
            for (CourtStatus status : values()) {
                if (status.code.equals(code)) {
                    return status.desc;
                }
            }
            return "未知狀態";
        }
    }

    @Getter
    @AllArgsConstructor
    public enum SessionStatus {
        OPEN(1, "開放報名"),
        WAITLIST(2, "候補中"),
        FULL(3, "已額滿"),
        FINISHED(4, "已結束"),
        CANCELLED(5, "已取消");

        private final Integer code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum RegisterStatus {
        NOT_STARTED(0, "尚未開始"),
        FINISHED(1, "已結束"),
        CANCELLED(2, "已取消");

        private final Integer code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum CourtCategory {
        SCHOOL("SCHOOL", "學校"),
        SPORTS_CENTER("SPORTS_CENTER", "運動中心"),
        PRIVATE("PRIVATE", "私人場地");

        private final String code;
        private final String desc;

        // 最簡單、最好調試的找法
        public static String getDescByCode(String code) {
            if (code == null) return "-";
            for (CourtCategory category : values()) {
                if (category.code.equalsIgnoreCase(code)) {
                    return category.desc;
                }
            }
            return code;
        }
    }

}
