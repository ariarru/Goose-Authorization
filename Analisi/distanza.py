import json
import math
import numpy as np
import matplotlib.pyplot as plt

# Funzione per calcolare la distanza a partire dal valore RSSI
def calcola_distanza(RSSI, A, n):
    distanza = 10 ** ((A - RSSI) / (10 * n))
    return distanza

# Funzione per leggere il file JSON e estrarre i valori di RSSI
def leggi_rssi_da_json(nome_file):
    with open(nome_file, 'r') as f:
        dati = json.load(f)
    return [misurazione['rssi'] for misurazione in dati]

# Parametri fissi per la calibrazione 
A = -60  # potenza a 1 metro (in dBm)
n = 2.5  # esponente di attenuazione, valore medio in ambienti tipici

# Leggi le misurazioni RSSI dal file JSON
rssi_values = leggi_rssi_da_json('..\GooseApp\app\src\main\java\com\example\gooseapp\service\val_rssi.json')

# Calcola la distanza per ogni misurazione
distanze = [calcola_distanza(rssi, A, n) for rssi in rssi_values]

# Analisi delle distanze: calcola media, deviazione standard e grafico
media_distanze = np.mean(distanze)
deviazione_standard = np.std(distanze)

# Stampa i risultati
print(f"Statistiche delle distanze stimate:")
print(f" - Media della distanza: {media_distanze:.2f} metri")
print(f" - Deviazione standard della distanza: {deviazione_standard:.2f} metri")

# Grafico delle distanze
plt.figure(figsize=(8, 5))
plt.plot(distanze, marker='o', linestyle='-', color='b')
plt.title("Distanze stimate a partire dal RSSI")
plt.xlabel("Rilevazioni")
plt.ylabel("Distanza (metri)")
plt.grid(True)
plt.show()
