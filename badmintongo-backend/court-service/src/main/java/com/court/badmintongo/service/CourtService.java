package com.court.badmintongo.service;

import com.court.badmintongo.bean.po.CourtHolidayInfoPo;
import com.court.badmintongo.bean.po.CourtImagePo;
import com.court.badmintongo.bean.po.CourtInfoPo;
import com.court.badmintongo.bean.po.CourtOpenInfoPo;
import com.court.badmintongo.bean.vo.CourtRs;
import com.court.badmintongo.bean.vo.CreateCourtRq;
import com.court.badmintongo.bean.vo.UpdateCourtRq;
import com.court.badmintongo.constant.SystemEnum.CourtStatus;
import com.court.badmintongo.enums.CourtReturnCode;
import com.court.badmintongo.exception.BusinessException;
import com.court.badmintongo.mapper.CourtMapper;
import com.court.badmintongo.repository.CourtHolidayInfoRepository;
import com.court.badmintongo.repository.CourtImageRepository;
import com.court.badmintongo.repository.CourtInfoRepository;
import com.court.badmintongo.repository.CourtOpenInfoRepository;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtInfoRepository courtInfoRepository;
    private final CourtOpenInfoRepository courtOpenInfoRepository;
    private final CourtHolidayInfoRepository courtHolidayInfoRepository;
    private final CourtImageRepository courtImageRepository;
    private final CourtImageService courtImageService;
    private final MinioService minioService;
    private final CourtMapper courtMapper;

    @Value("${s3.endpoint}")
    private String s3Endpoint;

    @Value("${s3.bucket-name}")
    private String bucketName;

    /**
     * 建立新的球場完整資訊。
     * 執行流程包含：球場名稱唯一性校驗、主表資訊存檔、營業時段初始化、以及特殊休息日設定。
     * 所有 ID 均採用 TSID (Time-Sorted Unique Identifiers) 產生，確保時序性與唯一性。
     *
     * @param courtRq 新增球場請求物件 {@link CreateCourtRq}
     * @return 包含主表與所有從表資訊的結果物件 {@link CourtRs}
     * @throws BusinessException 當球場名稱已存在時拋出 DUPLICATE_NAME 錯誤
     */
    @Transactional
    public CourtRs create(CreateCourtRq courtRq) {

        // 1. 業務驗證：檢查球場名稱是否重複，確保資料唯一性
        if (courtInfoRepository.existsByName(courtRq.getName())) {
            throw new BusinessException(CourtReturnCode.DUPLICATE_NAME);
        }

        // 2. 建立並儲存主表 (CourtInfoPo)
        // 使用 TSID 產生場地唯一識別碼，並預設狀態為「審核中」
        CourtInfoPo courtPo = CourtInfoPo.builder()
                .courtId(TSID.fast().toString())
                .name(courtRq.getName())
                .category(courtRq.getCategory())
                .sportType(courtRq.getSportType())
                .address(courtRq.getAddress())
                .url(courtRq.getUrl())
                .description(courtRq.getDescription())
                .status(CourtStatus.UNDER_REVIEW.getCode())   // 預設(審核中)
                .createdAt(OffsetDateTime.now())
                .build();

        CourtInfoPo savedCourt = courtInfoRepository.save(courtPo);

        // 3. 處理開放時間清單 (CourtOpenInfoPo)
        List<CourtOpenInfoPo> savedOpenPos = new ArrayList<>();
        if (courtRq.getOpenTimeList() != null) {
            // 將 DTO 轉換為 PO，並建立與主表的關聯 (courtId)
            List<CourtOpenInfoPo> openPoList = courtRq.getOpenTimeList().stream().map(openBo ->
                    CourtOpenInfoPo.builder()
                            .id(TSID.fast().toString())
                            .courtId(savedCourt.getCourtId())
                            .dayOfWeek(openBo.getDayOfWeek())
                            .isOpen(openBo.getIsOpen())
                            .openTime(LocalTime.parse(openBo.getOpenTime()))
                            .closeTime(LocalTime.parse(openBo.getCloseTime()))
                            .build()
            ).toList();

            // 批次儲存開放時段
            savedOpenPos = courtOpenInfoRepository.saveAll(openPoList);
        }

        // 4. 處理特殊休息日清單 (CourtHolidayInfoPo)
        List<CourtHolidayInfoPo> savedHolidayPos = new ArrayList<>();
        if (courtRq.getFixedHolidayList() != null && !courtRq.getFixedHolidayList().isEmpty()) {
            // 解析日期與時間字串，並處理全天休息（時間為 null）的情境
            List<CourtHolidayInfoPo> holidayPoList = courtRq.getFixedHolidayList().stream().map(holidayRq ->
                    CourtHolidayInfoPo.builder()
                            .courtHolidayId(TSID.fast().toString())
                            .courtId(savedCourt.getCourtId())
                            .holidayDate(LocalDate.parse(holidayRq.getDate()))
                            .startTime(holidayRq.getStartTime() != null ? LocalTime.parse(holidayRq.getStartTime()) : null)
                            .endTime(holidayRq.getEndTime() != null ? LocalTime.parse(holidayRq.getEndTime()) : null)
                            .description(holidayRq.getDescription())
                            .build()
            ).toList();

            // 批次儲存假日設定
            savedHolidayPos = courtHolidayInfoRepository.saveAll(holidayPoList);
        }

        // 5. 組裝回傳結果：使用靜態工廠方法將 saved POs 轉換為 Response DTO，初始圖片清單為空
        return CourtRs.from(savedCourt, savedOpenPos,savedHolidayPos, List.of());
    }

    /**
     * 刪除球場資料。
     * 根據球場目前的狀態執行不同的刪除策略
     * 硬刪除 (Hard Delete)：若場地處於「審核中 (UNDER_REVIEW)」，則物理刪除主表、從表及所有圖片檔案。
     * 軟刪除 (Soft Delete)：若場地已「開放 (OPEN)」或「關閉 (CLOSED)」，則僅將狀態標記為「已刪除 (DELETED)」，並保留歷史關聯資料。
     *
     * @param courtId 球場識別碼
     * @return 若執行軟刪除，回傳刪除前的球場快照 {@link CourtRs}；若執行硬刪除則回傳 null
     * @throws BusinessException 若場地不存在 (COURT_NOT_FOUND) 或已經是刪除狀態 (ALREADY_DELETED)
     */
    @Transactional
    public CourtRs delete(String courtId) {
        // 1. 驗證與查詢：確保目標場地存在
        CourtInfoPo courtInfoPo = courtInfoRepository.findById(courtId)
                .orElseThrow(() -> new BusinessException(CourtReturnCode.COURT_NOT_FOUND));

        Integer status = courtInfoPo.getStatus();

        // 2. 防呆檢查：若該場地已是刪除狀態，禁止重複操作
        if (CourtStatus.DELETED.getCode().equals(status)) {
            throw new BusinessException(CourtReturnCode.ALREADY_DELETED);
        }

        // 3. 策略執行：依照場地生命週期階段決定刪除方式
        if (CourtStatus.UNDER_REVIEW.getCode().equals(status)) {
            // 刪除營業時段從表
            courtOpenInfoRepository.deleteByCourtId(courtId);
            // 刪除主表紀錄
            courtInfoRepository.delete(courtInfoPo);
            // 呼叫 ImageService 處理資料庫紀錄並同步刪除 S3/MinIO 實體檔案
            courtImageService.deleteByCourtId(courtId);

            return null;
        } else if (CourtStatus.OPEN.getCode().equals(status) || CourtStatus.CLOSED.getCode().equals(status)) {
            // --- 策略 B：軟刪除 (已累積營運資料，僅標記狀態以供後續對帳或查閱) ---

            // 僅變更狀態標籤並更新時間戳
            courtInfoPo.setStatus(CourtStatus.DELETED.getCode());
            courtInfoPo.setUpdatedAt(OffsetDateTime.now());
            courtInfoRepository.save(courtInfoPo);

            // 收集目前相關資訊，作為最後的 Response 回傳 (Snapshot)
            List<CourtOpenInfoPo> openInfos = courtOpenInfoRepository.findByCourtId(courtId);
            // 只抓取今日以後的特殊假日資訊
            List<CourtHolidayInfoPo> holidayPos = courtHolidayInfoRepository.findUpcomingHolidays(courtId, LocalDate.now());
            List<String> imageUrls = courtImageRepository.findByCourtId(courtId)
                    .stream()
                    .map(CourtImagePo::getImageKey)
                    .toList();

            return CourtRs.from(courtInfoPo, openInfos, holidayPos, imageUrls);
        }

        return null;
    }

    /**
     * 動態條件查詢 + 分頁 + 排序
     */
    public Page<CourtRs> searchCourts(Map<String, String> params, String viewMode, Pageable pageable) {

        // 動態建立查詢條件
        Specification<CourtInfoPo> specification = buildSpecification(params, viewMode);

        // 1. 查詢場地主表資料
        Page<CourtInfoPo> poPage = courtInfoRepository.findAll(specification, pageable);

        // 如果沒資料，直接回傳空的 Page
        if (poPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // 取得當前頁面的所有場地 ID (Integer)
        List<String> courtIds = poPage.getContent().stream()
                .map(CourtInfoPo::getCourtId)
                .toList();

        // 2. 批次查詢「開放時間」並依照 CourtId 分群
        Map<String, List<CourtOpenInfoPo>> openTimeMap = courtOpenInfoRepository.findByCourtIdIn(courtIds)
                .stream()
                .collect(Collectors.groupingBy(CourtOpenInfoPo::getCourtId));

        Map<String, List<CourtHolidayInfoPo>> holidayMap = courtHolidayInfoRepository
                .findUpcomingHolidaysByCourtIds(courtIds, LocalDate.now())
                .stream()
                .collect(Collectors.groupingBy(CourtHolidayInfoPo::getCourtId));

        // 3. 批次查詢「圖片」並依照 CourtId 分群
        // 注意：請確認 imageMap 的 Key 型別是否與 courtIds 一致 (Integer)
        Map<String, List<CourtImagePo>> imageMap = courtImageRepository.findByCourtIdIn(courtIds)
                .stream()
                .collect(Collectors.groupingBy(img -> img.getCourtId()));

        // 4. 開始組裝 (利用 Page.map)
        return poPage.map(po -> {
            String id = po.getCourtId();

            // 1. 取得該場地的「開放時間」PO 列表
            List<CourtOpenInfoPo> courtOpenPos = openTimeMap.getOrDefault(id, List.of());

            // 2. 取得該場地的「特殊休息日」PO 列表 (從你剛查好的 holidayMap 拿)
            List<CourtHolidayInfoPo> courtHolidayPos = holidayMap.getOrDefault(id, List.of());

            // 3. 取得該場地的圖片 URL 列表
            List<String> imageUrls = imageMap.getOrDefault(id, List.of())
                    .stream()
                    .map(img -> minioService.getPresignedUrl(img.getImageKey()))
                    .filter(Objects::nonNull)
                    .toList();

            // 4. 傳入 4 個參數給靜態工廠方法
            return CourtRs.from(po, courtOpenPos, courtHolidayPos, imageUrls);
        });
    }

    /**
     * 動態組建球場查詢條件 Specification。
     * 透過前端傳入的參數 map，動態生成對應的 SQL WHERE 子句。
     * @param params 查詢條件參數(ex.name, sportType...)
     * @param viewMode 查看人員分類
     */
    private Specification<CourtInfoPo> buildSpecification(Map<String, String> params, String viewMode) {
        return (root, query, cb) -> {
            // (1) root  : 代表查詢的根實體 (CourtInfoPo)，用於獲取資料表欄位。
            // (2) query : 用於封裝查詢語句，可處理 DISTINCT, GROUP BY 等。
            // (3) cb    : CriteriaBuilder，JPA 提供的 SQL 語句工廠，負責生產 like, equal 等比較條件。
            List<Predicate> predicates = new ArrayList<>();

            if ("ADMIN".equals(viewMode)) {
                // 管理者：不加 status 限制，或者根據 params 傳什麼查什麼
                if (params.containsKey("status")) {
                    predicates.add(cb.equal(root.get("status"), params.get("status")));
                }
            } else {
                // 2. 使用者與團主：安全邊界控制
                if (params.containsKey("status")) {
                    int requestedStatus = Integer.parseInt(params.get("status"));

                    // 若一般人/團主想看 status != 2or3 ，強制擋掉，若有傳則使用他傳的為主
                    if (!List.of(2, 3).contains(requestedStatus)) {
                        predicates.add(cb.equal(root.get("status"), -1));
                    } else {
                        predicates.add(cb.equal(root.get("status"), requestedStatus));
                    }
                } else {
                    predicates.add(root.get("status").in(List.of(2, 3)));
                }
            }

            // name: 模糊查詢
            String name = params.get("name");
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }

            // category: 精確查詢
            String category = params.get("category");
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            // sportType: 精確查詢
            String sportType = params.get("sportType");
            if (sportType != null && !sportType.isBlank()) {
                predicates.add(cb.equal(root.get("sportType"), sportType));
            }

            // status: 精確查詢
            String status = params.get("status");
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), Integer.parseInt(status)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 更新球場完整資訊
     * 1. 驗證場地 ID 是否存在
     * 2. 更新球場主表資訊並記錄更新時間
     * 3. 處理開放時間：刪除舊資料後重新建立 (UUID)
     * 4. 處理特殊休息日：刪除舊資料後重新建立 (TSID)
     * 5. 處理圖片同步：執行差異化更新
     * 6. 重新封裝回傳結果，包含預簽名網址
     *
     * @param courtId 球場識別碼
     * @param rq 更新請求物件
     * @return 更新後的球場結果
     */
    @Transactional
    public CourtRs update(String courtId, UpdateCourtRq rq) {
        // 1. 驗證場地是否存在
        CourtInfoPo po = courtInfoRepository.findById(courtId)
                .orElseThrow(() -> new BusinessException(CourtReturnCode.COURT_NOT_FOUND));

        // 2. 更新主表資訊
        courtMapper.updatePoFromRq(rq, po);
        po.setUpdatedAt(OffsetDateTime.now());
        courtInfoRepository.save(po);

        // 3. 處理開放時間更新
        if (rq.getOpenTimeList() != null) {
            courtOpenInfoRepository.deleteByCourtId(courtId);
            List<CourtOpenInfoPo> newOpens = courtMapper.toOpenPoList(rq.getOpenTimeList());
            newOpens.forEach(o -> {
                o.setId(UUID.randomUUID().toString());
                o.setCourtId(courtId);
            });
            courtOpenInfoRepository.saveAll(newOpens);
            courtOpenInfoRepository.flush();
        }

        if (rq.getFixedHolidayList() != null) {
            // (1) 刪除舊的
            courtHolidayInfoRepository.deleteByCourtId(courtId);
            courtHolidayInfoRepository.flush();

            List<CourtHolidayInfoPo> holidayPoList = rq.getFixedHolidayList().stream().map(holidayRq ->
                    CourtHolidayInfoPo.builder()
                            .courtHolidayId(TSID.fast().toString())
                            .courtId(courtId)
                            .holidayDate(LocalDate.parse(holidayRq.getDate()))
                            .startTime(holidayRq.getStartTime() != null ? LocalTime.parse(holidayRq.getStartTime()) : null)
                            .endTime(holidayRq.getEndTime() != null ? LocalTime.parse(holidayRq.getEndTime()) : null)
                            .description(holidayRq.getDescription())
                            .build()
            ).toList();

            // (3) 儲存新資料
            courtHolidayInfoRepository.saveAll(holidayPoList);
        }

        // 4. 處理圖片清單更新，只針對不同的做更新
        if (rq.getImageKeys() != null) {
            this.syncCourtImages(courtId, rq.getImageKeys());
        }

        // 5. 準備回傳資料
        List<CourtOpenInfoPo> openTimeList = courtOpenInfoRepository.findByCourtId(courtId);
        List<CourtImagePo> courtImagePoList = courtImageRepository.findByCourtId(courtId);

        // 6. 核心轉換：將資料庫存的 imageKey (相對路徑) 轉換為具有時效性的 MinIO 預簽名 URL
        List<String> imageUrlList = courtImagePoList.stream()
                .map(img -> minioService.getPresignedUrl(img.getImageKey()))
                .filter(Objects::nonNull) // 過濾生成失敗的網址，Objects::nonNull 需要 import java.util.Objects
                .toList();

        // 7. 組裝並回傳最終結果 (包含主資訊、營業時間與圖片網址)
        return courtMapper.toRs(po, openTimeList, imageUrlList);
    }

    /**
     * 差集同步圖片邏輯：只針對變動的部分進行資料庫操作
     */
    private void syncCourtImages(String courtId, List<String> requestKeys) {
        // 1. 確保前端傳入的 Key 是唯一的，避免重複處理
        Set<String> uniqueRequestKeys = (requestKeys == null) ? new HashSet<>() : new HashSet<>(requestKeys);

        // 2. 取得資料庫目前的所有紀錄與 Key
        List<CourtImagePo> currentImages = courtImageRepository.findByCourtId(courtId);
        List<String> currentKeys = currentImages.stream()
                .map(CourtImagePo::getImageKey)
                .toList();

        // 3. 找出需要「刪除」的 (DB 有，但前端傳進來的 Set 沒有)
        List<String> keysToDelete = currentKeys.stream()
                .filter(key -> !uniqueRequestKeys.contains(key))
                .toList();

        if (!keysToDelete.isEmpty()) {
            // 批次從資料庫移除關聯
            courtImageRepository.deleteByCourtIdAndImageKeyIn(courtId, keysToDelete);

            /* * 注意：關於從 MinIO/S3 刪除實體檔案：
             * 建議在事務成功提交後再執行，或由後台排程清理孤兒檔案。
             * 若要現在刪除，請確保 minioService.deleteFiles(keysToDelete) 不會拋出異常影響 DB 回滾。
             */
        }

        // 4. 找出需要「新增」的 (前端傳進來的 Set 有，但 DB 紀錄沒出現過)
        List<String> keysToAdd = uniqueRequestKeys.stream()
                .filter(key -> !currentKeys.contains(key))
                .toList();

        if (!keysToAdd.isEmpty()) {
            List<CourtImagePo> newImages = keysToAdd.stream()
                    .map(key -> CourtImagePo.builder()
                            .courtId(courtId)
                            .imageKey(key)
                            .build())
                    .toList();
            courtImageRepository.saveAll(newImages);
        }
    }

    public CourtRs getCourtDetail(String courtId) {
        // 1. 查詢基本資料
        CourtInfoPo courtInfo = courtInfoRepository.findById(courtId)
                .orElseThrow(() -> new BusinessException(CourtReturnCode.COURT_NOT_FOUND));

        // 2. 查詢開放時間
        List<CourtOpenInfoPo> openInfos = courtOpenInfoRepository.findByCourtId(courtId);

        // 3. 查詢圖片 (這裡我幫你用了你之前的 Minio 預簽名網址邏輯，這樣比較安全)
        List<String> imageUrls = courtImageRepository.findByCourtId(courtId)
                .stream()
                .map(img -> minioService.getPresignedUrl(img.getImageKey()))
                .filter(Objects::nonNull)
                .toList();

        // 4. 查詢特殊休息日
        List<CourtHolidayInfoPo> holidayPos = courtHolidayInfoRepository.findUpcomingHolidays(courtId, LocalDate.now());

        return CourtRs.from(courtInfo, openInfos, holidayPos, imageUrls);
    }


}
