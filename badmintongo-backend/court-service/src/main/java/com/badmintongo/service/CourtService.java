package com.badmintongo.service;

import com.badmintongo.bean.po.CourtImagePo;
import com.badmintongo.bean.po.CourtInfoPo;
import com.badmintongo.bean.po.CourtOpenInfoPo;
import com.badmintongo.bean.vo.CourtRs;
import com.badmintongo.bean.vo.CreateCourtRq;
import com.badmintongo.bean.vo.UpdateCourtRq;
import com.badmintongo.enums.CourtReturnCode;
import com.badmintongo.exception.BusinessException;
import com.badmintongo.mapper.CourtMapper;
import com.badmintongo.repository.CourtImageRepository;
import com.badmintongo.repository.CourtInfoRepository;
import com.badmintongo.repository.CourtOpenInfoRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtInfoRepository courtInfoRepository;
    private final CourtOpenInfoRepository courtOpenInfoRepository;
    private final CourtImageRepository courtImageRepository;
    private final MinioService minioService;
    private final CourtMapper courtMapper;

    /**
     * 新增
     */
    @Transactional
    public CourtRs create(CreateCourtRq courtRq) {

        // 1. 業務驗證
        if (courtInfoRepository.existsByName(courtRq.getName())) {
            throw new BusinessException(CourtReturnCode.DUPLICATE_NAME);
        }

        // 2. 建立主表 PO (使用 Builder 代替 BeanUtils)
        CourtInfoPo courtPo = CourtInfoPo.builder()
                .name(courtRq.getName())
                .category(courtRq.getCategory())
                .sportType(courtRq.getSportType())
                .address(courtRq.getAddress())
                .description(courtRq.getDescription())
                .status(1)   // 預設(審核中)
                .createdAt(OffsetDateTime.now())
                .build();

        CourtInfoPo savedCourt = courtInfoRepository.save(courtPo);

        // 3. 建立從表 PO List (同樣使用 Builder)
        List<CourtOpenInfoPo> savedOpenPos = new ArrayList<>();
        if (courtRq.getOpenTimeList() != null) {
            List<CourtOpenInfoPo> openPoList = courtRq.getOpenTimeList().stream().map(openBo ->
                    CourtOpenInfoPo.builder()
                            .courtId(savedCourt.getCourtId())
                            .dayOfWeek(openBo.getDayOfWeek())
                            .isOpen(openBo.getIsOpen())
                            .openTime(LocalTime.parse(openBo.getOpenTime()))
                            .closeTime(LocalTime.parse(openBo.getCloseTime()))
                            .build()
            ).toList();

            savedOpenPos = courtOpenInfoRepository.saveAll(openPoList);
        }

        // 4. 使用 Rs 內建的靜態工廠轉換並回傳
        return CourtRs.from(savedCourt, savedOpenPos, List.of());
    }

    @Transactional
    public CourtRs softDelete(Integer id) {
        // 1. 查詢並直接檢查是否存在
        CourtInfoPo courtInfoPo = courtInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CourtReturnCode.COURT_NOT_FOUND));

        // 2. 檢查是否已經是刪除狀態
        if (Integer.valueOf(4).equals(courtInfoPo.getStatus())) {
            throw new BusinessException(CourtReturnCode.ALREADY_DELETED);
        }

        // 3. 執行邏輯刪除並儲存
        courtInfoPo.setStatus(4);
        courtInfoPo.setUpdatedAt(OffsetDateTime.now());
        courtInfoRepository.save(courtInfoPo);

        List<CourtOpenInfoPo> openInfos = courtOpenInfoRepository.findByCourtIdIn(List.of(id));
        List<String> imageUrls = courtImageRepository.findByCourtIdIn(List.of(courtInfoPo.getCourtId()))
                .stream()
                .map(CourtImagePo::getImageKey) // 假設你的圖片欄位叫 imageKey
                .toList();

        return CourtRs.from(courtInfoPo, openInfos, imageUrls);
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
        List<Integer> courtIds = poPage.getContent().stream()
                .map(CourtInfoPo::getCourtId)
                .toList();

        // 2. 批次查詢「開放時間」並依照 CourtId 分群
        Map<Integer, List<CourtOpenInfoPo>> openTimeMap = courtOpenInfoRepository.findByCourtIdIn(courtIds)
                .stream()
                .collect(Collectors.groupingBy(CourtOpenInfoPo::getCourtId));

        // 3. 批次查詢「圖片」並依照 CourtId 分群
        // 注意：請確認 imageMap 的 Key 型別是否與 courtIds 一致 (Integer)
        Map<Integer, List<CourtImagePo>> imageMap = courtImageRepository.findByCourtIdIn(courtIds)
                .stream()
                .collect(Collectors.groupingBy(img -> img.getCourtId().intValue()));

        // 4. 開始組裝 (利用 Page.map)
        return poPage.map(po -> {
            Integer id = po.getCourtId();

            // 取得該場地的開放時間 PO 列表
            List<CourtOpenInfoPo> courtOpenPos = openTimeMap.getOrDefault(id, List.of());

            // 取得該場地的圖片 URL 列表 (轉換 Key 為預簽名網址)
            List<String> imageUrls = imageMap.getOrDefault(id, List.of())
                    .stream()
                    .map(img -> minioService.getPresignedUrl(img.getImageKey()))
                    .filter(Objects::nonNull)
                    .toList();

            // 統一調用 CourtRs 的靜態轉換方法
            // 此方法內部會處理 OpenTimeRs 的轉換邏輯
            return CourtRs.from(po, courtOpenPos, imageUrls);
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

    public static CourtRs from(CourtInfoPo po, List<CourtOpenInfoPo> openPos, List<String> imageUrls) {
        return new CourtRs(
                po.getCourtId(),
                po.getName(),
                po.getCategory(),
                po.getSportType(),
                po.getAddress(),
                po.getDescription(),
                imageUrls, // 如果是刪除或新增，這裡傳 List.of() 或 null
                po.getStatus(),
                po.getCreatedAt(),
                openPos == null ? List.of() : openPos.stream().map(o ->
                        new CourtRs.OpenTimeRs(
                                o.getDayOfWeek(),
                                o.getIsOpen(),
                                o.getOpenTime().toString(),
                                o.getCloseTime().toString()
                        )
                ).toList()
        );
    }

    /**
     * 更新
     */
    @Transactional
    public CourtRs update(Integer courtId, UpdateCourtRq rq) {
        // 1. 驗證場地是否存在
        CourtInfoPo po = courtInfoRepository.findById(courtId)
                .orElseThrow(() -> new BusinessException(CourtReturnCode.COURT_NOT_FOUND));

        // 2. 更新主表資訊
        courtMapper.updatePoFromRq(rq, po);
        po.setUpdatedAt(OffsetDateTime.now());
        courtInfoRepository.save(po);

        // 3. 處理開放時間更新 (這裡你也可以考慮改用差集更新，目前整組替換 OK)
        if (rq.getOpenTimeList() != null) {
            courtOpenInfoRepository.deleteByCourtId(courtId);
            List<CourtOpenInfoPo> newOpens = courtMapper.toOpenPoList(rq.getOpenTimeList());
            newOpens.forEach(o -> o.setCourtId(courtId));
            courtOpenInfoRepository.saveAll(newOpens);
        }

        // 4. 處理圖片清單更新 (改用私有方法進行「差集同步」)
        if (rq.getImageKeys() != null) {
            this.syncCourtImages(courtId, rq.getImageKeys());
        }

        // 5. 準備回傳資料 (重新查詢資料庫確保回傳內容與資料庫同步)
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
    private void syncCourtImages(Integer courtId, List<String> requestKeys) {
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


}
