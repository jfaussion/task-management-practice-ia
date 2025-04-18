## ⚙️ Conception du Back-End  

### 🏗️ Architecture  
- **Pattern : Clean Architecture** (Séparation claire entre Domain, Application, Infrastructure).  
- **API REST** basée sur **Spring Boot**.  

### 📂 Organisation du Code  
- **Domain** : Entités métier et interfaces des repositories.  
- **Application** : Cas d’usage et services applicatifs.  
- **Infrastructure** : Implémentation des repositories et interactions avec la base de données.  
- **Adapters (Web/Controllers)** : Exposition des endpoints REST.  

### 🗄️ Accès aux Données  
- **Spring Data JPA avec Hibernate**.  
- **Gestion des transactions** avec `@Transactional`.  
- **Base de données** :  
  ✅ **H2** en mémoire pour les tests.  
  ✅ **PostgreSQL via Docker** pour l’exécution locale.  
  ✅ **ddl-auto** activé pour gérer la création des tables en environnement de test.  

### 🔄 Mapping des Objets  
- **MapStruct** pour convertir entre **DTOs et Entités**.  
- Implémentation des mappers en tant que **Spring Components** (`@Mapper(componentModel = "spring")`).  

### ✅ Validation & Gestion des Erreurs  
- **Validation des entrées** avec **Jakarta Validation (`@Valid`, `@NotNull`, etc.)**.  
- **Centralisation des erreurs** avec `@ControllerAdvice`.  

### 🧪 Tests  
- **Unitaires** : **JUnit + Mockito**.  
- **Intégration** : H2 en mode **Spring TestContext**, avec initialisation automatique des schémas (`ddl-auto`).  
