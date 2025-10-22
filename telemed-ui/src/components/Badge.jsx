export default function Badge({ children }) {
    return (
        <span className="px-2 py-0.5 text-xs rounded-full bg-slate-100 text-slate-600 border border-slate-200">
      {children}
    </span>
    );
}
