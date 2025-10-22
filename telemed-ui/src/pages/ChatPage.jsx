import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { useIdentity } from "../context/IdentityContext.jsx";
import MessageThread from "../components/MessageThread.jsx";
import MessageComposerBar from "../components/MessageComposerBar.jsx";

export default function ChatPage() {
    const { identity } = useIdentity();
    const { id: consultationId } = useParams();
    const [refreshToken, setRefreshToken] = useState(0);

    return (
        <div className="space-y-4">
            <MessageThread key={`thread-${refreshToken}`} consultationId={consultationId} />
            <MessageComposerBar
                consultationId={consultationId}
                authorId={identity.userId}
                authorRole={identity.role}
                onSent={() => setRefreshToken((v) => v + 1)}
            />
        </div>
    );
}
