"use client";
import { useState, useEffect } from "react";

import {
    Search,
    Bell,
    Menu,
    Maximize,
    Settings,
    LogOut,
    UserIcon,
    HelpCircle
} from "lucide-react";
import {
    Dropdown,
    DropdownTrigger,
    DropdownMenu,
    DropdownItem,
    Avatar,
    Badge,
    Button,
    Input
} from "@heroui/react";

export default function Header({ onOpenMenu }: { onOpenMenu: () => void }) {

    const [mounted, setMounted] = useState(false);
    useEffect(() => {
        setMounted(true);
    }, []);
    // 模擬登出邏輯
    const handleLogout = () => {
        console.log("Logging out...");
        // window.location.href = "/login";
    };

    if (!mounted) {
        return (
            <header className="sticky top-0 z-50 h-16 w-full flex items-center justify-between px-8 bg-white border-b border-blue-50">
                <div className="flex items-center gap-4">
                    <div className="font-bold text-sm text-slate-800">BadmintonGo</div>
                </div>
            </header>
        );
    }

    return (
        <header className="sticky top-0 z-50 h-16 w-full flex items-center justify-between px-8 bg-white/80 backdrop-blur-md border-b border-blue-50">
            {/* 左側：選單與標題 */}
            <div className="flex items-center gap-4">
                <Button
                    isIconOnly
                    variant="light"
                    className="lg:hidden text-slate-600"
                    onPress={onOpenMenu}
                >
                    <Menu size={20} />
                </Button>
                <div className="flex flex-col">
                    <h2 className="text-sm font-bold text-slate-800 tracking-tight">BadmintonGo</h2>
                    <span className="text-[10px] text-blue-500 font-medium">Management System</span>
                </div>
            </div>

            {/* 右側：功能區 */}
            <div className="flex items-center gap-2 md:gap-4">

                {/* 2. 全螢幕按鈕 (實用小工具) */}
                <Button isIconOnly variant="light" radius="full" className="text-slate-500 hover:text-slate-800 hidden sm:flex">
                    <Maximize size={18} />
                </Button>

                {/* 3. 通知中心 - 帶有紅點提示 */}
                <Badge
                    classNames={{
                        // 1. shadow-none 移除你發現的那段 box-shadow
                        // 2. border-none 移除白色邊框
                        // 3. min-w-0 p-0 確保它是一個純圓點
                        badge: "w-2.5 h-2.5 min-w-0 p-0 border-none shadow-none ring-0",
                    }}
                    color="danger"
                    isInvisible={false}
                    shape="circle"
                    variant="solid"
                    content=""
                >
                    <Button
                        isIconOnly
                        variant="light"
                        radius="full"
                        className="text-slate-500 hover:text-blue-600"
                    >
                        <Bell size={18} />
                    </Button>
                </Badge>

                {/* 分隔線 */}
                <div className="h-6 w-px bg-slate-200 mx-2" />

                {/* 4. 管理者帳號下拉選單 */}
                <Dropdown placement="bottom-end">
                    <DropdownTrigger>
                        <div className="flex items-center gap-3 cursor-pointer group">
                            <div className="flex flex-col items-end hidden sm:flex">
                                <span className="text-xs font-bold text-slate-700 group-hover:text-blue-600 transition-colors">Lawrence</span>
                                <span className="text-[10px] text-slate-400">系統管理員</span>
                            </div>
                            <Avatar
                                isBordered
                                as="button"
                                className="transition-transform"
                                color="primary"
                                name="L"
                                size="sm"
                                src="https://i.pravatar.cc/150?u=lawrence" // 之後可以換成你的頭像
                            />
                        </div>
                    </DropdownTrigger>

                    <DropdownMenu aria-label="Profile Actions" variant="flat">
                        <DropdownItem key="profile" className="h-14 gap-2" textValue="Profile Info">
                            <p className="font-semibold text-xs text-slate-500">登入帳號</p>
                            <p className="font-semibold text-blue-600 text-sm">lawrence@example.com</p>
                        </DropdownItem>

                        <DropdownItem
                            key="settings"
                            startContent={<UserIcon size={16} />}
                            description="編輯你的個人檔案與偏好"
                        >
                            資料設定
                        </DropdownItem>

                        <DropdownItem
                            key="help"
                            startContent={<HelpCircle size={16} />}
                        >
                            幫助中心
                        </DropdownItem>

                        <DropdownItem
                            key="logout"
                            className="text-danger"
                            color="danger"
                            startContent={<LogOut size={16} />}
                            onPress={handleLogout}
                        >
                            登出系統
                        </DropdownItem>
                    </DropdownMenu>
                </Dropdown>
            </div>
        </header>
    );
}