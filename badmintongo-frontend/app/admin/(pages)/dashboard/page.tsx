// app/thai/(pages)/dashboard/page.tsx

// 1. 必須是 export default
// 2. 必須是一個 Function Component
export default function DashboardPage() {
    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold text-slate-800">
                BadmintonGo Dashboard
            </h1>
            <p className="text-slate-500 mt-2">
                歡迎來到新版配置測試頁面！
            </p>
        </div>
    );
}