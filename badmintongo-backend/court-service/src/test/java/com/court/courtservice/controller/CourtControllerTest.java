package com.court.courtservice.controller;

import com.court.courtservice.bean.vo.CreateCourtRq;
import com.court.courtservice.service.CourtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourtController.class)
public class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc; // 模擬 HTTP 請求的核心工具

    @Autowired
    private ObjectMapper objectMapper; // 用來把物件轉成 JSON 字串

    @MockitoBean // 在 Spring Boot 3.4+ 建議使用此註解模擬 Service
    private CourtService courtService;

    @Test
    @DisplayName("API 測試 - 新增場地成功")
    void create_Success() throws Exception {
        // 1. 準備符合校驗規則的 Request (補齊被標註為不可為空的欄位)
        CreateCourtRq rq = CreateCourtRq.builder()
                .name("台北球館")
                .category("羽球中心") // 補上這個
                .sportType(1)
                .address("台北市中山區...") // 補上這個
                .openTimeList(List.of(      // 補上這個
                        CreateCourtRq.OpenTimeRq.builder()
                                .dayOfWeek(1)
                                .openTime("09:00:00")
                                .closeTime("22:00:00")
                                .isOpen(true)
                                .build()
                ))
                .build();

        // 2. 預期的 Rs (Service 回傳的 mock 資料)
//        CourtRs rs = CourtRs.builder()
//                .courtId(1L)
//                .name("台北球館")
//                .build();

//        when(courtService.create(any(CreateCourtRq.class))).thenReturn(rs);

        // 3. 執行測試
        mockMvc.perform(post("/api/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isOk()) // 這次應該就會是 200 了
                .andExpect(jsonPath("$.courtId").value(1L))
                .andExpect(jsonPath("$.name").value("台北球館"));
    }

    @Test
    @DisplayName("API 測試 - 驗證失敗 (名稱不可為空)")
    void create_Fail_InvalidInput() throws Exception {
        // 1. 準備一個不合法的 Request (name 是空的)
        CreateCourtRq rq = CreateCourtRq.builder()
                .name("") // 假設你有加 @NotBlank
                .build();

        // 2. 執行請求，預期會因為 Validation 失敗回傳 400 Bad Request
        mockMvc.perform(post("/api/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }
}
