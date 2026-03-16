"use client";

import { useState } from "react";
import Sidebar from "./_components/sidebar";
import Header from "./_components/header";

export default function ThaiLayout({ children }: { children: React.ReactNode }) {
    const [isOpen, setIsOpen] = useState(false);
    const [isMinified, setIsMinified] = useState(false);

    return (
        <div className="flex h-screen bg-[#F8FAFC] overflow-hidden"> {/* 禁止全域捲動 */}
            <Sidebar isOpen={isOpen} setIsOpen={setIsOpen} isMinified={isMinified} setIsMinified={setIsMinified} />

            <div className={`flex flex-col flex-1 h-screen transition-all duration-300 ${isMinified ? 'lg:ms-20' : 'lg:ms-64'}`}>
                <Header onOpenMenu={() => setIsOpen(true)} />

                {/* 讓 main 承接捲動，h-full 確保背景填滿 */}
                <main className="flex-1 overflow-y-auto p-8 bg-[#F8FAFC]">
                    <div className="mx-auto w-full max-w-[1600px]">
                        {children}
                    </div>
                </main>
            </div>
        </div>
    );
}