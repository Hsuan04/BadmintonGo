"use client";

import * as React from "react";

type BaseOption = {
  value: string;
  label: string;
  description?: string;
  icon?: React.ReactNode;
};

export type SelectOption = BaseOption;

export type SelectGroup = {
  label: string;
  options: SelectOption[];
};

type FancySelectProps = {
  label?: string;
  options: SelectOption[] | SelectGroup[];
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  searchable?: boolean;
  searchPlaceholder?: string;
  clearable?: boolean;
  icon?: React.ReactNode;
  className?: string;
};

type InternalOption = SelectOption & { groupLabel?: string };

function flattenOptions(
  opts: SelectOption[] | SelectGroup[],
): InternalOption[] {
  if (opts.length === 0) return [];
  if ("options" in (opts as SelectGroup[])[0]) {
    const groups = opts as SelectGroup[];
    return groups.flatMap((group) =>
      group.options.map((opt) => ({ ...opt, groupLabel: group.label })),
    );
  }
  return opts as SelectOption[];
}

export function FancySelect(props: FancySelectProps) {
  const {
    label,
    options,
    value,
    onChange,
    placeholder = "請選擇",
    searchable,
    searchPlaceholder = "搜尋...",
    clearable,
    icon,
    className,
  } = props;

  const [open, setOpen] = React.useState(false);
  const [search, setSearch] = React.useState("");
  const containerRef = React.useRef<HTMLDivElement | null>(null);

  const flatOptions = React.useMemo(() => flattenOptions(options), [options]);
  const selected = flatOptions.find((opt) => opt.value === value);

  const filteredOptions = React.useMemo(() => {
    if (!search.trim()) return flatOptions;
    const q = search.toLowerCase();
    return flatOptions.filter(
      (opt) =>
        opt.label.toLowerCase().includes(q) ||
        (opt.description && opt.description.toLowerCase().includes(q)),
    );
  }, [flatOptions, search]);

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

  const handleSelect = (val: string) => {
    onChange(val);
    setOpen(false);
    setSearch("");
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    onChange("");
    setSearch("");
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
          <span className="flex min-w-0 items-center gap-2">
            {selected?.icon || icon ? (
              <span className="text-slate-400">
                {selected?.icon ?? icon}
              </span>
            ) : null}
            <span className="truncate text-sm">
              {selected ? selected.label : (
                <span className="text-slate-400">{placeholder}</span>
              )}
            </span>
          </span>

          <span className="ml-3 flex items-center gap-1">
            {clearable && selected ? (
              <span
                onClick={handleClear}
                className="inline-flex h-6 w-6 cursor-pointer items-center justify-center rounded-full text-xs text-slate-400 hover:bg-slate-100 hover:text-slate-600"
              >
                ×
              </span>
            ) : null}
            <span className="inline-flex h-6 w-6 items-center justify-center rounded-full border border-slate-200 bg-slate-50 text-[10px] text-slate-500">
              {open ? "▴" : "▾"}
            </span>
          </span>
        </button>

        {open && (
          <div className="absolute z-30 mt-1 w-full overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl">
            {searchable && (
              <div className="border-b border-slate-100 bg-slate-50 px-3 py-2">
                <input
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  placeholder={searchPlaceholder}
                  className="block w-full rounded-lg border border-slate-200 bg-white px-2.5 py-1.5 text-xs text-slate-900 outline-none transition focus:border-blue-500/60 focus:ring-2 focus:ring-blue-500/15 placeholder:text-slate-400"
                />
              </div>
            )}

            <div className="max-h-64 overflow-y-auto py-1 text-sm">
              {filteredOptions.length === 0 ? (
                <div className="px-3 py-2 text-xs text-slate-400">
                  沒有符合的選項
                </div>
              ) : (
                filteredOptions.map((opt) => (
                  <button
                    type="button"
                    key={`${opt.groupLabel ?? "opt"}-${opt.value}`}
                    onClick={() => handleSelect(opt.value)}
                    className={`flex w-full cursor-pointer items-start gap-2 px-3 py-2 text-left text-sm transition hover:bg-blue-50 ${
                      opt.value === value ? "bg-blue-50" : ""
                    }`}
                  >
                    {opt.icon ? (
                      <span className="mt-0.5 text-slate-500">{opt.icon}</span>
                    ) : null}
                    <span className="flex min-w-0 flex-col">
                      <span className="truncate font-medium text-slate-900">
                        {opt.label}
                      </span>
                      {opt.description ? (
                        <span className="truncate text-xs text-slate-500">
                          {opt.description}
                        </span>
                      ) : opt.groupLabel ? (
                        <span className="truncate text-[11px] text-slate-400">
                          {opt.groupLabel}
                        </span>
                      ) : null}
                    </span>
                  </button>
                ))
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

