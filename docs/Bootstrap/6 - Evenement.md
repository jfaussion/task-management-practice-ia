## 📨 Événements & Asynchronisme  

### 🔄 Gestion des Flux  
- **Toutes les opérations restent synchrones** → Pas de traitement en arrière-plan.  
- **Gestion des transactions** via **Spring `@Transactional`** pour garantir la cohérence des opérations.  

### 🚀 Asynchronisme  
- **Pas d’asynchronisme (`@Async`)** → Toutes les actions sont exécutées immédiatement.  
- **Pas de bus d’événements internes (Spring Events)** → Les services interagissent directement.  
- **Pas de système de files de messages (RabbitMQ, Kafka)** → Toutes les opérations sont traitées de manière immédiate et directe.  
