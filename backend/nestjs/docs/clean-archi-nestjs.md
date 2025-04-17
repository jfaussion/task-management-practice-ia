
## Structure appli NestJS

```perl
src/
├── domain/
│   └── model/
│       ├── task.model.ts
│       └── user.model.ts
│
├── application/
│   ├── service/
│   │   ├── task.service.ts               # Interface
│   │   └── impl/
│   │       └── task.service.impl.ts      # Implémentation métier
│   └── dao/
│       └── task.dao.ts                   # Interface DAO
│
├── infrastructure/
│   ├── api/
│   │   └── task.controller.ts
│   └── data/
│       ├── dao/
│       │   └── task.dao.impl.ts          # Implémentation DAO
│       └── repository/
│           └── prisma-task.repository.ts # Prisma adapter
│
├── prisma/
│   └── schema.prisma
│
├── config/
│   └── prisma.module.ts
│
├── main.ts
└── app.module.ts

```