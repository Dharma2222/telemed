import { Link, useNavigate } from "react-router-dom";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Select from "../components/Select.jsx";
import TextInput from "../components/TextInput.jsx";
import Button from "../components/Button.jsx";
import { useIdentity } from "../context/IdentityContext.jsx";
import { useToast } from "../utils/hooks.jsx";
import { API_BASE, createConsultation } from "../api/client.js";
import React, { useState } from "react";

export default function IdentityPage() {
    const { identity, setIdentity } = useIdentity();
    const [role, setRole] = useState(identity.role || "PATIENT");
    const [userId, setUserId] = useState(identity.userId || "");
    const { setMsg, Toast } = useToast();
    const nav = useNavigate();

    const saveAndNext = () => {
        if (!userId) return setMsg("Enter your ID");
        setIdentity({ role, userId });
        nav("/consultations");
    };

    return (
        <div className="space-y-6">
            <Section
                title="Select Identity"
                right={<span className="text-xs text-slate-500">API: {API_BASE}</span>}
            >
                <div className="grid md:grid-cols-3 gap-4">
                    <Field label="Role">
                        <Select value={role} onChange={(e) => setRole(e.target.value)}>
                            <option value="PATIENT">PATIENT</option>
                            <option value="DOCTOR">DOCTOR</option>
                        </Select>
                    </Field>
                    <Field label="Your ID">
                        <TextInput
                            value={userId}
                            onChange={(e) => setUserId(e.target.value)}
                            placeholder="Enter your user ID"
                        />
                    </Field>
                    <div className="flex items-end">
                        <Button onClick={saveAndNext} className="w-full">
                            Continue
                        </Button>
                    </div>
                </div>
                <div className="mt-2 text-xs text-slate-500">
                    Only participants (doctor/patient) of a consultation can send messages.
                </div>
                <Toast />
            </Section>

            <DevCreateConsultationInline />
        </div>
    );
}

function DevCreateConsultationInline() {
    const [patientId, setPatientId] = useState("");
    const [doctorId, setDoctorId] = useState("");
    const { setMsg, Toast } = useToast();
    const nav = useNavigate();

    return (
        <Section title="Dev Helper: Create Consultation">
            <div className="grid md:grid-cols-3 gap-3">
                <TextInput
                    placeholder="Patient ID"
                    value={patientId}
                    onChange={(e) => setPatientId(e.target.value)}
                />
                <TextInput
                    placeholder="Doctor ID"
                    value={doctorId}
                    onChange={(e) => setDoctorId(e.target.value)}
                />
                <Button
                    onClick={async () => {
                        if (!patientId || !doctorId) return setMsg("Enter patient & doctor IDs");
                        const c = await createConsultation(patientId, doctorId);
                        setMsg("Created");
                        nav(`/chat/${c.id}`);
                    }}
                >
                    Create & Open
                </Button>
            </div>
            <Toast />
        </Section>
    );
}
