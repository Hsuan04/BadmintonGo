"use client";

import { useState } from "react";
import Link from "next/link";
import {
    LayoutDashboard, MapPin, Clock, ClipboardList,
    Users, X, PanelLeftClose, PanelLeftOpen, ChevronDown
} from "lucide-react";

interface SidebarProps {
    isOpen: boolean;
    setIsOpen: (open: boolean) => void;
    isMinified: boolean;
    setIsMinified: (min: boolean) => void;
}

export default function Sidebar({ isOpen, setIsOpen, isMinified, setIsMinified }: SidebarProps) {
    const [openMenus, setOpenMenus] = useState<string[]>(["場地管理"]);

    const toggleMenu = (menuName: string) => {
        setOpenMenus(prev =>
            prev.includes(menuName) ? prev.filter(i => i !== menuName) : [...prev, menuName]
        );
    };

    const menuGroups = [
        {
            title: "場務維護",
            items: [
                { label: "場地管理", icon: <MapPin size={18} />, subItems: [
                        { label: "所有場地清單", href: "/admin/court" },
                        { label: "新增場地設施", href: "/admin/court/detail" }
                    ]},
                { label: "場次管理", icon: <Clock size={18} />, subItems: [
                        { label: "時段樣板設定", href: "/admin/slots/template" }
                    ]}
            ]
        },
        {
            title: "營運管理",
            items: [
                { label: "報名管理", icon: <ClipboardList size={18} />, subItems: [
                        { label: "待核對報名", href: "/admin/registration/pending" }
                    ]},
                { label: "會員管理", icon: <Users size={18} />, subItems: [
                        { label: "會員等級權限", href: "/admin/users/levels" }
                    ]}
            ]
        }
    ];

    return (
        <aside className={`fixed inset-y-0 start-0 z-60 bg-white border-e border-blue-100 transition-all duration-300 ${isMinified ? 'w-20' : 'w-64'} ${isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}`}>
            <div className="flex flex-col h-full">
                <header className="p-4 flex justify-between items-center overflow-hidden">
                    {!isMinified && <span className="font-bold text-lg text-slate-800">BadmintonGo</span>}
                    <button onClick={() => setIsMinified(!isMinified)} className="hidden lg:flex p-2 hover:bg-blue-50 rounded-lg text-blue-400">
                        {isMinified ? <PanelLeftOpen size={18} /> : <PanelLeftClose size={18} />}
                    </button>
                    <button className="lg:hidden" onClick={() => setIsOpen(false)}><X size={20} /></button>
                </header>

                <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
                    <Link href="/badmintongo-frontend/app/admin/dashboard" className="flex items-center gap-3 py-2 px-3 rounded-xl bg-blue-600 text-white font-semibold">
                        <LayoutDashboard size={18} />
                        {!isMinified && <span>控制面板</span>}
                    </Link>

                    {menuGroups.map((group) => (
                        <div key={group.title} className="space-y-1.5">
                            {!isMinified && <p className="px-4 text-[10px] font-bold text-blue-300 uppercase tracking-widest">{group.title}</p>}
                            {group.items.map((item) => (
                                <div key={item.label} className="px-1">
                                    <button
                                        onClick={() => toggleMenu(item.label)}
                                        className={`w-full flex items-center gap-3 py-2 px-3 rounded-xl text-sm transition-all cursor-pointer ${openMenus.includes(item.label) ? 'text-blue-600 font-bold bg-blue-50/50' : 'text-slate-600 hover:bg-blue-50/30'}`}
                                    >
                                        <span className={openMenus.includes(item.label) ? 'text-blue-600' : 'text-slate-400'}>{item.icon}</span>
                                        {!isMinified && (
                                            <>
                                                <span className="flex-1 text-left">{item.label}</span>
                                                <ChevronDown size={14} className={`transition-transform duration-200 ${openMenus.includes(item.label) ? 'rotate-180' : ''}`} />
                                            </>
                                        )}
                                    </button>
                                    {!isMinified && openMenus.includes(item.label) && (
                                        <div className="ms-8 mt-1 space-y-1 border-s-2 border-blue-50">
                                            {item.subItems.map((sub) => (
                                                <Link key={sub.label} href={sub.href} className="block py-2 px-4 text-[13px] text-slate-500 hover:text-blue-600 rounded-lg">{sub.label}</Link>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    ))}
                </nav>
            </div>
        </aside>
    );
}