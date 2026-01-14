# 📌 Task Management Practice IA

## 🚀 Description
Task Management Practice IA est une application de gestion de tâches développée avec **Java Spring Boot (backend)** et **Angular (frontend)**. Ce projet est conçu pour démontrer l'utilisation de l'IA dans le développement logiciel, notamment pour la **génération de tests, la documentation et la correction de bugs**.

## 🏗️ Architecture du projet
Le projet suit une **architecture en monorepo** et est organisé comme suit :

```
/task-management-practice-ia
│── backend/          # API Spring Boot 3.x
│── frontend/         # Application Angular 19
│── docs/             # Documentation du projet
│── scripts/          # Scripts d'automatisation
│── .github/          # Workflows CI/CD
│── README.md         # Documentation principale
```

## ⚙️ Technologies utilisées
### **Backend : Java Spring Boot 3.x**
- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Liquibase

### **Frontend : Angular 19**
- Node.js 20
- Angular CLI
- Tailwind CSS 4
- RxJS

### **Outils et automatisation**
- GitHub Actions (CI/CD)
- Docker
- Postgres (via Docker)

## 🔧 Installation & Setup
### 1️⃣ Prérequis
Avant de commencer, assurez-vous d'avoir installé :
- **JDK 21** ([Télécharger ici](https://adoptium.net/))
- **Maven 3.9+** ([Télécharger ici](https://maven.apache.org/download.cgi))
- **Node.js 20+ et npm 10+** ([Télécharger ici](https://nodejs.org/))
- **Docker** (optionnel pour PostgreSQL)

### 2️⃣ Cloner le repository
```bash
git clone https://github.com/<votre-username>/task-management-practice-ia.git
cd task-management-practice-ia
```

### 3️⃣ Lancer la base de données (optionnel)
Si vous utilisez **PostgreSQL via Docker**, exécutez :
```bash
./scripts/setup-db.sh
```

### 4️⃣ Démarrer l'application
Lancer le backend et le frontend en parallèle :
```bash
./scripts/run.sh
```
Ou manuellement :
```bash
# Lancer le backend
cd backend/springboot && mvn spring-boot:run

# Lancer le frontend
cd frontend && ng serve
```

## 🛠️ Commandes utiles
| Commande | Description |
|----------|------------|
| `./scripts/run.sh` | Démarre le backend et le frontend |
| `./scripts/setup-db.sh` | Initialise la base PostgreSQL via Docker |
| `./scripts/build.sh` | Build complet du projet |
| `./scripts/test.sh` | Exécute les tests du backend et du frontend |

## ✅ Fonctionnalités principales
- **CRUD complet** pour la gestion des tâches
- **Assignation des tâches** avec limitation par utilisateur
- **Gestion transactionnelle** pour éviter les incohérences
- **Tests unitaires générés par l'IA** (JUnit, Karma)
- **Documentation automatisée** avec JavaDoc et Mermaid.js
- **Déploiement facilité** grâce à GitHub Actions

## 📝 Bonnes pratiques avec l'IA
- **Génération automatique de tests unitaires** pour garantir la fiabilité du code.
- **Utilisation de l'IA pour la documentation** (JavaDoc, descriptions de classes).
- **Détection et correction de bugs** assistée par IA.

## 📐 Architecture et Documentation technique

| Section | Description |
|---------|------------|
| [Database Design](docs/Architecture/db-design.md) | Conception et structure de la base de données |
| [API Documentation](docs/Architecture/url-api-structure.md) | Documentation de l'API |

## 📜 Licence
Ce projet est sous licence **MIT**.

## 📩 Contribution
Les contributions sont les bienvenues ! Pour proposer une modification :
1. **Fork** le repo
2. **Crée une branche** (`git checkout -b feature/ma-fonctionnalite`)
3. **Commit tes changements** (`git commit -m "Ajout d'une nouvelle fonctionnalité"`)
4. **Push ta branche** (`git push origin feature/ma-fonctionnalite`)
5. **Ouvre une Pull Request**

---
Développé avec ❤️ par la communauté Task Management Practice IA 🚀

