import Link from "next/link";
import Icon from "./components/Icon";
import MapContainer from "./components/MapContainer";
import styles from "./page.module.css";

export default function Home() {
  
  return (
    <main className={styles.main}>
      <header className="p-4 flex horizontal justify-items-end place-items-end w-full">
        <Icon classes="float-right absolute right-4 top-4">
          <Link href="/login">
              <svg xmlns="http://www.w3.org/2000/svg" width="2.5em" height="2.5em" viewBox="0 0 24 24"><g fill="none" fillRule="evenodd">
              <path d="M24 0v24H0V0zM12.594 23.258l-.012.002l-.071.035l-.02.004l-.014-.004l-.071-.036q-.016-.004-.024.006l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427q-.004-.016-.016-.018m.264-.113l-.014.002l-.184.093l-.01.01l-.003.011l.018.43l.005.012l.008.008l.201.092q.019.005.029-.008l.004-.014l-.034-.614q-.005-.018-.02-.022m-.715.002a.02.02 0 0 0-.027.006l-.006.014l-.034.614q.001.018.017.024l.015-.002l.201-.093l.01-.008l.003-.011l.018-.43l-.003-.012l-.01-.01z"/>
              <path fill="#1b263b" d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10s10-4.477 10-10S17.523 2 12 2M8.5 9.5a3.5 3.5 0 1 1 7 0a3.5 3.5 0 0 1-7 0m9.758 7.484A7.99 7.99 0 0 1 12 20a7.99 7.99 0 0 1-6.258-3.016C7.363 15.821 9.575 15 12 15s4.637.821 6.258 1.984"/>
              </g>
            </svg>
          </Link>
        </Icon>
      </header>
      <section>
        <h1 className="text-4xl text-center">Welcome to Goose Authorization</h1>
        <p className="text-center">Please <Link href="/login" className="text-sky-500">log in</Link> to use the platform...</p>
        <MapContainer />
      </section>
      
    </main>
  );
}
