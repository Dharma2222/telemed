export default function Select(props) {
    return (
        <select
            {...props}
            className={
                "w-full rounded-xl border border-slate-300 px-3 py-2 outline-none focus:ring-2 focus:ring-slate-400 " +
                (props.className || "")
            }
        />
    );
}
