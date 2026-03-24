 "use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Cookies from "js-cookie";

export default function AdminLoginPage() {
  const router = useRouter();                   //用於切換網頁
  const [email, setEmail] = useState("");              //記錄當前頁面 account/email 輸入匡資料
  const [password, setPassword] = useState("");        //記錄當前頁面 password 輸入匡資料
  const [loading, setLoading] = useState(false);       //記錄按鈕是否點擊並等待回應中
  const [rememberMe, setRememberMe] = useState(true);  //頁面「記住我」的 checkbox 是否點擊
  const [errors, setErrors] = useState<{                         //錯誤狀態資料
    form?: string;
    email?: string;
    password?: string;
  }>({});

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrors({});

        if (!email.trim() || !password.trim()) {
            setErrors({ form: "wrong account / email or password\n" });
            return;
        }

        setLoading(true);

        try {
            const response = await axios.post("http://localhost:8081/api/auth/admin/login", {
                email: email.trim(),
                password: password
            });

            if (response.data.success) {
                const token = response.data.token;

                // 關鍵就在這裡：同時存入 localStorage 和 Cookies
                localStorage.setItem("token", token);

                // 存入 Cookie，middleware 才能讀到。expires: 1 代表一天後過期
                Cookies.set("token", token, { expires: 1, path: '/' });

                router.push("/admin/dashboard");
            }
        } catch (error: any) {
            setErrors({
                form: error.response?.data?.message || "帳號或密碼有誤，請重新輸入",
            });
        } finally {
            setLoading(false);
        }
    };

  return (
    <div className="min-h-screen bg-[#f3f5f9]">
      <div className="mx-auto flex min-h-screen w-full max-w-6xl items-center justify-center px-4 py-10">
        <div className="w-full max-w-[440px]">
          <div className="rounded-2xl border border-slate-200/80 bg-white shadow-[0_20px_60px_rgba(15,23,42,0.12)]">
            <div className="px-8 pt-8 pb-7">
              <h1 className="text-2xl font-semibold tracking-tight text-slate-900">
                Back Office Login
              </h1>
              <p className="mt-2 text-sm leading-6 text-slate-500">
                Sign in with your administrator account to access the BadmintonGo
                back-office dashboard.
              </p>
            </div>

            <div className="px-8 pb-8">
              <form onSubmit={handleSubmit} className="space-y-4">
                {errors.form ? (
                  <div className="rounded-lg border border-rose-200 bg-rose-50 px-3.5 py-2.5 text-sm text-rose-700">
                    {errors.form}
                  </div>
                ) : null}

                <div className="space-y-2">
                  <label
                    htmlFor="email"
                    className="text-sm font-medium text-slate-700"
                  >
                    * account / email
                  </label>
                  <input
                    id="email"
                    type="text"
                    autoComplete="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    aria-invalid={Boolean(errors.email)}
                    aria-describedby={errors.email ? "email-error" : undefined}
                    className="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400 aria-[invalid=true]:border-rose-300 aria-[invalid=true]:focus:border-rose-400/80 aria-[invalid=true]:focus:ring-rose-500/10"
                    placeholder="admin@courtflow.tw"
                  />
                  {errors.email ? (
                    <p id="email-error" className="text-xs text-rose-600">
                      {errors.email}
                    </p>
                  ) : null}
                </div>

                <div className="space-y-2">
                  <label
                    htmlFor="password"
                    className="text-sm font-medium text-slate-700"
                  >
                    * password
                  </label>
                  <input
                    id="password"
                    type="password"
                    autoComplete="current-password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    aria-invalid={Boolean(errors.password)}
                    aria-describedby={
                      errors.password ? "password-error" : undefined
                    }
                    className="block w-full rounded-lg border border-slate-200 bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-blue-500/70 focus:ring-4 focus:ring-blue-500/10 placeholder:text-slate-400 aria-[invalid=true]:border-rose-300 aria-[invalid=true]:focus:border-rose-400/80 aria-[invalid=true]:focus:ring-rose-500/10"
                    placeholder="••••••••"
                  />
                  {errors.password ? (
                    <p id="password-error" className="text-xs text-rose-600">
                      {errors.password}
                    </p>
                  ) : null}
                </div>

                <div className="flex items-center justify-between pt-1">
                  {/*<label className="inline-flex items-center gap-2 text-sm text-slate-600">*/}
                  {/*  <input*/}
                  {/*    type="checkbox"*/}
                  {/*    checked={rememberMe}*/}
                  {/*    onChange={(e) => setRememberMe(e.target.checked)}*/}
                  {/*    className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-4 focus:ring-blue-500/10"*/}
                  {/*  />*/}
                  {/*  Remember me*/}
                  {/*</label>*/}

                  <button
                    type="button"
                  className="cursor-pointer text-sm font-medium text-blue-600 hover:text-blue-700"
                  >
                    Forgot password?
                  </button>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                className="mt-2 inline-flex w-full cursor-pointer items-center justify-center rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-blue-500/20 disabled:cursor-not-allowed disabled:opacity-80"
                >
                  {loading ? "Signing in..." : "LOGIN"}
                </button>
              </form>
            </div>
          </div>

          <p className="mt-5 text-center text-xs text-slate-500">
            Authorized administrators only.
          </p>
        </div>
      </div>
    </div>
  );
}
