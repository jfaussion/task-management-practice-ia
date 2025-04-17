## 📦 Structure Complète du Projet  

### 🏗️ Backend (Java Spring Boot / NestJS en Clean Architecture)  
📂 `src/main/java/com/neosoft/practice_software/`  
- 📁 `domain/` → **Entités uniquement**.  
- 📁 `application/`  
  - 📁 `service/` → Logique métier.  
  - 📁 `dao/` → **Interfaces DAO pour l’accès aux données**.  
- 📁 `infrastructure/`  
  - 📁 `api/` → Contrôleurs REST.  
  - 📁 `jpa/` → **Implémentations JPA des DAO**.  

📂 `src/main/resources/`  
- 📁 `db/changelog/` → Scripts Liquibase.  
- 📄 `application.yml` → Config par défaut.  
- 📄 `application-test.yml` → Config pour les tests.  

📂 `scripts/`  
- 📄 `run.sh` → Lancer backend + frontend.  
- 📄 `setup-db.sh` → Initialisation de la BDD PostgreSQL via Docker.  


### 📊 Diagramme de l'Architecture

Pour une vue d'ensemble de l'architecture du projet, veuillez consulter le [diagramme d'architecture Java Spring boot](../../backend/springboot/docs/clean-archi-diagram.md) ou [diagramme d'architecture NestJS](../../backend/nestjs/docs/clean-archi-nestjs.md).



### 🎨 Frontend (Angular)  
📂 `src/`  
- 📁 `app/` → Composants et services.  
- 📁 `assets/` → Ressources statiques (icônes, images).  
- 📁 `environments/` → Configuration Angular (`environment.ts`, `environment.prod.ts`).  
- 📁 `styles/` → Fichiers Tailwind CSS globaux (`src/assets/styles/`).  

📄 `angular.json` → Configuration Angular CLI.  
📄 `package.json` → Dépendances et scripts.  

---

### ⚙️ Configuration & Environnements  
- `application.yml` (prod/dev par défaut).  
- `application-test.yml` (spécifique aux tests).  
- `environments/` classique pour Angular (`environment.ts`, `environment.prod.ts`).  

---

## 🚀 Points Clés  
✅ **Backend en Clean Architecture** avec séparation `domain/`, `application/`, `infrastructure/`.  
✅ **DAO dans `application/dao/` pour découpler la logique métier et la base**.  
✅ **Liquibase pour versionner le schéma**, `ddl-auto=update` pour le dev rapide.  
✅ **Frontend Angular structuré simplement**, Tailwind pour le style.  
✅ **Pas d’authentification complexe**, gestion des utilisateurs en base avec rôles.  
✅ **Pas d’asynchronisme**, tout reste synchrone.  
✅ **Monitoring basique avec logs Logback et (optionnellement) Spring Boot Actuator**.  
