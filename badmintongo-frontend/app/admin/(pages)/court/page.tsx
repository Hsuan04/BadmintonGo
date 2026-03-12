 "use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { FancySelect, type SelectOption } from "@/components/fancy-select";

type CourtItem = {
  id: number | string;
  courtId?: number | string;
  name: string;
  category?: string;
  sportType?: number;
  sportTypeLabel?: string;
  address?: string;
  status?: string;
};

type CourtsPageData = {
  content: CourtItem[];
  totalPages: number;
  totalElements: number;
  number: number; // current page index (0-based)
};

type SortField = "name" | "category" | "sportType" | "status";
type SortDirection = "asc" | "desc";

const PAGE_SIZE = 10;

export default function CourtsListPage() {
  const [nameFilter, setNameFilter] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("");
  const [sportTypeFilter, setSportTypeFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const [page, setPage] = useState(0);
  const [sortField, setSortField] = useState<SortField>("name");
  const [sortDirection, setSortDirection] = useState<SortDirection>("asc");

  const [data, setData] = useState<CourtsPageData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const sortParam = useMemo(
    () => `${sortField},${sortDirection}`,
    [sortField, sortDirection],
  );

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedCourtId, setSelectedCourtId] = useState<number | string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchCourts = async () => {
    setLoading(true);
    setError(null);

    try {
      const params = new URLSearchParams();
      if (nameFilter.trim()) params.set("name", nameFilter.trim());
      if (categoryFilter) params.set("category", categoryFilter);
      if (sportTypeFilter) params.set("sportType", sportTypeFilter);
      if (statusFilter) params.set("status", statusFilter);

      params.set("page", String(page));
      params.set("size", String(PAGE_SIZE));
      params.set("sort", sortParam);
      params.set("viewMode", "ADMIN");

      const res = await fetch(`/api/courts?${params.toString()}`);
      if (!res.ok) {
        throw new Error("載入球場列表失敗。");
      }

      const json = await res.json();
      const pageData: CourtsPageData = json.data;

      setData(pageData);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "發生未知錯誤，請稍後再試。";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  // 初次載入與條件變更時自動查詢
  useEffect(() => {
    fetchCourts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, sortParam, nameFilter, categoryFilter, sportTypeFilter, statusFilter]);

  const handleSearchClick = () => {
    setPage(0);
    fetchCourts();
  };

  const handleReset = () => {
    setNameFilter("");
    setCategoryFilter("");
    setSportTypeFilter("");
    setStatusFilter("");
    setSortField("name");
    setSortDirection("asc");
    setPage(0);
  };

  const handleToggleSortDirection = () => {
    setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
    setPage(0);
  };

  const handlePrevPage = () => {
    setPage((prev) => Math.max(prev - 1, 0));
  };

  const handleNextPage = () => {
    if (totalPages === 0) return;
    setPage((prev) => Math.min(prev + 1, totalPages - 1));
  };

  const currentContent = data?.content ?? [];

  const handleDeleteCourt = async (courtId: number | string) => {
    // 1. 原生確認對話框 (如果你有 UI Modal 組件可以替換它)
    const confirmed = window.confirm("確定要刪除此場地資料嗎？此操作將使場地狀態變更為「已刪除」。");
    
    if (!confirmed) return;

    try {
      setLoading(true);
      const res = await fetch(`/api/courts/${courtId}`, {
        method: "DELETE",
      });

      if (!res.ok) {
        throw new Error("刪除失敗，請稍後再試。");
      }

      // 2. 刪除成功後，重新抓取清單或手動過濾掉該筆資料
      alert("場地已成功刪除");
      fetchCourts(); 
    } catch (err) {
      alert(err instanceof Error ? err.message : "發生未知錯誤");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">球場管理</h1>
          <p className="mt-1 text-sm text-slate-500">
            管理所有球場的基本資訊、開放狀態與營運設定。
          </p>
        </div>
        <Link
          href="/admin/courts/detail"
          className="inline-flex items-center justify-center rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700"
        >
          新增球場
        </Link>
      </div>

      {/* Filter bar */}
<div className="grid gap-3 rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-700 md:grid-cols-[minmax(0,2fr)_minmax(0,1.2fr)_minmax(0,1.2fr)_minmax(0,1.2fr)_auto] md:items-end">
  
  {/* 球場名稱輸入框 (保持不變) */}
  <div className="space-y-1.5">
  {/* 調整這裡的 Label 樣式以符合 FancySelect */}
  <label 
    htmlFor="nameFilter" 
    className="text-[13px] font-medium text-slate-800"
  >
    球場名稱
  </label>
  
  <input
    id="nameFilter"
    type="text"
    value={nameFilter}
    onChange={(e) => {
      setPage(0);
      setNameFilter(e.target.value);
    }}
    className="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400"
    placeholder="輸入關鍵字..."
  />
</div>

  {/* 1. 球場類別 - 套用 FancySelect */}
  <FancySelect
    label="球場類別"
    options={[
      { value: "室內木板", label: "室內木板" },
      { value: "室內 PU", label: "室內 PU" },
      { value: "室外硬地", label: "室外硬地" },
      { value: "室外 PU", label: "室外 PU" },
    ]}
    value={categoryFilter}
    onChange={(val) => {
      setPage(0);
      setCategoryFilter(val);
    }}
    placeholder="全部"
    clearable
  />

  {/* 2. 運動類型 - 套用 FancySelect */}
  <FancySelect
    label="運動類型"
    options={[
      { value: "1", label: "羽球" },
      { value: "2", label: "籃球" },
      { value: "3", label: "網球" },
      { value: "4", label: "桌球" },
    ]}
    value={sportTypeFilter}
    onChange={(val) => {
      setPage(0);
      setSportTypeFilter(val);
    }}
    placeholder="全部"
    clearable
  />

  {/* 3. 狀態 - 套用 FancySelect */}
  <FancySelect
    label="狀態"
    options={[
      { value: "1", label: "審核中" },
      { value: "2", label: "開放中" },
      { value: "3", label: "關閉中" },
      { value: "4", label: "已刪除" },
    ]}
    value={statusFilter}
    onChange={(val) => {
      setPage(0);
      setStatusFilter(val);
    }}
    placeholder="全部"
    clearable
  />

  {/* 按鈕組 */}
  <button type="button" onClick={handleReset} className="cursor-pointer rounded-lg border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-100">
    重置
  </button>

  
</div>
      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        <div className="flex items-center justify-between border-b border-slate-200 bg-slate-50 px-4 py-3 text-xs text-slate-500">
          <span>
            共 {totalElements} 筆球場資料，頁碼 {totalPages > 0 ? page + 1 : 0}/
            {totalPages}
          </span>

          <div className="flex items-center gap-2">
  <span className="text-[11px] uppercase tracking-[0.16em] text-slate-400">
    排序
  </span>
  <FancySelect
    options={[
      { value: "name", label: "球場名稱" },
      { value: "category", label: "類別" },
      { value: "sportType", label: "運動類型" },
      { value: "status", label: "狀態" },
    ]}
    value={sortField}
    onChange={(val) => {
      setSortField(val as SortField);
      setPage(0);
    }}
    className="w-32" // 設定一個固定的寬度，避免它撐開
    placeholder="選擇欄位"
    // 這裡不傳 label，它就不會顯示標題
  />
  <button
    type="button"
    onClick={handleToggleSortDirection}
    className="inline-flex h-10 w-10 cursor-pointer items-center justify-center rounded-xl border border-slate-200 bg-white text-[11px] font-semibold text-slate-700 hover:bg-slate-100 shadow-sm"
    aria-label="切換排序方向"
  >
    {sortDirection === "asc" ? "↑" : "↓"}
  </button>
</div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200 text-sm">
            <thead className="bg-slate-50">
              <tr>
                <th className="w-10 px-3 py-2 text-left text-xs font-medium uppercase tracking-[0.12em] text-slate-500">
                  操作
                </th>
                <th className="whitespace-nowrap px-4 py-2 text-left text-xs font-medium uppercase tracking-[0.12em] text-slate-500">
                  球場名稱
                </th>
                <th className="whitespace-nowrap px-4 py-2 text-left text-xs font-medium uppercase tracking-[0.12em] text-slate-500">
                  類別
                </th>
                <th className="whitespace-nowrap px-4 py-2 text-left text-xs font-medium uppercase tracking-[0.12em] text-slate-500">
                  運動類型
                </th>
                <th className="whitespace-nowrap px-4 py-2 text-left text-xs font-medium uppercase tracking-[0.12em] text-slate-500">
                  地址
                </th>
                <th className="whitespace-nowrap px-4 py-2 text-left text-xs font-medium uppercase tracking-[0.12em] text-slate-500">
                  狀態
                </th>
                {/* 放刪除按鈕的欄位 */}
                <th className="w-12 px-3 py-2"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
  {/* 1. 狀態提示區：colSpan 全部改為 7 */}
  {loading && (
    <tr>
      <td colSpan={7} className="px-4 py-6 text-center text-sm text-slate-500">
        載入中...
      </td>
    </tr>
  )}
  {!loading && error && (
    <tr>
      <td colSpan={7} className="px-4 py-6 text-center text-sm text-rose-600">
        {error}
      </td>
    </tr>
  )}
  {!loading && !error && currentContent.length === 0 && (
    <tr>
      <td colSpan={7} className="px-4 py-6 text-center text-sm text-slate-500">
        目前沒有符合條件的球場。
      </td>
    </tr>
  )}

  {/* 2. 資料列區 */}
  {!loading && !error && currentContent.map((court, index) => {
    const uniqueKey = court.id || court.courtId || index;

    return (
      <tr key={uniqueKey} className="hover:bg-slate-50/80">
        {/* 第一欄：編輯按鈕 */}
        <td className="px-3 py-2.5">
          <Link
            href={`/admin/courts/detail?id=${uniqueKey}`}
            className="inline-flex h-7 w-7 items-center justify-center rounded-md border border-slate-200 bg-white text-xs text-slate-600 hover:bg-slate-100 transition-colors"
          >
            ✎
          </Link>
        </td>

        <td className="whitespace-nowrap px-4 py-2.5">
          <Link
            href={`/admin/courts/detail?id=${uniqueKey}`}
            className="text-sm font-medium text-blue-600 hover:text-blue-700"
          >
            {court.name}
          </Link>
        </td>

        <td className="whitespace-nowrap px-4 py-2.5 text-sm text-slate-700">
          {court.category ?? "-"}
        </td>

        <td className="whitespace-nowrap px-4 py-2.5 text-sm text-slate-700">
          {court.sportTypeLabel ?? (court.sportType === 1 ? "羽球" : "-")}
        </td>

        <td className="px-4 py-2.5 text-sm text-slate-500">
          {court.address ?? "-"}
        </td>

        {/* 狀態標籤欄位 */}
        <td className="whitespace-nowrap px-4 py-2.5 text-sm">
          {(() => {
            const statusConfig: Record<string, { label: string; classes: string }> = {
              "1": { label: "審核中", classes: "bg-blue-50 text-blue-700 ring-blue-600/20" },
              "2": { label: "開放中", classes: "bg-emerald-50 text-emerald-700 ring-emerald-600/20" },
              "3": { label: "關閉中", classes: "bg-amber-50 text-amber-700 ring-amber-600/20" },
              "4": { label: "已刪除", classes: "bg-rose-50 text-rose-700 ring-rose-600/20" },
            };
            const config = statusConfig[String(court.status)] || { label: "-", classes: "bg-slate-50 text-slate-600" };
            return (
              <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${config.classes}`}>
                {config.label}
              </span>
            );
          })()}
        </td>

        {/*  這裡是最後一欄：放置刪除按鈕 */}
        <td className="px-3 py-2.5 text-right">
          <button
            type="button"
            onClick={() => handleDeleteCourt(uniqueKey)}
            className="inline-flex h-7 w-7 cursor-pointer items-center justify-center rounded-full bg-rose-50 text-[11px] font-semibold text-rose-600 hover:bg-rose-100 transition-colors"
            aria-label="刪除球場"
          >
            ✕
          </button>
        </td>
      </tr>
    );
  })}
</tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-end gap-3 border-t border-slate-200 bg-white px-4 py-3 text-xs text-slate-600">
          <span>
            每頁 {PAGE_SIZE} 筆，第 {totalPages > 0 ? page + 1 : 0} / {totalPages}{" "}
            頁
          </span>
          <div className="flex items-center gap-1.5">
            <button
              type="button"
              onClick={handlePrevPage}
              disabled={page === 0 || totalPages === 0}
              className="inline-flex cursor-pointer items-center rounded-md border border-slate-200 bg-white px-2 py-1 text-[11px] font-medium hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
            >
              上一頁
            </button>
            <button
              type="button"
              onClick={handleNextPage}
              disabled={totalPages === 0 || page >= totalPages - 1}
              className="inline-flex cursor-pointer items-center rounded-md border border-slate-200 bg-white px-2 py-1 text-[11px] font-medium hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
            >
              下一頁
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}


