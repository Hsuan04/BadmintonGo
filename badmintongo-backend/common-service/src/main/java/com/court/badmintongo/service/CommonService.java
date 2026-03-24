package com.court.badmintongo.service;

import com.court.badmintongo.bean.po.SystemConfigPo;
import com.court.badmintongo.bean.vo.OptionVo;
import com.court.badmintongo.repository.SystemConfigRepository;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final SystemConfigRepository systemConfigRepository;

    /**
     * 獲取特定分類下所有啟用的配置項 (PO 原始資料)
     * 加上快取，避免頻繁查詢資料庫
     */
    @Cacheable(value = "system_config", key = "#typeCode")
    public List<SystemConfigPo> getActiveConfigs(String typeCode) {
        return systemConfigRepository.findActiveConfigs(typeCode);
    }

    /**
     * 獲取選單格式的配置項
     */
    public List<OptionVo> getOptionsByTypeCode(String typeCode) {
        List<SystemConfigPo> configs = this.getActiveConfigs(typeCode);
        return configs.stream()
                .map(c -> new OptionVo(c.getItemKey(), c.getItemValue()))
                .toList();
    }
}
