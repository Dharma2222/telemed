export default function Button({ children, variant = "primary", ...rest }) {
    const base =
        "inline-flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-medium focus:outline-none active:scale-[.98] disabled:opacity-50 disabled:cursor-not-allowed";
    const styles = {
        primary: "bg-slate-900 text-white hover:bg-slate-800",
        secondary: "bg-white border border-slate-300 text-slate-800 hover:bg-slate-50",
        danger: "bg-rose-600 text-white hover:bg-rose-500",
    };
    return (
        <button {...rest} className={`${base} ${styles[variant] || styles.primary}`}>
            {children}
        </button>
    );
}
