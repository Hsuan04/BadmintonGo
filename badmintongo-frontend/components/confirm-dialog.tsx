"use client";

import React from "react";
import { AlertTriangle, Bell, CheckCircle2, Info } from "lucide-react";

export type DialogType = "danger" | "warning" | "info" | "success";

interface ConfirmDialogProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    content: string;
    type?: DialogType;
    confirmText?: string;
    cancelText?: string;
    isLoading?: boolean;
}

export function ConfirmDialog({
                                  isOpen,
                                  onClose,
                                  onConfirm,
                                  title,
                                  content,
                                  type = "info",
                                  confirmText = "確定",
                                  cancelText = "取消",
                                  isLoading = false,
                              }: ConfirmDialogProps) {
    if (!isOpen) return null;

    // 對齊你專案的視覺規範
    const typeConfigs = {
        danger: {
            bg: "bg-rose-50",
            icon: <AlertTriangle size={28} />,
            iconColor: "text-rose-600",
            btn: "bg-rose-600 hover:bg-rose-700 focus:ring-rose-500/20",
        },
        warning: {
            bg: "bg-amber-50",
            icon: <Bell size={28} />,
            iconColor: "text-amber-600",
            btn: "bg-amber-600 hover:bg-amber-700 focus:ring-amber-500/20",
        },
        success: {
            bg: "bg-emerald-50",
            icon: <CheckCircle2 size={28} />,
            iconColor: "text-emerald-600",
            btn: "bg-emerald-600 hover:bg-emerald-700 focus:ring-emerald-500/20",
        },
        info: {
            bg: "bg-blue-50",
            icon: <Info size={28} />,
            iconColor: "text-blue-600",
            btn: "bg-blue-600 hover:bg-blue-700 focus:ring-blue-500/20",
        },
    };

    const config = typeConfigs[type];

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            {/* 背景遮罩：使用毛玻璃效果對齊 FancySelect */}
            <div
                className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity"
                onClick={isLoading ? undefined : onClose}
            />

            {/* 彈窗主體：使用 rounded-2xl 增加層次感 */}
            <div className="relative w-full max-w-sm transform overflow-hidden rounded-2xl border border-slate-200 bg-white p-6 shadow-2xl transition-all animate-in fade-in zoom-in duration-200">
                <div className="flex flex-col items-center text-center">
                    {/* 圓形 Icon 區域 */}
                    <div className={`flex h-14 w-14 items-center justify-center rounded-full ${config.bg} ${config.iconColor} mb-4 text-2xl`}>
                        {config.icon}
                    </div>

                    <h3 className="mb-2 text-lg font-bold text-slate-900">{title}</h3>

                    {/* 內容區域：使用與搜尋列 Label 相似的顏色 */}
                    <p className="mb-8 text-sm text-slate-500 leading-relaxed px-2">
                        {content}
                    </p>

                    <div className="flex w-full gap-3">
                        <button
                            type="button"
                            disabled={isLoading}
                            onClick={onClose}
                            className="flex-1 cursor-pointer rounded-xl border border-slate-200 bg-white py-2.5 text-sm font-semibold text-slate-600 transition-all hover:bg-slate-50 disabled:opacity-50 active:scale-95"
                        >
                            {cancelText}
                        </button>
                        <button
                            type="button"
                            disabled={isLoading}
                            onClick={onConfirm}
                            className={`flex-1 cursor-pointer rounded-xl py-2.5 text-sm font-semibold text-white transition-all active:scale-95 disabled:opacity-50 ${config.btn} shadow-sm focus:ring-4`}
                        >
                            {isLoading ? "處理中..." : confirmText}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}