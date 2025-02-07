## 📊 Observabilité & Maintenance  

### 📜 Gestion des Logs  
- **Logger : Logback (par défaut dans Spring Boot)**.  
- **Format des logs : Texte brut (pas de JSON)**.  
- **Pas de fichier de logs séparé**, tout est affiché dans la console.  

### 🔎 Niveaux de Logs  
- **`DEBUG`** activé uniquement pour les **tests unitaires**.  
- **`INFO`** activé pour l’exécution locale et les autres environnements.  

### 📡 Monitoring & Debugging  
- **Activation optionnelle de Spring Boot Actuator**, si cela ne génère pas trop d’overhead.  
- **Endpoints de monitoring** (`/health`, `/metrics`, etc.) accessibles en local si activé.  
- **Pas de système avancé de monitoring (Prometheus, Grafana, etc.)**, projet local uniquement.  

