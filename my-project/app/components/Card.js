export default function Card({ children,  add }) {
    let classes = 'bg-white shadow-md shadow-gray-300 rounded-lg mb-5 p-4';
    return (
        <div className={`${classes} ${add}`}>{children}</  div>
    )
}