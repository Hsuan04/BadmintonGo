import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    // 1. 從 Cookie 拿 Token (這是標準做法，因為 Middleware 拿不到 localStorage)
    const token = request.cookies.get('token')?.value;
    const isLoginPage = request.nextUrl.pathname === '/admin/login';
    const isAdminPage = request.nextUrl.pathname.startsWith('/admin');

    // 2. 如果要去管理頁面但沒 Token -> 踢回登入
    if (isAdminPage && !isLoginPage && !token) {
        return NextResponse.redirect(new URL('/admin/login', request.url));
    }

    // 3. 如果已經登入還想去登入頁 -> 踢回 Dashboard
    if (isLoginPage && token) {
        return NextResponse.redirect(new URL('/admin/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/admin/:path*'], // 守護所有管理後台路徑
};