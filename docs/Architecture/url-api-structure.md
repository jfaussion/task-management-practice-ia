## 🎯 Project Context & Objectives

- Application web avec API REST (Spring Boot + Angular)
- Langue des routes : **Anglais**
- Organisation : **Pas de séparation par rôle** (admin/user)
- Authentification : **Aucune gestion d'authentification prévue**
- Pages clés (routes frontend/API) :
  - `GET /tasks` → Liste des tâches
  - `GET /tasks/:id` → Détail d’une tâche
  - `POST /tasks` → Création d’une tâche
  - `PUT /tasks/:id` → Mise à jour d’une tâche
  - `DELETE /tasks/:id` → Suppression d’une tâche
  - (Optionnel) `GET /tasks/:id/estimate` → Estimation du temps (Décorateur)
- Routes publiques : ✅ Toutes les routes sont ouvertes (pas de protection d’accès)


## 🌐 Frontend URL Structure

- ✅ Convention de nommage : **kebab-case** (ex. `/task-details`)
- ✅ Utilisation de **path parameters** :
  - `:id` pour les tâches individuelles → ex. `/tasks/:id`
- ✅ Utilisation de **query parameters** :
  - Pour le filtrage et le tri → ex. `/tasks?status=done&sort=priority`
- ❌ Aucune route optimisée pour le SEO nécessaire (ex. pas de `/tasks/completed`)
- ✅ Toutes les routes sont **publiques**, aucune distinction admin/user dans l’URL
- 🔒 Aucune donnée sensible dans les URLs


## 🔌 Backend API Design

- ✅ **Style** : API REST avec Spring Boot
- ✅ **Versioning activé** : toutes les routes seront préfixées par `/api/v1`
- ✅ **Ressource principale** : `/tasks`
  - `GET /api/v1/tasks` → Liste paginée des tâches
  - `GET /api/v1/tasks/:id` → Détail d'une tâche
  - `POST /api/v1/tasks` → Création d'une tâche
  - `PUT /api/v1/tasks/:id` → Mise à jour
  - `DELETE /api/v1/tasks/:id` → Suppression
- ✅ **Ressource imbriquée** :
  - `GET /api/v1/users/:id/tasks` → Liste des tâches d’un utilisateur
- ✅ **Filtres et tri via query params** :
  - Ex : `/api/v1/tasks?status=done&priority=high&page=1&limit=10`
- ✅ **Traitement par lot prévu** :
  - `POST /api/v1/tasks/batch` → Création multiple
  - `DELETE /api/v1/tasks/batch` → Suppression multiple (IDs en body)
- ✅ Mapping DTOs/Entities via **MapStruct**


## 🔒 Security & Access Management

- ✅ **Rôles définis** : `USER`, `ADMIN`, stockés en base
- ✅ **Contrôle d’accès simulé** :
  - Certains endpoints peuvent être **restreints au rôle ADMIN**
  - Exemple pédagogique sans système de login
- ❌ **Aucune authentification** : pas d’OAuth, JWT ou session utilisateur
- ✅ **CSRF désactivé** (API REST)
- ✅ **CORS activé** : uniquement pour `http://localhost:4200`
- ✅ **Protection contre les abus activée** :
  - Exemple : rate limiting à 10 requêtes/sec sur `/api/v1/tasks`
  - Implémentable via annotation ou interceptor
- ❌ Pas de gestion de sessions temporaires ou d’URL sécurisée
- ✅ Projet exécuté **en local uniquement** → pas de contrainte RGPD ou SSO


## ⚠️ Error Handling & HTTP Status Codes

- ✅ Centralisation des erreurs avec `@ControllerAdvice` (Spring Boot)
- ✅ Validation des entrées avec Jakarta (`@Valid`, etc.)
- ✅ Format JSON standardisé pour toutes les erreurs :
  ```json
  {
    "timestamp": "2025-03-21T10:25:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed for field 'name'",
    "path": "/api/v1/tasks",
    "errorId": "abc123xyz"
  }
  ```
- ❌ Pas de message métier personnalisé (ex : règle des 5 tâches)
- ✅ Ajout d’un champ `errorId` (UUID généré) pour aider à la traçabilité dans les logs
- ✅ Statuts HTTP utilisés :
  - `200 OK` – Requête réussie
  - `201 Created` – Ressource créée
  - `204 No Content` – Suppression réussie
  - `400 Bad Request` – Données invalides
  - `401 Unauthorized` – Non utilisé ici (pas d'authent)
  - `403 Forbidden` – Accès refusé (ex : restriction rôle ADMIN)
  - `404 Not Found` – Ressource absente
  - `409 Conflict` – Conflit métier (optionnel)
  - `429 Too Many Requests` – Protection anti-abus
  - `500 Internal Server Error` – Erreur inattendue


## 🚀 Performance & Caching Strategy

- ❌ Aucun cache backend activé (pas de Redis, Caffeine, etc.)
- ❌ Pas de stratégie de cache HTTP (pas de `Cache-Control`, ni `ETag`)
- ✅ Pagination activée sur les endpoints à fort volume (`/users`, `/tasks`)
- ❌ Pas d’optimisation spécifique pour les ressources statiques côté frontend
- ❌ Pas de pré-caching frontend (pas de LocalStorage, memory caching ou service worker)
- ✅ Approche simple et efficace adaptée à un usage **local et formateur**


## 📊 Monitoring & Scalability

- ✅ **Logging** activé via **Logback** (console uniquement)
  - Niveau `DEBUG` → pour les tests unitaires
  - Niveau `INFO` → pour l'exécution locale
- ❌ Pas de format JSON pour les logs
- ❌ Aucun fichier de log distinct (tout reste en console)
- ❌ **Spring Boot Actuator non activé**
- ❌ Aucun outil externe de monitoring ou visualisation (ex. Prometheus)
- ❌ Pas de système de scalabilité implémenté (pas de Load Balancer, CDN, etc.)
- ❌ Pas de mécanisme d’audit ou de journalisation avancée des accès API
- ✅ Cohérent avec une **exécution locale, jetable et simple**, axée sur la pédagogie IA


