import React, { useEffect, useState } from "react";
import Section from "./Section.jsx";
import Badge from "./Badge.jsx";
import Button from "./Button.jsx";
import { Link } from "react-router-dom";
import { listMessages } from "../api/client.js";
import { useIdentity } from "../context/IdentityContext.jsx";

export default function MessageThread({ consultationId }) {
    const { identity } = useIdentity();
    const [items, setItems] = useState([]);
    const [cursor, setCursor] = useState(0);
    const [busy, setBusy] = useState(false);

    const fetchMore = async (reset = false) => {
        if (!consultationId) return;
        setBusy(true);
        try {
            const data = await listMessages({
                consultationId,
                cursor: reset ? 0 : cursor,
                limit: 50,
            });
            const merged = reset ? data : [...items, ...data];
            setItems(merged);
            const lastSeq = data.length ? data[data.length - 1].sequence : cursor;
            setCursor(lastSeq || cursor);
        } finally {
            setBusy(false);
        }
    };

    useEffect(() => {
        setItems([]);
        setCursor(0);
        if (consultationId) fetchMore(true);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [consultationId]);

    return (
        <Section title="Chat" right={<Badge>{consultationId}</Badge>}>
            <div className="max-h-[60vh] overflow-auto pr-2 flex flex-col gap-2">
                {items.length === 0 && (
                    <div className="text-slate-500 text-sm">No messages yet.</div>
                )}

                {items.map((m) => {
                    const isSelf =
                        m.authorId === identity.userId && m.authorRole === identity.role;
                    return (
                        <div
                            key={`${m.consultationId}-${m.sequence}`}
                            className={`flex ${isSelf ? "justify-end" : "justify-start"}`}
                        >
                            <div
                                className={`max-w-[75%] rounded-2xl px-4 py-2 shadow border ${
                                    isSelf
                                        ? "bg-slate-900 text-white border-slate-800"
                                        : "bg-white text-slate-900 border-slate-200"
                                }`}
                            >
                                <div className="text-[10px] opacity-70 mb-1 flex items-center gap-2">
                  <span className="font-mono">
                    {m.authorRole}:{m.authorId}
                  </span>
                                    <span>·</span>
                                    <span>{new Date(m.timestamp).toLocaleString()}</span>
                                </div>

                                {m.content?.type === "TEXT" && (
                                    <div className="whitespace-pre-wrap break-words">
                                        {m.content.text}
                                    </div>
                                )}

                                {m.content?.type === "MEDIA" && (
                                    <div className="space-y-2">
                                        <div className="text-xs opacity-80">
                                            Media{" "}
                                            <span className="font-mono">
                        {m.content.media?.storageKey || m.content.storageKey}
                      </span>
                                        </div>
                                        {String(m.content.mimeType || "").startsWith("image/") && (
                                            <img
                                                src={(m.content.media?.storageKey || m.content.storageKey)?.replace(
                                                    "local:",
                                                    "file:"
                                                )}
                                                alt="media"
                                                className="rounded-xl border max-h-72"
                                                onError={(e) => {
                                                    e.currentTarget.style.display = "none";
                                                }}
                                            />
                                        )}
                                    </div>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>

            <div className="mt-4 flex items-center gap-3">
                <Button onClick={() => fetchMore(false)} disabled={busy}>
                    {busy ? "Loading..." : "Load more"}
                </Button>
                <Link to="/consultations" className="text-sm underline text-slate-600">
                    Back to consultations
                </Link>
            </div>
        </Section>
    );
}
