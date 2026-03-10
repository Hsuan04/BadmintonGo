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
    public enum CourtStatus {
        UNDER_REVIEW(1, "審核中"),
        OPEN(2, "開放中"),
        CLOSED(3, "關閉中");

        private final Integer code;
        private final String desc;
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
}
