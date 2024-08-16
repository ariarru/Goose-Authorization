import MapContainer from "./components/MapContainer";
import styles from "./page.module.css";

export default function Home() {

  
  return (
    <main className={styles.main}>
      <h1>Welcome to Goose Authorization</h1>
      <MapContainer />
    </main>
  );
}
