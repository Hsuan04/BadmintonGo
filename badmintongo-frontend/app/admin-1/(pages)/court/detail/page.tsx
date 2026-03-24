 "use client";

import {useEffect, useState} from "react";
import { useRouter } from "next/navigation";
import { FancySelect, type SelectOption } from "@/components/fancy-select";
import TimePicker, { type TimeValue } from "@/components/time-picker";

type OpenTimeItem = {
  dayOfWeek: number;
  isOpen: boolean;
  openTime: string;
  closeTime: string;
};

type FixedHoliday = {
  date: string;
  description: string;
};

const dayOfWeekOptions: { value: number; label: string }[] = [
  { value: 1, label: "星期一" },
  { value: 2, label: "星期二" },
  { value: 3, label: "星期三" },
  { value: 4, label: "星期四" },
  { value: 5, label: "星期五" },
  { value: 6, label: "星期六" },
  { value: 7, label: "星期日" },
];

export default function CourtDetailPage() {
    const router = useRouter();

    const [name, setName] = useState("");
    const [category, setCategory] = useState("");
    const [sportType, setSportType] = useState<string>("");
    const [status, setStatus] = useState<string>("");
    //下拉選單
    const [categoryOptions, setCategoryOptions] = useState<SelectOption[]>([]);
    const [sportTypeOptions, setSportTypeOptions] = useState<SelectOption[]>([]);
    const [statusOptions, setStatusOptions] = useState<SelectOption[]>([]);
    const [address, setAddress] = useState("");
    const [url, setUrl] = useState("");
    const [description, setDescription] = useState("");
    const [openTimeList, setOpenTimeList] = useState<OpenTimeItem[]>(
        dayOfWeekOptions.map((opt) => ({
            dayOfWeek: opt.value,
            isOpen: true,
            openTime: "06:00:00",
            closeTime: "22:00:00",
        })),
    );
    const [files, setFiles] = useState<File[]>([]);
    const [previewUrls, setPreviewUrls] = useState<string[]>([]);
    const [fileMessage, setFileMessage] = useState<string | null>(null);
    const [fixedHolidays, setFixedHolidays] = useState<FixedHoliday[]>([]);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    useEffect(() => {
        const fetchAllOptions = async () => {
            try {
                const [catRes, sportRes, statRes] = await Promise.all([
                    fetch('http://localhost:8086/api/common/config/COURT_CATEGORY').then(res => res.json()),
                    fetch('http://localhost:8086/api/common/config/SPORT_TYPE').then(res => res.json()),
                    fetch('http://localhost:8086/api/common/config/COURT_STATUS').then(res => res.json()),
                ]);

                if (catRes.code !== "200" || sportRes.code !== "200" || statRes.code !== "200") {
                    throw new Error("部分選單資料載入異常");
                }

                setCategoryOptions(catRes.data);
                setSportTypeOptions(sportRes.data);

                // 💡 修正點：確保 opt.value 轉為字串進行匹配
                const statusWithIcons = statRes.data.map((opt: SelectOption) => {
                    const val = String(opt.value);
                    const colorMap: Record<string, string> = {
                        "1": "bg-blue-500",    // 審核中
                        "2": "bg-emerald-500", // 開放
                        "3": "bg-amber-500",   // 關閉
                        "4": "bg-rose-500",    // 已刪除
                    };

                    return {
                        ...opt,
                        icon: (
                            <span className={`flex h-2 w-2 shrink-0 rounded-full ${colorMap[val] || "bg-slate-400"}`} />
                        ),
                    };
                });

                setStatusOptions(statusWithIcons);

                if (statusWithIcons.length > 0 && !status) {
                    setStatus(String(statusWithIcons[0].value));
                }

            } catch (err) {
                console.error("初始化選單失敗:", err);
            }
        };

        fetchAllOptions();
    }, []);

    const handleUpdateOpenTime = (
        index: number,
        partial: Partial<OpenTimeItem>,
    ) => {
        setOpenTimeList((prev) =>
            prev.map((item, i) => (i === index ? {...item, ...partial} : item)),
        );
    };

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const fileList = Array.from(event.target.files ?? []);

        const limited = fileList.slice(0, 10);
        if (fileList.length > 10) {
            setFileMessage("最多只能上傳 10 張圖片，已自動保留前 10 張。");
        } else {
            setFileMessage(null);
        }

        // 釋放舊的預覽 URL
        if (previewUrls.length > 0) {
            previewUrls.forEach((url) => URL.revokeObjectURL(url));
        }

        const nextPreviewUrls = limited.map((file) => URL.createObjectURL(file));

        setFiles(limited);
        setPreviewUrls(nextPreviewUrls);
    };

    const handleAddHoliday = () => {
        setFixedHolidays((prev) => [
            ...prev,
            {
                date: "",
                description: "",
            },
        ]);
    };

    const handleUpdateHoliday = (
        index: number,
        partial: Partial<FixedHoliday>,
    ) => {
        setFixedHolidays((prev) =>
            prev.map((item, i) => (i === index ? {...item, ...partial} : item)),
        );
    };

    const handleRemoveHoliday = (index: number) => {
        setFixedHolidays((prev) => prev.filter((_, i) => i !== index));
    };

    const handleRemoveImage = (index: number) => {
        setFiles((prev) => prev.filter((_, i) => i !== index));
        setPreviewUrls((prev) => {
            const next = [...prev];
            const [removed] = next.splice(index, 1);
            if (removed) URL.revokeObjectURL(removed);
            return next;
        });
    };

    const toTimeValue = (time: string): TimeValue | undefined => {
        if (!time) return undefined;
        const [hStr, mStr] = time.split(":");
        const hour24 = Number(hStr);
        const minute = Number(mStr);
        if (Number.isNaN(hour24) || Number.isNaN(minute)) return undefined;
        const period: "AM" | "PM" = hour24 >= 12 ? "PM" : "AM";
        const hour12 = hour24 % 12 === 0 ? 12 : hour24 % 12;
        return {hour: hour12, minute, period};
    };

    const fromTimeValue = (
        value: TimeValue | undefined,
        fallback: string,
    ): string => {
        if (!value) return fallback;
        const baseHour =
            value.period === "PM"
                ? value.hour === 12
                    ? 12
                    : value.hour + 12
                : value.hour === 12
                    ? 0
                    : value.hour;
        const hh = String(baseHour).padStart(2, "0");
        const mm = String(value.minute).padStart(2, "0");
        return `${hh}:${mm}:00`;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        const trimmedName = name.trim();
        const trimmedCategory = category.trim();
        const trimmedAddress = address.trim();

        if (
            !trimmedName ||
            !trimmedCategory ||
            !sportType ||
            !trimmedAddress
        ) {
            setError("請填寫所有標示 * 的必填欄位。");
            return;
        }

        setSaving(true);

        try {
            const payload = {
                name: trimmedName,
                category: trimmedCategory,
                sportType: sportType,
                status: status,
                address: trimmedAddress,
                url: url.trim(),
                description: description.trim(),
                openTimeList: openTimeList.map((item) => ({
                    dayOfWeek: item.dayOfWeek,
                    isOpen: item.isOpen,
                    openTime: item.openTime,
                    closeTime: item.closeTime,
                })),
                fixedHolidayList: fixedHolidays
                    .filter((h) => h.date)
                    .map((h) => ({
                        date: h.date,
                        description: h.description.trim() || null,
                    })),
            };

            const createRes = await fetch("/api/court", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });

            if (!createRes.ok) {
                throw new Error("建立球場資料失敗。");
            }

            const json = await createRes.json();
            const courtId = json.id ?? json.courtId;

            if (!courtId) {
                throw new Error("回傳資料中沒有球場 ID。");
            }

            if (files.length > 0) {
                const formData = new FormData();
                files.forEach((file) => {
                    formData.append("files", file);
                });

                const uploadRes = await fetch(`/api/court/${courtId}/images`, {
                    method: "POST",
                    body: formData,
                });

                if (!uploadRes.ok) {
                    throw new Error("圖片上傳失敗。");
                }
            }

            setSuccess("儲存成功！");
            setTimeout(() => {
                router.push("/admin/court");
            }, 600);
        } catch (err) {
            const message =
                err instanceof Error ? err.message : "儲存失敗，請稍後再試。";
            setError(message);
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 className="text-xl font-semibold text-slate-900">球場基本資料</h1>
                    <p className="mt-1 text-sm text-slate-500">
                        請填寫球場的名稱、類別、運動類型與開放時間等資訊。
                    </p>
                </div>
            </div>

            <form
                onSubmit={handleSubmit}
                className="space-y-6 rounded-xl border border-slate-200 bg-white p-5 shadow-sm"
            >
                {error ? (
                    <div className="rounded-lg border border-rose-200 bg-rose-50 px-3.5 py-2.5 text-sm text-rose-700">
                        {error}
                    </div>
                ) : null}
                {success ? (
                    <div
                        className="rounded-lg border border-emerald-200 bg-emerald-50 px-3.5 py-2.5 text-sm text-emerald-700">
                        {success}
                    </div>
                ) : null}

                <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-2">
                        <label htmlFor="name" className="text-sm font-medium text-slate-800">* 球場名稱</label>
                        <input id="name" type="text" value={name} className="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400" placeholder="" onChange={(e) => setName(e.target.value)}/>
                    </div>
                    <div className="space-y-2">
                        <FancySelect label="* 場館類型" options={categoryOptions} value={category} onChange={setCategory} placeholder="請選擇類別" clearable/>
                    </div>

                    <div className="space-y-2">
                        <FancySelect label="* 運動類型" options={sportTypeOptions} value={sportType} onChange={setSportType} placeholder="請選擇運動類型" clearable/>
                    </div>

                    <div className="space-y-2">
                        <FancySelect label="* 場館狀態" options={statusOptions} value={status} onChange={setStatus} placeholder="請選擇狀態" clearable/>
                    </div>

                    <div className="space-y-2">
                        <label htmlFor="address" className="text-sm font-medium text-slate-800">* 地址</label>
                        <input id="address" type="text" value={address} className="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400" placeholder="例如：台北市大安區羅斯福路四段 1 號"
                            onChange={(e) => setAddress(e.target.value)}/>
                    </div>

                    <div className="space-y-2">
                        <label htmlFor="url" className="text-sm font-medium text-slate-800">網站 URL</label>
                        <input id="url" type="text" value={url} className="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400" placeholder="例如：https://ntusportscenter.ntu.edu.tw/"
                            onChange={(e) => setUrl(e.target.value)}/>
                    </div>
                </div>

                <div className="space-y-2">
                    <label htmlFor="description" className="text-sm font-medium text-slate-800">描述</label>
                    <textarea id="description" rows={3} value={description} className="block w-full min-h-[56px] rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400" placeholder="例如：位於體育館三樓，光線充足，備有空調。"
                        onChange={(e) => setDescription(e.target.value)}/>
                </div>

                <div className="space-y-3">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-sm font-semibold text-slate-900">開放時間設定</h2>
                            <p className="text-xs text-slate-500">針對星期一到星期日設定平日的營業時間與狀態。</p>
                        </div>
                    </div>

                    <div className="overflow-x-auto rounded-xl border border-slate-200 bg-slate-50">
                        <table className="min-w-full text-xs text-slate-700">
                            <thead>
                            <tr className="border-b border-slate-200 bg-slate-100 text-[11px] font-medium text-slate-500">
                                <th className="px-3 py-2 text-left">星期</th>
                                <th className="px-3 py-2 text-left">開始時間</th>
                                <th className="px-3 py-2 text-left">結束時間</th>
                                <th className="px-3 py-2 text-left">狀態</th>
                            </tr>
                            </thead>
                            <tbody>
                            {openTimeList.map((item, index) => {
                                // 💡 核心檢核邏輯：如果營業中，且結束時間 <= 開始時間，就是錯誤
                                const isError = item.isOpen && item.closeTime <= item.openTime;

                                return (
                                    <tr key={item.dayOfWeek} className="border-t border-slate-200 hover:bg-slate-100/60">
                                        <td className="px-3 py-2 align-middle text-[13px] font-medium text-slate-700">
                                            {dayOfWeekOptions.find((opt) => opt.value === item.dayOfWeek)?.label}
                                        </td>

                                        {/* 開始時間 */}
                                        <td className="px-3 py-2 align-middle">
                                            {item.isOpen ? (
                                                <TimePicker
                                                    // 💡 這裡直接丟「訊號」給它，TimePicker 內部會自己變紅
                                                    isError={isError}
                                                    value={toTimeValue(item.openTime)}
                                                    onChange={(val) =>
                                                        handleUpdateOpenTime(index, {
                                                            openTime: fromTimeValue(val, item.openTime),
                                                        })
                                                    }
                                                    minuteStep={5}
                                                    placeholder="開始時間"
                                                />
                                            ) : (
                                                <div className="flex h-[42px] w-full items-center justify-center rounded-xl border border-slate-200 bg-slate-50 text-[11px] text-slate-400">
                                                    -
                                                </div>
                                            )}
                                        </td>

                                        {/* 結束時間 */}
                                        <td className="px-3 py-2 align-middle">
                                            {item.isOpen ? (
                                                <TimePicker
                                                    isError={isError}
                                                    value={toTimeValue(item.closeTime)}
                                                    onChange={(val) =>
                                                        handleUpdateOpenTime(index, {
                                                            closeTime: fromTimeValue(val, item.closeTime),
                                                        })
                                                    }
                                                    minuteStep={5}
                                                    placeholder="結束時間"
                                                />
                                            ) : (
                                                <div className="flex h-[42px] w-full items-center justify-center rounded-xl border border-slate-200 bg-slate-50 text-[11px] text-slate-400">
                                                    -
                                                </div>
                                            )}
                                        </td>

                                        {/* 狀態 */}
                                        <td className="px-3 py-2 align-middle">
                                            <div className="flex flex-col gap-1 min-h-[50px] justify-center">
                                                <FancySelect
                                                    options={[
                                                        {
                                                            value: "OPEN",
                                                            label: "營業",
                                                            icon: <span className="inline-block h-2 w-2 rounded-full bg-emerald-500" />,
                                                        },
                                                        {
                                                            value: "CLOSED",
                                                            label: "休息",
                                                            icon: <span className="inline-block h-2 w-2 rounded-full bg-amber-500" />,
                                                        },
                                                    ]}
                                                    value={item.isOpen ? "OPEN" : "CLOSED"}
                                                    onChange={(val) =>
                                                        handleUpdateOpenTime(index, {
                                                            isOpen: val === "OPEN",
                                                        })
                                                    }
                                                    placeholder="選擇狀態"
                                                    className="w-32"
                                                />
                                                {isError && (
                                                    <span className="ml-1 text-[10px] font-semibold text-rose-500 animate-pulse">時間設定有誤</span>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </div>
                </div>

                <div className="space-y-3">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-sm font-semibold text-slate-900">
                                特殊休息日設定
                            </h2>
                            <p className="text-xs text-slate-500">
                                針對特定日期（例如過年、連續假期）設定不營業，不會影響平日的開放時間。
                            </p>
                        </div>
                        <button
                            type="button"
                            onClick={handleAddHoliday}
                            className="cursor-pointer rounded-lg border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-100"
                        >
                            新增特殊休息日
                        </button>
                    </div>

                    {fixedHolidays.length > 0 && (
                        <div
                            className="overflow-x-auto rounded-xl border border-slate-200 bg-slate-50 text-xs text-slate-700">
                            <table className="min-w-full">
                                <thead>
                                <tr className="border-b border-slate-200 bg-slate-100 text-[11px] font-medium text-slate-500">
                                    <th className="px-3 py-2 text-left">日期</th>
                                    <th className="px-3 py-2 text-left">說明（選填）</th>
                                    <th className="w-16 px-3 py-2 text-center">刪除</th>
                                </tr>
                                </thead>
                                <tbody>
                                {fixedHolidays.map((holiday, index) => (
                                    <tr
                                        key={`${holiday.date}-${index}`}
                                        className="border-t border-slate-200 hover:bg-slate-100/60"
                                    >
                                        <td className="px-3 py-2 align-middle">
                                            <input
                                                type="date"
                                                value={holiday.date}
                                                onChange={(e) =>
                                                    handleUpdateHoliday(index, {date: e.target.value})
                                                }
                                                className="w-full rounded-md border border-slate-200 bg-white px-2 py-1.5 text-xs text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-2 focus:ring-blue-500/20"
                                            />
                                        </td>
                                        <td className="px-3 py-2 align-middle">
                                            <input
                                                type="text"
                                                value={holiday.description}
                                                onChange={(e) =>
                                                    handleUpdateHoliday(index, {
                                                        description: e.target.value,
                                                    })
                                                }
                                                className="w-full rounded-md border border-slate-200 bg-white px-2 py-1.5 text-xs text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-2 focus:ring-blue-500/20"
                                                placeholder="例如：除夕～初三公休"
                                            />
                                        </td>
                                        <td className="px-3 py-2 text-center align-middle">
                                            <button
                                                type="button"
                                                onClick={() => handleRemoveHoliday(index)}
                                                className="inline-flex h-7 w-7 cursor-pointer items-center justify-center rounded-full bg-rose-50 text-[11px] font-semibold text-rose-600 hover:bg-rose-100"
                                                aria-label="刪除特殊休息日"
                                            >
                                                ✕
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                <div className="space-y-2">
                    <h2 className="text-sm font-semibold text-slate-900">圖片上傳區</h2>
                    <p className="text-xs text-slate-500">
                        可上傳場地照片，用於前台或後台顯示。此處僅負責呼叫圖片上傳 API，實際儲存與壓縮由後端處理。
                    </p>
                    <div
                        className="mt-2 flex flex-col items-start gap-3 rounded-xl border border-dashed border-slate-300 bg-slate-50 px-4 py-5 text-xs text-slate-500">
                        <input
                            id="files"
                            type="file"
                            accept="image/*"
                            multiple
                            onChange={handleFileChange}
                            className="block cursor-pointer text-xs text-slate-600 file:mr-3 file:cursor-pointer file:rounded-md file:border-0 file:bg-blue-600 file:px-3 file:py-1.5 file:text-xs file:font-semibold file:text-white hover:file:bg-blue-700"
                        />
                        <div className="text-[11px] text-slate-500">
                            最多可上傳 10 張圖片。
                        </div>
                        {fileMessage && (
                            <div
                                className="rounded-md border border-amber-200 bg-amber-50 px-3 py-1.5 text-[11px] text-amber-700">
                                {fileMessage}
                            </div>
                        )}
                        {previewUrls.length > 0 && (
                            <div className="grid w-full grid-cols-2 gap-3 sm:grid-cols-4 md:grid-cols-5">
                                {previewUrls.map((url, index) => (
                                    <div
                                        key={url}
                                        className="relative h-20 overflow-hidden rounded-md border border-slate-200 bg-white"
                                    >
                                        <img
                                            src={url}
                                            alt={`預覽圖片 ${index + 1}`}
                                            className="h-full w-full object-cover"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => handleRemoveImage(index)}
                                            className="absolute right-1.5 top-1.5 inline-flex h-6 w-6 cursor-pointer items-center justify-center rounded-full bg-white/90 text-[11px] font-semibold text-rose-600 shadow-sm hover:bg-rose-50"
                                            aria-label="刪除此圖片"
                                        >
                                            ✕
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>

                <div className="flex items-center justify-end gap-3 pt-2">
                    <button
                        type="button"
                        onClick={() => router.push("/admin/court")}
                        className="cursor-pointer rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
                    >
                        取消
                    </button>
                    <button
                        type="submit"
                        disabled={saving}
                        className="inline-flex cursor-pointer items-center justify-center rounded-lg bg-blue-600 px-5 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-80"
                    >
                        {saving ? "儲存中..." : "儲存球場"}
                    </button>
                </div>
            </form>
        </div>
    );
}

