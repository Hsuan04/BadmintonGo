"use client";

import React from "react";

export type ButtonVariant = "primary" | "danger" | "secondary" | "ghost";

interface ModalButton {
    text: string;
    onClick: () => void;
    variant?: ButtonVariant;
    loading?: boolean;
}

type ConfirmModalProps = {
    isOpen: boolean;
    title: string;
    message: string;
    // 💡 傳入一個按鈕陣列，讓調用者決定顯示幾個
    buttons: ModalButton[];
    // 點擊背景遮罩時的行為 (通常是關閉)
    onClose?: () => void;
};

export function ConfirmModal({
                                 isOpen,
                                 title,
                                 message,
                                 buttons,
                                 onClose,
                             }: ConfirmModalProps) {
    if (!isOpen) return null;

    // 💡 定義顏色映射表，方便管理與擴充
    const variantStyles: Record<ButtonVariant, string> = {
        primary: "bg-emerald-600 hover:bg-emerald-700 text-white shadow-md",
        danger: "bg-rose-600 hover:bg-rose-700 text-white shadow-md",
        secondary: "bg-slate-100 hover:bg-slate-200 text-slate-600",
        ghost: "bg-transparent hover:bg-slate-50 text-slate-500 border border-slate-200",
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            {/* 背景遮罩 */}
            <div
                className="absolute inset-0 bg-slate-900/20 backdrop-blur-[2px] transition-opacity cursor-pointer"
                onClick={onClose}
            />

            {/* 彈窗主體 */}
            <div className="relative w-full max-w-sm rounded-2xl bg-white p-6 shadow-2xl animate-in fade-in zoom-in duration-200">
                <h3 className="text-lg font-bold text-slate-900">{title}</h3>
                <p className="mt-2 text-sm text-slate-500 leading-relaxed">{message}</p>

                <div className="mt-6 flex justify-end gap-3">
                    {buttons.map((btn, index) => (
                        <button
                            key={index}
                            type="button"
                            onClick={btn.onClick}
                            disabled={btn.loading}
                            className={`cursor-pointer rounded-xl px-5 py-2.5 text-sm font-semibold transition disabled:opacity-50 flex items-center gap-2 ${
                                variantStyles[btn.variant || "primary"]
                            }`}
                        >
                            {btn.loading && (
                                <span className="h-3 w-3 animate-spin rounded-full border-2 border-white border-t-transparent" />
                            )}
                            {btn.text}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
}