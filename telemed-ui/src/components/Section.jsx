export default function Section({ title, right, children }) {
    return (
        <div className="bg-white/70 backdrop-blur rounded-2xl shadow p-5 border border-slate-200">
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-semibold text-slate-800">{title}</h2>
                {right}
            </div>
            {children}
        </div>
    );
}
