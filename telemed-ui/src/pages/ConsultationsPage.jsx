import React, { useEffect, useState } from "react";
import Section from "../components/Section.jsx";
import Badge from "../components/Badge.jsx";
import { Link, useNavigate } from "react-router-dom";
import { useToast } from "../utils/hooks.jsx";
import { searchConsultations } from "../api/client.js";
import { useIdentity } from "../context/IdentityContext.jsx";

export default function ConsultationsPage() {
    const { identity } = useIdentity();
    const [items, setItems] = useState([]);
    const { setMsg, Toast } = useToast();
    const nav = useNavigate();

    useEffect(() => {
        (async () => {
            try {
                if (!identity?.userId) return;
                const list = await searchConsultations({
                    role: identity.role,
                    userId: identity.userId,
                });
                setItems(list || []);
            } catch (e) {
                setMsg(e?.response?.data?.message || e.message);
            }
        })();
    }, [identity]);

    return (
        <Section title="Your Consultations" right={<Badge>{identity.role}:{identity.userId}</Badge>}>
            {!items?.length ? (
                <div className="text-sm text-slate-500">No consultations found for this identity.</div>
            ) : (
                <ul className="divide-y divide-slate-200 rounded-xl border border-slate-200 overflow-hidden">
                    {items.map((c) => (
                        <li
                            key={c.id}
                            className="p-3 bg-white hover:bg-slate-50 cursor-pointer"
                            onClick={() => nav(`/chat/${c.id}`)}
                        >
                            <div className="flex items-center justify-between">
                                <div className="font-medium text-slate-800">Consultation</div>
                                <div className="text-xs text-slate-500 font-mono">
                                    {new Date(c.createdAt).toLocaleString()}
                                </div>
                            </div>
                            <div className="text-xs text-slate-600 mt-1">
                                Doctor: <span className="font-mono">{c.doctorId}</span> · Patient:{" "}
                                <span className="font-mono">{c.patientId}</span>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
            <div className="mt-3 text-xs text-slate-500">
                Need to switch identity? <Link className="underline" to="/">Go back</Link>
            </div>
            <Toast />
        </Section>
    );
}
