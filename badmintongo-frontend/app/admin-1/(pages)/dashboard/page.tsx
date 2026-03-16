export default function AdminDashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-slate-900">Dashboard</h1>
        <p className="mt-1 text-sm text-slate-500">
          今日球場營運概況與重點指標。
        </p>
      </div>

      {/* Top KPI cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <div className="rounded-xl border border-slate-200 bg-white px-4 py-4 shadow-sm">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span>啟用球場數</span>
            <span className="rounded-full bg-blue-50 px-2 py-0.5 text-[10px] font-medium text-blue-600">
              Courts
            </span>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-semibold tracking-tight text-slate-900">
              18
            </span>
            <span className="text-xs text-slate-400">個場地</span>
          </div>
          <div className="mt-3 h-1.5 w-full overflow-hidden rounded-full bg-slate-100">
            <div className="h-full w-2/3 rounded-full bg-gradient-to-r from-blue-500 to-sky-400" />
          </div>
          <p className="mt-2 text-xs text-slate-400">含室內與室外球場。</p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white px-4 py-4 shadow-sm">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span>今日預約</span>
            <span className="text-[10px] font-medium text-emerald-600">
              +12% vs 昨日
            </span>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-semibold tracking-tight text-slate-900">
              86
            </span>
            <span className="text-xs text-slate-400">筆訂場</span>
          </div>
          <div className="mt-3 h-1.5 w-full overflow-hidden rounded-full bg-slate-100">
            <div className="h-full w-3/4 rounded-full bg-gradient-to-r from-emerald-500 to-lime-400" />
          </div>
          <p className="mt-2 text-xs text-slate-400">
            包含線上預約與現場臨打。
          </p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white px-4 py-4 shadow-sm">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span>場地使用率</span>
            <span className="text-[10px] text-slate-400">尖峰 19:00</span>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-semibold tracking-tight text-slate-900">
              74%
            </span>
            <span className="text-xs text-emerald-600">+6% 本週</span>
          </div>
          <div className="mt-3 grid grid-cols-6 gap-1">
            {[60, 72, 80, 68, 75, 74].map((v, idx) => (
              <div
                // eslint-disable-next-line react/no-array-index-key
                key={idx}
                className="flex h-16 items-end justify-center rounded-md bg-slate-50"
              >
                <div
                  className="w-2 rounded-full bg-gradient-to-t from-blue-500 to-sky-400"
                  style={{ height: `${v}%` }}
                />
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white px-4 py-4 shadow-sm">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span>異常與提醒</span>
            <span className="text-[10px] text-amber-600">2 issues</span>
          </div>
          <ul className="mt-3 space-y-1.5 text-xs text-slate-500">
            <li>・ B1 球場 21:00-22:00 有重複訂場。</li>
            <li>・ A3 球場今晚 20:00 後維護關閉。</li>
          </ul>
        </div>
      </div>

      {/* Lower content */}
      <div className="grid gap-4 lg:grid-cols-3">
        <section className="lg:col-span-2 rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h2 className="text-sm font-semibold text-slate-900">
                今日時段概況
              </h2>
              <p className="text-xs text-slate-500">
                示意區塊，之後可接上實際訂場／臨打紀錄。
              </p>
            </div>
            <button className="cursor-pointer rounded-full border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-100">
              匯出報表
            </button>
          </div>

          <div className="mt-2 grid grid-cols-12 gap-2 text-[11px] text-slate-500">
            <div className="col-span-2 space-y-1">
              {["15:00", "16:00", "17:00", "18:00", "19:00"].map((t) => (
                <div key={t} className="h-9">
                  {t}
                </div>
              ))}
            </div>
            <div className="col-span-10">
              <div className="relative h-[180px] rounded-lg bg-slate-50">
                <div className="absolute inset-3 grid grid-cols-5 gap-2">
                  {[45, 60, 80, 70, 55].map((v, idx) => (
                    <div
                      // eslint-disable-next-line react/no-array-index-key
                      key={idx}
                      className="flex items-end justify-center rounded-md bg-slate-100"
                    >
                      <div
                        className="w-6 rounded-t-md bg-gradient-to-t from-blue-500 to-sky-400"
                        style={{ height: `${v}%` }}
                      />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="rounded-xl border border-slate-200 bg-white p-4 text-xs text-slate-500 shadow-sm">
          <h2 className="text-sm font-semibold text-slate-900">快速紀錄</h2>
          <ul className="mt-3 space-y-2.5">
            <li className="flex items-start justify-between gap-2">
              <div>
                <div className="text-[13px] font-medium text-slate-800">
                  即將開始的場次
                </div>
                <p className="text-[11px] text-slate-500">
                  A1 球場 19:00 - 20:00 · 線上預約 4 人
                </p>
              </div>
              <span className="rounded-full bg-emerald-50 px-2 py-0.5 text-[10px] font-medium text-emerald-600">
                15 分
              </span>
            </li>
            <li className="flex items-start justify-between gap-2">
              <div>
                <div className="text-[13px] font-medium text-slate-800">
                  維護通知
                </div>
                <p className="text-[11px] text-slate-500">
                  B3 球場 22:00 後暫停開放，請提前通知現場人員。
                </p>
              </div>
            </li>
            <li className="flex items-start justify-between gap-2">
              <div>
                <div className="text-[13px] font-medium text-slate-800">
                  系統狀態
                </div>
                <p className="text-[11px] text-slate-500">
                  API 正常，最近 24 小時無錯誤紀錄。
                </p>
              </div>
            </li>
          </ul>
        </section>
      </div>
    </div>
  );
}

