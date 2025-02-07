## 💾 Gestion des Données & Base de Données  

### 🗄️ Base de Données  
- **H2 en mémoire pour les tests unitaires/intégration**.  
- **PostgreSQL via Docker pour l’exécution locale**.  
- **ORM : Spring Data JPA avec Hibernate**.  
- **Gestion des transactions : `@Transactional`**.  

### 🔄 Versioning du Schéma (Approche Mixte)  
✅ **Développement local rapide :**  
- `ddl-auto=update` activé uniquement en local.  

✅ **Gestion propre des évolutions :**  
- **Liquibase** pour versionner les migrations et écrire des scripts SQL manuels avec les stagiaires.  
- Suivi des changements via la table `DATABASECHANGELOG`.  

### 🚀 Optimisation  
- **Indexation des colonnes critiques** (`id`, `name`, clés étrangères) pour accélérer les requêtes.  
- **Pas de cache supplémentaire** (pas de Redis ni Caffeine).  
- **Pas de moteur de recherche avancé (Elasticsearch, Meilisearch, etc.)**.  
