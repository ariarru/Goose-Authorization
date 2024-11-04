# Dockerfile
FROM python:3.9

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file Python nella directory di lavoro
COPY backend/ .

# Installa Flask e Gunicorn
RUN pip install --no-cache-dir Flask gunicorn

# Espone le porte per i due servizi
EXPOSE 5001 5002

# Comando per avviare i due servizi
CMD ["gunicorn", "--bind", "0.0.0.0:5001", "Calcposizione:app", "&", "gunicorn", "--bind", "0.0.0.0:5002", "notifiche:app"]
