// Usage:
//   API=http://localhost:8081 node seed_api.js
//   # or if using compose proxy:
//   # API=http://localhost:8088/api node seed_api.js
// Optional pacing between messages (ms):
//   SLEEP=300 node seed_api.js

const axios = require("axios");
const crypto = require("crypto");

const API = process.env.API || "http://localhost:8080";
const SLEEP = Number(process.env.SLEEP || 300); // ms between posts

const http = axios.create({
  baseURL: API,
  timeout: 15000,
  validateStatus: s => s >= 200 && s < 600, // we'll handle errors explicitly
});

const sleep = (ms) => new Promise(r => setTimeout(r, ms));

function uniqueKey(base) {
  // Make every idempotency key unique per run to avoid any stale "in-progress" collisions:
  const rand = crypto.randomBytes(4).toString("hex");
  return `${base}-${Date.now()}-${rand}`;
}

async function withRetry(fn, { max = 8, base = 200, factor = 1.8, maxDelay = 2500 } = {}) {
  let attempt = 0;
  // eslint-disable-next-line no-constant-condition
  while (true) {
    try {
      return await fn();
    } catch (err) {
      const status = err.response?.status;
      const retriable =
        status === 409 || status === 425 || status === 429 || (status >= 500 && status < 600) ||
        err.code === "ECONNABORTED" || err.code === "ECONNRESET" || err.code === "ENETUNREACH";

      attempt += 1;
      if (!retriable || attempt > max) throw err;

      const delay = Math.min(Math.round(base * Math.pow(factor, attempt - 1) + Math.random() * 100), maxDelay);
      await sleep(delay);
    }
  }
}

async function createConsultation(patientId, doctorId) {
  const res = await withRetry(() => http.post("/consultations", { patientId, doctorId }));
  if (res.status >= 300) throw new Error(`Create consultation failed: ${res.status} ${JSON.stringify(res.data)}`);
  console.log(`Created consultation: ${res.data.id}  (${patientId} ↔ ${doctorId})`);
  return res.data.id;
}

async function postText(consultationId, role, authorId, keyBase, text) {
  const idem = uniqueKey(keyBase);
  const res = await withRetry(() =>
    http.post(
      `/consultations/${consultationId}/messages`,
      { authorRole: role, authorId, content: { type: "TEXT", text } },
      { headers: { "Idempotency-Key": idem } }
    )
  );
  if (res.status >= 300) {
    throw new Error(`Post text failed (${consultationId}): ${res.status} ${JSON.stringify(res.data)}`);
  }
  console.log(`  → [${consultationId}] ${role}:${authorId}  ${text}`);
  await sleep(SLEEP); // small spacing to let locks clear
  return res.data;
}

async function verify(consultationId) {
  const res = await http.get(`/consultations/${consultationId}/messages`, { params: { limit: 100 } });
  if (res.status !== 200) throw new Error(`Verify failed: ${res.status} ${JSON.stringify(res.data)}`);
  const msgs = res.data || [];
  console.log(`\nMessages for ${consultationId} (${msgs.length}):`);
  msgs.forEach(m => {
    const body = m.content?.type === "TEXT" ? m.content.text : "[MEDIA]";
    console.log(`  #${m.sequence} ${m.authorRole}:${m.authorId} @ ${m.timestamp}  ${body}`);
  });
}

(async () => {
  try {
    console.log(`API base: ${API}`);

    // Create two consultations (or set env C1/C2 to reuse existing)
    const C1 = process.env.C1 || await createConsultation("P123", "D456");
    const C2 = process.env.C2 || await createConsultation("P789", "D012");

    // C1 — headaches
    await postText(C1, "PATIENT", "P123", "c1-1", "Hi Doctor, I’ve had a headache since yesterday.");
    await postText(C1, "DOCTOR",  "D456", "c1-2", "Any nausea, vision changes, or sensitivity to light?");
    await postText(C1, "PATIENT", "P123", "c1-3", "Light sensitivity, no nausea. Slept late recently.");
    await postText(C1, "DOCTOR",  "D456", "c1-4", "Hydration, rest, and acetaminophen 500 mg as needed.");
    await postText(C1, "PATIENT", "P123", "c1-5", "How often can I take it?");
    await postText(C1, "DOCTOR",  "D456", "c1-6", "Every 6–8 hours, max 3,000 mg/day. If worse, ping me.");

    // C2 — side effects
    await postText(C2, "PATIENT", "P789", "c2-1", "I started the new meds and feel drowsy.");
    await postText(C2, "DOCTOR",  "D012", "c2-2", "How long after taking it do you feel drowsy?");
    await postText(C2, "PATIENT", "P789", "c2-3", "About an hour later; lasts for 3–4 hours.");
    await postText(C2, "DOCTOR",  "D012", "c2-4", "Try taking it in the evening after dinner.");
    await postText(C2, "PATIENT", "P789", "c2-5", "Okay, I will try today.");
    await postText(C2, "DOCTOR",  "D012", "c2-6", "Avoid driving until you know your reaction.");
    await postText(C2, "PATIENT", "P789", "c2-7", "Got it, thanks.");
    await postText(C2, "DOCTOR",  "D012", "c2-8", "Check back in 48h with an update.");

    await verify(C1);
    await verify(C2);

    console.log("\n✅ Seed complete.");
  } catch (err) {
    console.error("\n❌ Seed failed:", err.message);
    if (err.response) {
      console.error("Status:", err.response.status);
      console.error("Body:", err.response.data);
    }
    process.exit(1);
  }
})();
