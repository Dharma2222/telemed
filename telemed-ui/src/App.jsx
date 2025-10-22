import React from "react";
import { BrowserRouter, Routes, Route, Link, NavLink, useLocation } from "react-router-dom";
import { IdentityProvider } from "./context/IdentityContext.jsx";
import IdentityPage from "./pages/IdentityPage.jsx";
import ConsultationsPage from "./pages/ConsultationsPage.jsx";
import ChatPage from "./pages/ChatPage.jsx";
import { API_BASE } from "./api/client.js";

function Shell({ children }) {
    const loc = useLocation();
    return (
        <div className="min-h-screen bg-gradient-to-b from-slate-50 to-slate-100 text-slate-900">
            <div className="max-w-5xl mx-auto p-6 md:p-10 space-y-6">
                <header className="flex items-center justify-between">
                    <h1 className="text-2xl font-bold tracking-tight">
                        <Link to="/">Telemed Console</Link>
                    </h1>
                    <nav className="flex items-center gap-4 text-sm">
                        <NavLink to="/" className={({ isActive }) => `hover:underline ${isActive && loc.pathname === '/' ? 'font-semibold' : ''}`}>Identity</NavLink>
                        <NavLink to="/consultations" className={({ isActive }) => `hover:underline ${isActive ? 'font-semibold' : ''}`}>Consultations</NavLink>
                        
                    </nav>
                </header>
                {children}
                <footer className="text-xs text-slate-500 pt-6">
                    API Base: {API_BASE} — set <span className="font-mono">VITE_API_BASE</span>.
                </footer>
            </div>
        </div>
    );
}

export default function App() {
    return (
        <BrowserRouter>
            <IdentityProvider>
                <Shell>
                    <Routes>
                        <Route path="/" element={<IdentityPage />} />
                        <Route path="/consultations" element={<ConsultationsPage />} />
                        <Route path="/chat/:id" element={<ChatPage />} />
                    </Routes>
                </Shell>
            </IdentityProvider>
        </BrowserRouter>
    );
}
