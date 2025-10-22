import React, { useRef, useState } from "react";
import { useIdemKey, useToast } from "../utils/hooks.jsx";
import { postMessage, uploadMedia } from "../api/client.js";

export default function MessageComposerBar({ consultationId, authorId, authorRole, onSent }) {
    const [text, setText] = useState("");
    const [busy, setBusy] = useState(false);
    const { key, refresh } = useIdemKey();
    const { setMsg, Toast } = useToast();
    const fileInputRef = useRef(null);

    const canSend = !!consultationId && !!authorId && !!authorRole && !!text.trim();

    const onPickFile = () => fileInputRef.current?.click();

    const handleFileChange = async (e) => {
        const f = e.target.files?.[0];
        if (!f) return;
        setBusy(true);
        try {
            const up = await uploadMedia(f);
            const payload = {
                type: "MEDIA",
                storageKey: up.storageKey,
                mimeType: f.type || "application/octet-stream",
                sizeBytes: f.size,
            };
            await postMessage({
                consultationId,
                idemKey: key,
                authorId,
                authorRole,
                payload,
            });
            setText("");
            e.target.value = "";
            refresh();
            setMsg("Media sent");
            onSent && onSent();
        } catch (err) {
            setMsg(err?.response?.data?.message || err.message);
        } finally {
            setBusy(false);
        }
    };

    const sendText = async () => {
        const trimmed = text.trim();
        if (!trimmed) return;
        setBusy(true);
        try {
            const payload = { type: "TEXT", text: trimmed };
            await postMessage({
                consultationId,
                idemKey: key,
                authorId,
                authorRole,
                payload,
            });
            setText("");
            refresh();
            setMsg("Message sent");
            onSent && onSent();
        } catch (e) {
            setMsg(e?.response?.data?.message || e.message);
        } finally {
            setBusy(false);
        }
    };

    const onKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            if (canSend && !busy) sendText();
        }
    };

    return (
        <div className="sticky bottom-4">
            <div className="bg-white rounded-2xl border border-slate-200 shadow flex items-center gap-2 px-3 py-2">
                {/* Attach */}
                <button
                    type="button"
                    onClick={onPickFile}
                    className="p-2 rounded-lg hover:bg-slate-100 disabled:opacity-50"
                    title="Attach media"
                    disabled={busy}
                >
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                         fill="none" stroke="currentColor" className="w-5 h-5">
                        <path strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"
                              d="M21.44 11.05l-8.49 8.49a5.5 5.5 0 11-7.78-7.78l9.19-9.19a3.5 3.5 0 114.95 4.95l-9.2 9.2a1.5 1.5 0 11-2.12-2.12l8.49-8.49"/>
                    </svg>
                </button>
                <input ref={fileInputRef} type="file" className="hidden" onChange={handleFileChange} />

                {/* Text */}
                <textarea
                    rows={1}
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    onKeyDown={onKeyDown}
                    placeholder="Message"
                    className="flex-1 resize-none outline-none px-2 py-1 text-sm bg-transparent"
                />

                {/* Send */}
                <button
                    type="button"
                    onClick={sendText}
                    disabled={!text.trim() || busy}
                    className="p-2 rounded-lg hover:bg-slate-100 disabled:opacity-50"
                    title="Send"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                         fill="none" stroke="currentColor" className="w-5 h-5 rotate-45">
                        <path strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"
                              d="M5 12L3 21l9-2 9-9-7-7-9 9zM14 7l3 3"/>
                    </svg>
                </button>
            </div>
            <Toast />
        </div>
    );
}
