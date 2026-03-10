package com.court.badmintongo.enums;

import com.court.badmintongo.exception.IReturnCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum SessionReturnCode implements IReturnCode {

    // --- 成功 ---
    SUCCESS("0000", "操作成功"),

    // --- 業務檢核錯誤 (4000 ~ 4999) ---
    SESSION_NOT_FOUND("4001", "找不到該場次資料"),
    SESSION_STATUS_LOCKED("4002", "場次已結束或已取消，無法修改資料"),
    DATE_CHANGE_NOT_ALLOWED("4003", "不可修改場次日期，若需更改請刪除並重新建立"),

    // --- 報名相關 (預留給後續功能) ---
    SESSION_FULL("4004", "該場次報名人數已滿"),
    ALREADY_JOINED("4005", "您已報名過此場次"),

    // --- 系統錯誤 ---
    SYSTEM_ERROR("500", "系統繁忙，請稍後再試");

    private final String code;
    private final String message;

    SessionReturnCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
