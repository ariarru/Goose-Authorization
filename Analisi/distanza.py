import numpy as np
import matplotlib.pyplot as plt
import re

# Funzione per leggere il file e estrarre le distanze
def estrai_distanze_da_file(file_path):
    try:
        # Apre il file in modalità lettura
        with open(file_path, 'r') as file:
            # Leggi tutto il contenuto del file
            file_data = file.read()

            # Usa espressioni regolari per trovare tutte le distanze (valori numerici con punto decimale)
            distanze = re.findall(r"(\d+\.\d+)", file_data)

            # Converte i risultati in un array di numeri float
            distanze = [float(distanza) for distanza in distanze]
            return distanze

    except Exception as e:
        print(f"Errore nel leggere il file: {e}")
        return []

def analisi():
    # Percorso del file
    file_path = r'C:\Users\claud\Documents\Goose-Authorization\Analisi\distanza.txt'
    
    # Estrai le distanze dal file
    distanze = estrai_distanze_da_file(file_path)

    # Verifica che la lista delle distanze non sia vuota
    if not distanze:
        print("Errore: nessuna distanza trovata nel file.")
        return

    # Analisi delle distanze: calcola media, deviazione standard e grafico
    media_distanze = np.mean(distanze)
    deviazione_standard = np.std(distanze)
    errore_medio = np.mean(np.abs(np.array(distanze) - media_distanze))

    # Stampa i risultati
    print(f"Statistiche delle distanze stimate:")
    print(f" - Media della distanza: {media_distanze:.2f} metri")
    print(f" - Deviazione standard della distanza: {deviazione_standard:.2f} metri")
    print(f" - Errore medio sulla stima: {errore_medio:.2f} metri")


 # Istogramma delle distanze
    plt.figure(figsize=(8, 5))
    plt.hist(distanze, bins=10, color='blue', alpha=0.7, edgecolor='black')
    plt.title("Distribuzione delle distanze stimate")
    plt.xlabel("Distanza (metri)")
    plt.ylabel("Frequenza")
    plt.grid(True)
    plt.show()

    # Grafico delle distanze
    plt.figure(figsize=(8, 5))
    plt.plot(distanze, marker='o', linestyle='-', color='b')
    plt.title("Distanze stimate a partire dal RSSI")
    plt.xlabel("Rilevazioni")
    plt.ylabel("Distanza (metri)")
    plt.grid(True)

    # Aggiungi una linea per la media
    plt.axhline(y=media_distanze, color='r', linestyle='--', label=f"Media: {media_distanze:.2f}m")
    plt.legend()
    plt.show()

    # Calcolo dell'errore quadratico medio (RMSE)
    rmse = np.sqrt(np.mean((np.array(distanze) - media_distanze) ** 2))
    print(f" - Errore quadratico medio (RMSE): {rmse:.2f} metri")


# Esegui l'analisi
analisi()
