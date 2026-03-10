package com.court.badmintongo.enums;

import com.court.badmintongo.exception.IReturnCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourtReturnCode implements IReturnCode {

    // 格式：枚舉名("錯誤代碼", "錯誤訊息")

    // --- 成功 ---
    SUCCESS("0000", "操作成功"),

    // --- 參數驗證錯誤 (E開頭) ---
    PARAM_ERROR("E001", "參數格式不正確"),

    // --- 業務邏輯錯誤 (C開頭，代表 Court 業務) ---
    DUPLICATE_NAME("C101", "該場地名稱已被註冊，請換個名字。"),
    COURT_NOT_FOUND("C102", "找不到指定的場地資料"),
    TIME_CONFLICT("C103", "開放時間設定衝突"),
    ALREADY_DELETED("C104", "該場地先前已被刪除");


    private final String code;
    private final String message;
}
