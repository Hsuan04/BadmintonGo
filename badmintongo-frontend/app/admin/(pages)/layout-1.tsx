// "use client";
//
// import { useState } from "react";
// import Link from "next/link";
// import {
//     LayoutDashboard, MapPin, Clock, ClipboardList,
//     Users, Menu, X, PanelLeftClose, PanelLeftOpen,
//     ChevronDown, Settings
// } from "lucide-react";
//
// export default function BadmintonGoLayout({ children }: { children: React.ReactNode }) {
//     const [isOpen, setIsOpen] = useState(false); // 手機版
//     const [isMinified, setIsMinified] = useState(false); // 電腦版縮放
//     const [openMenus, setOpenMenus] = useState<string[]>(["場地管理"]); // 預設展開
//
//     const toggleMenu = (menuName: string) => {
//         setOpenMenus(prev =>
//             prev.includes(menuName) ? prev.filter(i => i !== menuName) : [...prev, menuName]
//         );
//     };
//
//     const menuGroups = [
//         {
//             title: "場務維護",
//             items: [
//                 {
//                     label: "場地管理",
//                     icon: <MapPin size={18} />,
//                     subItems: [
//                         { label: "所有場地清單", href: "/thai/courts" },
//                         { label: "新增場地設施", href: "/thai/courts/add" },
//                         { label: "場地異常紀錄", href: "/thai/courts/issues" }
//                     ]
//                 },
//                 {
//                     label: "場次管理",
//                     icon: <Clock size={18} />,
//                     subItems: [
//                         { label: "時段樣板設定", href: "/thai/slots/template" },
//                         { label: "場次狀態批次修改", href: "/thai/slots/status" }
//                     ]
//                 }
//             ]
//         },
//         {
//             title: "營運管理",
//             items: [
//                 {
//                     label: "報名管理",
//                     icon: <ClipboardList size={18} />,
//                     subItems: [
//                         { label: "待核對報名", href: "/thai/registration/pending" },
//                         { label: "退費/取消申請", href: "/thai/registration/refunds" }
//                     ]
//                 },
//                 {
//                     label: "會員管理",
//                     icon: <Users size={18} />,
//                     subItems: [
//                         { label: "會員等級權限", href: "/thai/users/levels" },
//                         { label: "積分/優惠券發放", href: "/thai/users/rewards" }
//                     ]
//                 }
//             ]
//         }
//     ];
//
//     return (
//         <div className="flex h-screen bg-[#F8FAFC]">
//             {/* 1. Sidebar */}
//             <aside
//                 className={`
//                     fixed inset-y-0 start-0 z-60 bg-white border-e border-blue-100 transition-all duration-300
//                     ${isMinified ? 'w-20' : 'w-64'}
//                     ${isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
//                 `}
//             >
//                 <div className="relative flex flex-col h-full">
//                     {/* Header */}
//                     <header className="p-4 flex justify-between items-center overflow-hidden">
//                         {!isMinified && (
//                             <div className="flex items-center gap-2">
//                                 <div className="bg-blue-600 h-8 w-8 flex items-center justify-center rounded-lg text-white font-bold">B</div>
//                                 <span className="font-bold text-lg text-slate-800 tracking-tight">BadmintonGo</span>
//                             </div>
//                         )}
//                         <button
//                             onClick={() => setIsMinified(!isMinified)}
//                             className="hidden lg:flex p-2 hover:bg-blue-50 rounded-lg text-blue-400 transition-colors"
//                         >
//                             {isMinified ? <PanelLeftOpen size={18} /> : <PanelLeftClose size={18} />}
//                         </button>
//                     </header>
//
//                     {/* Navigation */}
//                     <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
//                         {/* Dashboard */}
//                         <div className="space-y-1 px-1">
//                             <Link href="/thai/dashboard" className="flex items-center gap-3 py-2 px-3 rounded-xl bg-blue-600 text-white font-semibold shadow-lg shadow-blue-100">
//                                 <LayoutDashboard size={18} />
//                                 {!isMinified && <span>控制面板</span>}
//                             </Link>
//                         </div>
//
//                         {/* Groups */}
//                         {menuGroups.map((group) => (
//                             <div key={group.title} className="space-y-1.5">
//                                 {!isMinified && <p className="px-4 text-[10px] font-bold text-blue-300 uppercase tracking-widest">{group.title}</p>}
//                                 {group.items.map((item) => (
//                                     <div key={item.label} className="px-1">
//                                         <button
//                                             onClick={() => toggleMenu(item.label)}
//                                             className={`
//                                                 w-full flex items-center gap-3 py-2 px-3 rounded-xl text-sm transition-all
//                                                 cursor-pointer
//                                                 ${openMenus.includes(item.label) ? 'text-blue-600 font-bold bg-blue-50/50' : 'text-slate-600 hover:bg-blue-50/30'}
//                                             `}
//                                         >
//                                             <span className={openMenus.includes(item.label) ? 'text-blue-600' : 'text-slate-400'}>{item.icon}</span>
//                                             {!isMinified && (
//                                                 <>
//                                                     <span className="flex-1 text-left">{item.label}</span>
//                                                     <ChevronDown size={14} className={`transition-transform duration-200 ${openMenus.includes(item.label) ? 'rotate-180' : ''}`} />
//                                                 </>
//                                             )}
//                                         </button>
//
//                                         {/* SubItems */}
//                                         {!isMinified && openMenus.includes(item.label) && (
//                                             <div className="ms-8 mt-1 space-y-1 border-s-2 border-blue-50">
//                                                 {item.subItems.map((sub) => (
//                                                     <Link
//                                                         key={sub.label}
//                                                         href={sub.href}
//                                                         className="block py-2 px-4 text-[13px] text-slate-500 hover:text-blue-600 hover:bg-blue-50/20 rounded-lg transition-all"
//                                                     >
//                                                         {sub.label}
//                                                     </Link>
//                                                 ))}
//                                             </div>
//                                         )}
//                                     </div>
//                                 ))}
//                             </div>
//                         ))}
//                     </nav>
//                 </div>
//             </aside>
//
//             {/* 2. Main Content */}
//             <main className={`flex-1 transition-all duration-300 ${isMinified ? 'lg:ms-20' : 'lg:ms-64'}`}>
//                 {/* Header/Navbar 可以在這裡加上 */}
//                 <div className="p-8">
//                     {children}
//                 </div>
//             </main>
//         </div>
//     );
// }