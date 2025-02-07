## 🎨 Conception du Front-End  

### 🏗️ Architecture  
- **Framework : Angular**.  
- **Structure : Un seul module principal (`AppModule`)** pour éviter la complexité.  
- **Consommation d’API REST** avec `HttpClientModule`.  

### 🔄 Gestion des Requêtes API  
- **Utilisation d’`HttpClientModule`** natif pour les appels à l’API.  
- **Ajout d’un `HttpInterceptor`** pour gérer le **loader et les erreurs globales**.  

### 🔧 Gestion de l’État  
- **Utilisation de `RxJS` (`BehaviorSubject`, `Observable`)** pour la gestion réactive de l’état.  
- **Pas d’implémentation Redux (NgRx, Akita)** pour éviter la complexité.  

### 🎨 Styling & UI  
- **Framework CSS : TailwindCSS**.  
- **Approche minimaliste et responsive** pour l’interface utilisateur.  

### 🧪 Tests  
- **Tests unitaires (`Jasmine/Karma`) uniquement sur les composants critiques**.  
- **Pas de tests end-to-end (Cypress, Playwright)**.  
