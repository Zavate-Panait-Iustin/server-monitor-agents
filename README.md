 Server-monitor-agent

Aplicație de monitorizare a serverelor(cpu+ram usage) folosind agenți JADE.

 Studenți
- Student #1: Zavate Panait-Iustin
- Student #2: Gavrilescu Liviu

 Descriere
Această aplicație creează un ecosistem de agenți:
- ServerAgent – colectează CPU% și RAM% de la server
- MonitorAgent – afișează dashboard centralizat
- LoggerAgent – salvează istoric în history.txt

Funcționalități:
- Tabel cu valori CPU, RAM și Ping
- Sparkline pentru istoric
- LED de ping cu valoare exactă
- Butoane Pause / Resume

 Instalare
1. Deschide proiectul în Eclipse.
2. Adaugă JADE 4.6.0 în Build Path.
3. Rulează main.MainContainer pentru a porni ecosistemul de agenți.

 Protocol
- Agenții comunică folosind ACLMessage (INFORM).
- Mesaje către MonitorAgent și LoggerAgent.
- Toți agenții sunt înregistrați la DF pentru identificarea serviciilor.

 Observații
- Proiect realizat cu Java 1.8 și OSHI pentru monitorizare.
- Istoricul este salvat în history.txt în folderul proiectului.
