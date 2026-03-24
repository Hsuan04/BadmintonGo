package com.court.badmintongo.bean.vo;

/**
 * 系統配置選項 VO
 * 用於統一回傳前端下拉選單需要的格式
 */
public record OptionVo(
        String value,  // 對應資料庫 system_config.item_key
        String label   // 對應資料庫 system_config.item_value
) {}
