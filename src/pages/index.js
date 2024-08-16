import Head from 'next/head';
import styles from '../styles/Home.module.css';
import Synchronize from "ol-ext/interaction/Synchronize";
import { useEffect, useState } from 'react';
import Map1 from '../components/Map';

export default function Home() {
  const [mapObject, setMapObject] = useState(null);
  useEffect(() => {
    if(!mapObject) return;
    var synchronize = new Synchronize({ maps: [mapObject] });
    mapObject.addInteraction( synchronize );
    return () => {
      if(mapObject) mapObject.removeInteraction(synchronize);
    }
  }, [mapObject])
  return (
    <div className={styles.container}>
      <Head>
        <title>Goose Authorization</title>
        <link rel="icon" href="/favicon.ico" />
        <link rel="stylesheet" href="../styles/output.css" />
      </Head>

      <main>
        <h1 className={styles.title}>
          Welcome to Goose Authorization
        </h1>

        <p className={styles.description}>
          Get started by editing <code>pages/index.js</code>
        </p>

        <div className="flex h-[100vh] gap-[2px] bg-white/70" >
          <div className='relative w-1/2   border border-transparent'>
          <Map1 setMap1Object={setMapObject}/>
          </div>
        </div>
      </main>

      <footer>
       <p>vediamo cosa riusciamo a sistemare qui</p>
      </footer>

      <style jsx>{`
        main {
          padding: 5rem 0;
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
        }
        footer {
          width: 100%;
          height: 100px;
          border-top: 1px solid #eaeaea;
          display: flex;
          justify-content: center;
          align-items: center;
        }
        footer img {
          margin-left: 0.5rem;
        }
        footer a {
          display: flex;
          justify-content: center;
          align-items: center;
          text-decoration: none;
          color: inherit;
        }
        code {
          background: #fafafa;
          border-radius: 5px;
          padding: 0.75rem;
          font-size: 1.1rem;
          font-family:
            Menlo,
            Monaco,
            Lucida Console,
            Liberation Mono,
            DejaVu Sans Mono,
            Bitstream Vera Sans Mono,
            Courier New,
            monospace;
        }
      `}</style>

      <style jsx global>{`
        html,
        body {
          padding: 0;
          margin: 0;
          font-family:
            -apple-system,
            BlinkMacSystemFont,
            Segoe UI,
            Roboto,
            Oxygen,
            Ubuntu,
            Cantarell,
            Fira Sans,
            Droid Sans,
            Helvetica Neue,
            sans-serif;
        }
        * {
          box-sizing: border-box;
        }
      `}</style>
    </div>
  );
}
