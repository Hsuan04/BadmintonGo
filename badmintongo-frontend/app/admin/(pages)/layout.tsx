"use client";

import { ReactNode } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    LayoutDashboard,
    MapPin,
    LogOut,
    RefreshCw,
    ChevronRight
} from "lucide-react";
import Cookies from "js-cookie";
import axios from "axios";

export default function AdminConsoleLayout({
                                               children,
                                           }: {
    children: ReactNode;
}) {
    const router = useRouter();

    // 登出
    const handleLogout = async () => {
        try {
            const token = Cookies.get("token") || localStorage.getItem("token");
            await axios.post("http://localhost:8081/api/auth/admin/logout", {}, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            console.log("後端登出紀錄成功");
        } catch (e) {

        } finally {
            Cookies.remove("token");
            localStorage.removeItem("token");
            localStorage.removeItem("role");
            router.push("/admin/login");
        }
    };

    return (
        <div className="min-h-screen bg-[#f5f7fb]">
            <div className="flex h-screen max-h-screen">
                {/* Sidebar */}
                <aside className="hidden w-64 shrink-0 border-r border-slate-200 bg-white/95 px-4 py-5 shadow-sm md:flex md:flex-col">
                    <div className="mb-8 flex items-center gap-2 px-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-blue-600 text-sm font-semibold text-white">
                            B
                        </div>
                        <div>
                            <div className="text-sm font-semibold tracking-wide text-slate-900">BadmintonGo</div>
                            <div className="text-xs text-slate-400">Admin Console</div>
                        </div>
                    </div>

                    <nav className="flex-1 space-y-1 text-sm">
                        <div className="px-2 pb-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-400">
                            Navigation
                        </div>

                        <Link
                            href="/admin/dashboard"
                            className="group flex items-center justify-between rounded-lg px-3 py-2 text-slate-700 transition hover:bg-slate-100"
                        >
              <span className="flex items-center gap-2">
                {/* 💡 使用 Lucide 代替原本的文字小方塊 */}
                  <LayoutDashboard size={18} className="text-blue-600" />
                <span>Dashboard</span>
              </span>
                            <ChevronRight size={14} className="opacity-0 transition group-hover:opacity-100" />
                        </Link>

                        <Link
                            href="/admin/court"
                            className="group flex items-center justify-between rounded-lg px-3 py-2 text-slate-700 transition hover:bg-slate-100"
                        >
              <span className="flex items-center gap-2">
                <MapPin size={18} className="text-emerald-600" />
                <span>球場管理</span>
              </span>
                            <ChevronRight size={14} className="opacity-0 transition group-hover:opacity-100" />
                        </Link>
                    </nav>

                    <div className="mt-6 rounded-xl border border-slate-200 bg-slate-50 px-3.5 py-3 text-xs text-slate-500">
                        <div className="mb-1 text-[11px] font-semibold uppercase tracking-[0.14em] text-slate-400">
                            Today
                        </div>
                        快速查看球場與訂場狀態。
                    </div>
                </aside>

                {/* Main column */}
                <div className="flex min-w-0 flex-1 flex-col">
                    {/* Header */}
                    <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white/90 px-4 shadow-sm backdrop-blur-sm md:px-6">
                        <div className="space-y-0.5">
                            <div className="text-xs uppercase tracking-[0.18em] text-slate-400">Admin</div>
                            <div className="text-sm font-medium text-slate-900">BadmintonGo Back Office</div>
                        </div>

                        <div className="flex items-center gap-4">
                            <button className="hidden cursor-pointer items-center gap-1.5 rounded-full border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-700 shadow-sm hover:bg-slate-100 md:inline-flex">
                                <RefreshCw size={14} />
                                Refresh
                            </button>

                            <div className="h-7 w-px bg-slate-200" />

                            <div className="flex items-center gap-3">
                                {/* 使用者頭像區 */}
                                <div className="flex items-center gap-2 rounded-full border border-slate-200 bg-slate-50 px-2.5 py-1.5">
                                    <div className="h-7 w-7 rounded-full bg-slate-200" />
                                    <div className="hidden text-xs leading-tight text-slate-700 sm:block">
                                        <div className="font-medium">Admin</div>
                                        <div className="text-[11px] text-slate-400">admin@badmintongo.tw</div>
                                    </div>
                                </div>

                                {/* 登出按鈕 */}
                                <div className="group relative flex items-center">
                                    <button
                                        onClick={handleLogout}
                                        className="flex h-9 w-9 cursor-pointer items-center justify-center rounded-full border border-slate-200 bg-white text-slate-400 shadow-sm transition-all hover:border-rose-200 hover:bg-rose-50 hover:text-rose-600"
                                    >
                                        <LogOut size={18} />
                                    </button>
                                </div>
                            </div>
                        </div>
                    </header>

                    {/* Content */}
                    <main className="flex-1 overflow-y-auto px-4 py-4 md:px-6 md:py-6">
                        {children}
                    </main>
                </div>
            </div>
        </div>
    );
}

