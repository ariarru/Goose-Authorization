import matplotlib.pyplot as plt
import numpy as np

# Funzione per leggere i dati dal file di latenza
def read_latency_data(file_path):
    latencies = []
    try:
        with open(file_path, 'r') as file:
            for line in file:
                try:
                    # Prova a convertire ogni riga in un numero float
                    latency = float(line.strip())  # rimuove gli spazi vuoti alla fine e all'inizio
                    latencies.append(latency)
                except ValueError:
                    print(f"Errore nel convertire la riga in un numero: {line}")
    except FileNotFoundError:
        print("Il file non esiste.")
    return latencies

# Funzione per fare l'analisi statistica
def analyze_latencies(latencies):
    if len(latencies) == 0:
        print("Nessun dato di latenza disponibile.")
        return

    # Calcola le statistiche di base
    mean_latency = np.mean(latencies)
    median_latency = np.median(latencies)
    std_dev_latency = np.std(latencies)
    min_latency = np.min(latencies)
    max_latency = np.max(latencies)

    print("Analisi delle latenze:")
    print(f"Latente media: {mean_latency:.2f} ms")
    print(f"Latente mediana: {median_latency:.2f} ms")
    print(f"Deviazione standard: {std_dev_latency:.2f} ms")
    print(f"Latente minima: {min_latency} ms")
    print(f"Latente massima: {max_latency} ms")

    return mean_latency, median_latency, std_dev_latency, min_latency, max_latency

# Funzione per visualizzare un grafico delle latenze
def plot_latencies(latencies):
    if len(latencies) == 0:
        print("Nessun dato di latenza disponibile per il grafico.")
        return

    # Crea il grafico
    plt.figure(figsize=(10, 6))
    plt.plot(latencies, marker='o', linestyle='-', color='b', label='Latenza')
    plt.title('Andamento della latenza nel tempo')
    plt.xlabel('Esecuzioni')
    plt.ylabel('Latenza (ms)')
    plt.grid(True)
    plt.legend()
    plt.show()

# Percorso del file
file_path = r'C:\Users\claud\Documents\Goose-Authorization\Analisi\latenza.txt'  

# Leggi i dati di latenza dal file
latencies = read_latency_data(file_path)

# Analizza i dati di latenza
analyze_latencies(latencies)

# Visualizza il grafico delle latenze
plot_latencies(latencies)
