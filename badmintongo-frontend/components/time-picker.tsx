"use client";

import * as React from "react";

export type TimeValue = {
  hour: number; // 1-12
  minute: number; // 0-59
  period: "AM" | "PM";
};

type TimePickerProps = {
  label?: string;
  value?: TimeValue;
  onChange: (value: TimeValue | undefined) => void;
  placeholder?: string;
  minuteStep?: number;
  className?: string;
};

const HOURS = Array.from({ length: 12 }, (_, i) => i + 1);

function buildMinutes(step: number) {
  const mins: number[] = [];
  for (let m = 0; m < 60; m += step) mins.push(m);
  return mins;
}

export function TimePicker({
  label,
  value,
  onChange,
  placeholder = "選擇時間",
  minuteStep = 5,
  className,
}: TimePickerProps) {
  const [open, setOpen] = React.useState(false);
  const [internal, setInternal] = React.useState<TimeValue | undefined>(value);
  const containerRef = React.useRef<HTMLDivElement | null>(null);

  React.useEffect(() => {
    setInternal(value);
  }, [value]);

  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    };
    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [open]);

  const minutes = React.useMemo(() => buildMinutes(minuteStep), [minuteStep]);

  const display = internal
    ? `${String(internal.hour).padStart(2, "0")}:${String(
        internal.minute,
      ).padStart(2, "0")} ${internal.period}`
    : "";

  const updatePart = (partial: Partial<TimeValue>) => {
    const base: TimeValue = internal ?? { hour: 12, minute: 0, period: "AM" };
    const next: TimeValue = { ...base, ...partial };
    setInternal(next);
    onChange(next);
  };

  return (
    <div className={className}>
      {label ? (
        <label className="mb-1 block text-sm font-medium text-slate-800">
          {label}
        </label>
      ) : null}
      <div ref={containerRef} className="relative">
        <button
          type="button"
          onClick={() => setOpen((prev) => !prev)}
          className="flex w-full cursor-pointer items-center justify-between rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-left text-sm text-slate-900 shadow-sm outline-none transition hover:border-slate-300 focus-visible:border-blue-500/70 focus-visible:ring-4 focus-visible:ring-blue-500/10"
        >
          <span className="flex items-center gap-2">
            {display ? (
              <span className="tabular-nums">{display}</span>
            ) : (
              <span className="text-slate-400">{placeholder}</span>
            )}
          </span>
          <span className="inline-flex h-6 w-6 items-center justify-center rounded-full border border-slate-200 bg-slate-50 text-[10px] text-slate-500">
            {open ? "▴" : "▾"}
          </span>
        </button>

        {open && (
          <div className="absolute z-30 mt-1 w-full rounded-2xl border border-slate-200 bg-white p-3 shadow-xl">
            <div className="grid grid-cols-[1.2fr_1.2fr_auto] gap-3 text-xs text-slate-700">
              {/* Hours */}
              <div>
                <div className="mb-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-400">
                  HOUR
                </div>
                <div className="max-h-40 overflow-y-auto rounded-lg border border-slate-100 bg-slate-50">
                  {HOURS.map((h) => (
                    <button
                      key={h}
                      type="button"
                      onClick={() => updatePart({ hour: h })}
                      className={`flex w-full cursor-pointer items-center justify-center px-2 py-1.5 text-xs tabular-nums transition ${
                        internal?.hour === h
                          ? "bg-blue-600 text-white"
                          : "bg-transparent text-slate-700 hover:bg-slate-100"
                      }`}
                    >
                      {String(h).padStart(2, "0")}
                    </button>
                  ))}
                </div>
              </div>

              {/* Minutes */}
              <div>
                <div className="mb-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-400">
                  MIN
                </div>
                <div className="max-h-40 overflow-y-auto rounded-lg border border-slate-100 bg-slate-50">
                  {minutes.map((m) => (
                    <button
                      key={m}
                      type="button"
                      onClick={() => updatePart({ minute: m })}
                      className={`flex w-full cursor-pointer items-center justify-center px-2 py-1.5 text-xs tabular-nums transition ${
                        internal?.minute === m
                          ? "bg-blue-600 text-white"
                          : "bg-transparent text-slate-700 hover:bg-slate-100"
                      }`}
                    >
                      {String(m).padStart(2, "0")}
                    </button>
                  ))}
                </div>
              </div>

              {/* Period */}
              <div className="flex flex-col justify-between gap-2">
                <div>
                  <div className="mb-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-400">
                    PERIOD
                  </div>
                  <div className="flex flex-col gap-2">
                    {(["AM", "PM"] as const).map((p) => (
                      <button
                        key={p}
                        type="button"
                        onClick={() => updatePart({ period: p })}
                        className={`inline-flex cursor-pointer items-center justify-center rounded-full px-3 py-1.5 text-xs font-semibold tabular-nums transition ${
                          internal?.period === p
                            ? "bg-blue-600 text-white"
                            : "bg-slate-50 text-slate-700 hover:bg-slate-100"
                        }`}
                      >
                        {p}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

