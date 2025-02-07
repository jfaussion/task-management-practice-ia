## 🚀 Hébergement & Déploiement  

### 🏠 Hébergement  
- **Pas d’hébergement distant** → L’application sera exécutée **en local sur le poste des stagiaires**.  
- **Backend et Frontend lancés localement** sans orchestration.  

### 🔄 CI/CD  
- **Aucun pipeline d’automatisation** → Lancement manuel des builds et tests.  

### 🛠️ Infrastructure  
- **Pas d’Infrastructure as Code (IaC)**.  
- **Pas de conteneurs distincts**, tout tourne sur l’environnement local.  

### 🗄️ Base de Données  
- **H2 en mémoire** pour les **tests unitaires/intégration**.  
- **PostgreSQL via Docker** pour tester l’application en local.  

### 🔧 Outils  
- **Lancement simple** avec un script (`run.sh` ou `run.bat` pour faciliter l’exécution).  
