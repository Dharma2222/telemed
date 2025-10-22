import axios from "axios";

export const API_BASE = import.meta?.env?.VITE_API_BASE || "http://localhost:8080";
export const api = axios.create({ baseURL: API_BASE, timeout: 15000 });

// ---- Endpoints ----
export async function searchConsultations({ role, userId }) {
    const params = role === "DOCTOR" ? { doctorId: userId } : { patientId: userId };
    const { data } = await api.get("/consultations", { params });
    return data;
}

export async function createConsultation(patientId, doctorId) {
    const { data } = await api.post("/consultations", { patientId, doctorId });
    return data;
}

export async function uploadMedia(file) {
    const form = new FormData();
    form.append("file", file);
    const { data } = await api.post("/media/files", form, {
        headers: { "Content-Type": "multipart/form-data" },
    });
    return data; // { storageKey }
}

export async function postMessage({ consultationId, idemKey, authorId, authorRole, payload }) {
    const { data } = await api.post(
        `/consultations/${consultationId}/messages`,
        { authorId, authorRole, content: payload },
        { headers: { "Idempotency-Key": idemKey } }
    );
    return data;
}

export async function listMessages({ consultationId, authorRole, cursor, limit = 50 }) {
    const params = {};
    if (authorRole && authorRole !== "ALL") params.authorRole = authorRole;
    if (cursor) params.cursor = cursor;
    if (limit) params.limit = limit;
    const { data } = await api.get(`/consultations/${consultationId}/messages`, { params });
    return data;
}
