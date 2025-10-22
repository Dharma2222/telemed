export default function Field({ label, children }) {
    return (
        <label className="block mb-3">
            <span className="block text-sm text-slate-600 mb-1">{label}</span>
            {children}
        </label>
    );
}
