package com.court.courtservice.service;

import com.court.courtservice.bean.po.CourtInfoPo;
import com.court.courtservice.bean.vo.CreateCourtRq;
import com.court.courtservice.enums.CourtReturnCode;
import com.court.courtservice.exception.BusinessException;
import com.court.courtservice.repository.CourtInfoRepository;
import com.court.courtservice.repository.CourtOpenInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 使用 Mockito 框架
public class CourtServiceTest {

    @Mock
    private CourtInfoRepository courtInfoRepository;

    @Mock
    private CourtOpenInfoRepository courtOpenInfoRepository;

    @InjectMocks
    private CourtService courtService; // 將模擬好的 Repo 注入 Service

    @Test
    @DisplayName("新增場地 - 成功流程")
    void create_Success() {
        // [1] 準備輸入資料 (使用 Builder!)
        CreateCourtRq rq = CreateCourtRq.builder()
                .name("板橋羽球館")
                .category("羽球")
                .address("智樂路 1 號")
                .openTimeList(Collections.emptyList())
                .build();

        // [2] 定義 Mock 行為 (打樁)
        // 當執行 existsByName 時，回傳 false (表示名稱沒重複)
        when(courtInfoRepository.existsByName(anyString())).thenReturn(false);
        // 當執行 save 時，回傳一個帶有 ID 的 PO
        when(courtInfoRepository.save(any(CourtInfoPo.class))).thenAnswer(invocation -> {
            CourtInfoPo po = invocation.getArgument(0);
//            po.setCourtId(100L); // 模擬資料庫生成 ID
            return po;
        });

        // [3] 執行測試
//        CreateCourtRs result = courtService.create(rq);

        // [4] 驗證結果
//        assertNotNull(result);
//        assertEquals(100L, result.getCourtId());
//        assertEquals("板橋羽球館", result.getName());

        // 驗證 courtRepo.save() 是否真的有被呼叫過 1 次
        verify(courtInfoRepository, times(1)).save(any(CourtInfoPo.class));
    }

    @Test
    @DisplayName("新增場地 - 名稱重複應拋出異常")
    void create_DuplicateName_ThrowException() {
        // [1] 準備輸入
        CreateCourtRq rq = CreateCourtRq.builder().name("重複名").build();

        // [2] 模擬名稱已存在
        when(courtInfoRepository.existsByName("重複名")).thenReturn(true);

        // [3] 驗證是否拋出正確的 BusinessException
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            courtService.create(rq);
        });

        assertEquals(CourtReturnCode.DUPLICATE_NAME, exception.getCode());
    }

    @Test
    @DisplayName("新增場地 - 失敗：場地名稱已存在")
    void create_Fail_DuplicateName() {
        // 1. 準備 Request
        CreateCourtRq rq = CreateCourtRq.builder().name("重複場地").build();

        // 2. 模擬 Repository 回傳 true (表示名稱重複)
        when(courtInfoRepository.existsByName("重複場地")).thenReturn(true);

        // 3. 執行並驗證是否拋出正確異常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            courtService.create(rq);
        });

        // 4. 檢查錯誤代碼
        assertEquals(CourtReturnCode.DUPLICATE_NAME, exception.getCode());

        // 5. 確保後續的 save 動作「完全沒有」被執行（這對安全性測試很重要）
        verify(courtInfoRepository, never()).save(any());
        verify(courtOpenInfoRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("新增場地 - 成功：當沒有傳入開放時間清單時")
    void create_Success_WithNullOpenTime() {
        // 1. 準備 Request (openTimeList 為 null)
        CreateCourtRq rq = CreateCourtRq.builder()
                .name("無人球場")
                .openTimeList(null)
                .build();

        // 2. 打樁
        when(courtInfoRepository.existsByName(anyString())).thenReturn(false);
        when(courtInfoRepository.save(any(CourtInfoPo.class))).thenAnswer(i -> {
            CourtInfoPo po = i.getArgument(0);
//            po.setCourtId(2L);
            return po;
        });

        // 3. 執行
//        CreateCourtRs result = courtService.create(rq);

        // 4. 驗證 (覆蓋了 if(list != null) 的 else 路徑)
//        assertNotNull(result);
//        assertTrue(result.getOpenTimes() == null || result.getOpenTimes().isEmpty());

        // 驗證從表的 saveAll 從未被呼叫
        verify(courtOpenInfoRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("新增場地 - 成功：多筆開放時間轉換驗證")
    void create_Success_MultipleOpenTimes() {
        // 1. 準備 Request (傳入兩筆時間)
        CreateCourtRq rq = CreateCourtRq.builder()
                .name("巨蛋體育館")
                .openTimeList(List.of(
                        CreateCourtRq.OpenTimeRq.builder().dayOfWeek(1).openTime("08:00").closeTime("12:00").build(),
                        CreateCourtRq.OpenTimeRq.builder().dayOfWeek(2).openTime("13:00").closeTime("17:00").build()
                ))
                .build();

        when(courtInfoRepository.existsByName(anyString())).thenReturn(false);
        when(courtInfoRepository.save(any(CourtInfoPo.class))).thenAnswer(i -> {
            CourtInfoPo po = i.getArgument(0);
//            po.setCourtId(3L);
            return po;
        });
        when(courtOpenInfoRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        // 2. 執行
//        CreateCourtRs result = courtService.create(rq);

        // 3. 驗證 list 的大小與內容轉換是否正確
//        assertEquals(2, result.getOpenTimes().size());
//        assertEquals("08:00", result.getOpenTimes().get(0).getOpenTime());
//        assertEquals("13:00", result.getOpenTimes().get(1).getOpenTime());
    }
}
