import React, { useEffect, useState } from "react";

export function useToast() {
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        if (!msg) return;
        const t = setTimeout(() => setMsg(null), 2200);
        return () => clearTimeout(t);
    }, [msg]);

    const Toast = () =>
        msg ? (
            <div className="fixed bottom-4 right-4 bg-slate-900 text-white px-4 py-2 rounded-xl shadow-lg">
                {msg}
            </div>
        ) : null;

    return { setMsg, Toast };
}

export function useIdemKey() {
    const gen = () =>
        (crypto?.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random()}`);
    const [key, setKey] = useState(gen());
    return { key, refresh: () => setKey(gen()) };
}
