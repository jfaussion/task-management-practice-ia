import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  // Create users from SQL file
  const alice = await prisma.user.upsert({
    where: { username: 'alice' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440000',
      username: 'alice',
      email: 'alice@example.com',
      role: 'ADMIN',
      createdAt: new Date('2025-03-21T18:55:58.79013'),
    },
  });

  const bob = await prisma.user.upsert({
    where: { username: 'bob' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440001',
      username: 'bob',
      email: 'bob@example.com',
      role: 'USER',
      createdAt: new Date('2025-03-21T18:55:58.79013'),
    },
  });

  const charlie = await prisma.user.upsert({
    where: { username: 'charlie' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440002',
      username: 'charlie',
      email: 'charlie@example.com',
      role: 'USER',
      createdAt: new Date('2025-03-21T18:55:58.79013'),
    },
  });

  const david = await prisma.user.upsert({
    where: { username: 'david' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440003',
      username: 'david',
      email: 'david@example.com',
      role: 'USER',
      createdAt: new Date('2025-03-21T18:55:58.79013'),
    },
  });

  const eve = await prisma.user.upsert({
    where: { username: 'eve' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440004',
      username: 'eve',
      email: 'eve@example.com',
      role: 'ADMIN',
      createdAt: new Date('2025-03-21T18:55:58.79013'),
    },
  });

  // Create tasks from SQL file
  const task1 = await prisma.task.upsert({
    where: { id: '550e8400-e29b-41d4-a716-446655440010' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440010',
      title: 'Task 1',
      description: 'Description for task 1',
      status: 'TODO',
      priority: 'HIGH',
      dueDate: new Date('2023-12-01'),
      createdAt: new Date('2025-03-21T18:55:58.870344'),
      updatedAt: new Date('2025-03-21T18:55:58.870344'),
      assigneeId: alice.id,
    },
  });

  const task2 = await prisma.task.upsert({
    where: { id: '550e8400-e29b-41d4-a716-446655440011' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440011',
      title: 'Task 2',
      description: 'Description for task 2',
      status: 'IN_PROGRESS',
      priority: 'MEDIUM',
      dueDate: new Date('2023-12-05'),
      createdAt: new Date('2025-03-21T18:55:58.870344'),
      updatedAt: new Date('2025-03-21T18:55:58.870344'),
      assigneeId: bob.id,
    },
  });

  const task3 = await prisma.task.upsert({
    where: { id: '550e8400-e29b-41d4-a716-446655440012' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440012',
      title: 'Task 3',
      description: 'Description for task 3',
      status: 'DONE',
      priority: 'LOW',
      dueDate: new Date('2023-12-10'),
      createdAt: new Date('2025-03-21T18:55:58.870344'),
      updatedAt: new Date('2025-03-21T18:55:58.870344'),
      assigneeId: charlie.id,
    },
  });

  const task4 = await prisma.task.upsert({
    where: { id: '550e8400-e29b-41d4-a716-446655440013' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440013',
      title: 'Task 4',
      description: 'Description for task 4',
      status: 'TODO',
      priority: 'MEDIUM',
      dueDate: new Date('2023-12-15'),
      createdAt: new Date('2025-03-21T18:55:58.870344'),
      updatedAt: new Date('2025-03-21T18:55:58.870344'),
      assigneeId: david.id,
    },
  });

  const task5 = await prisma.task.upsert({
    where: { id: '550e8400-e29b-41d4-a716-446655440014' },
    update: {},
    create: {
      id: '550e8400-e29b-41d4-a716-446655440014',
      title: 'Task 5',
      description: 'Description for task 5',
      status: 'IN_PROGRESS',
      priority: 'HIGH',
      dueDate: new Date('2023-12-20'),
      createdAt: new Date('2025-03-21T18:55:58.870344'),
      updatedAt: new Date('2025-03-21T18:55:58.870344'),
      assigneeId: eve.id,
    },
  });

  console.log({ alice, bob, charlie, david, eve, task1, task2, task3, task4, task5 });
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  }); 