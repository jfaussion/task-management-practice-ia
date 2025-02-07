## 📌 Besoins & Contraintes  

### 🎯 Objectif  
Créer une application **de gestion de tâches** avec **Java Spring Boot (backend)** et **Angular (frontend)**, intégrant l’IA comme **support au développement** (génération de tests unitaires, documentation, correction de bugs).  

### 🏗️ Type d’Application  
- **Application web** avec **API REST**.  
- Démonstration des cas d’usage IA en développement.  

### 🔑 Fonctionnalités principales  
✅ **CRUD** des tâches.  
✅ Assignation de tâches avec **limite de 5 par utilisateur** (TDD).  
✅ Gestion transactionnelle et correction de bugs.  
✅ **Génération automatique** de tests unitaires et documentation (JavaDoc, Mermaid).  
✅ Estimation du temps des tâches (**Pattern Décorateur**).  

### 👥 Utilisateurs & Authentification  
- **Rôles souhaités** (ex: Admin, Utilisateur), mais pas d’authentification complexe.  
- Proposition pour simplifier : **Stocker les utilisateurs en local (Base de données) et gérer les rôles via un champ en base.**  
- Pas de gestion de token ni OAuth pour éviter la complexité.  

### 🚫 Contraintes spécifiques  
- **Projet jetable pour la formation** → Pas d’exigence RGPD ou haute disponibilité.  

### 🔗 Intégrations externes  
- **Aucune intégration d’API tierce** (GitHub, Jira, etc.).  
- IA uniquement en support des développeurs, pas intégrée dans l’application.  
