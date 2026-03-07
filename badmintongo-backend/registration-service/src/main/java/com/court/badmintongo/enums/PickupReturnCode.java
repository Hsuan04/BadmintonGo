package com.court.badmintongo.enums;

import com.badmintongo.exception.IReturnCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PickupReturnCode implements IReturnCode {

    // 格式：枚舉名("錯誤代碼", "錯誤訊息")

    // --- 成功 ---
    SUCCESS("0000", "操作成功");



    private final String code;
    private final String message;
}
