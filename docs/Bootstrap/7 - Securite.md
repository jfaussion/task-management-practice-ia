## 🔐 Sécurité & Performance  

### 🛡️ Sécurité API  
- **Désactivation de CSRF** (inutile pour API REST).  
- **Activation de CORS** pour autoriser uniquement **`http://localhost:4200`**.  
- **Protection XSS conservée** (déjà activée par Spring et Angular).  
- **Pas de regex spécifiques** pour la validation des entrées.  

### 🔄 Performance  
- **Pagination ajoutée sur les endpoints renvoyant des listes volumineuses** (`/utilisateurs`, `/taches`).  
- **Pas de compression HTTP (`gzip`) activée**.  
