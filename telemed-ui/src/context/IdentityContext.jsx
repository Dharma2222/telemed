import React, { createContext, useContext, useEffect, useState } from "react";

const IdentityCtx = createContext(null);
export const useIdentity = () => useContext(IdentityCtx);

export function IdentityProvider({ children }) {
    const [identity, setIdentity] = useState(() => {
        try {
            return JSON.parse(localStorage.getItem("telemed.identity") || "null") || {
                role: "PATIENT",
                userId: "",
            };
        } catch {
            return { role: "PATIENT", userId: "" };
        }
    });

    useEffect(() => {
        localStorage.setItem("telemed.identity", JSON.stringify(identity));
    }, [identity]);

    return (
        <IdentityCtx.Provider value={{ identity, setIdentity }}>
            {children}
        </IdentityCtx.Provider>
    );
}
