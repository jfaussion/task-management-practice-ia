import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  // Create sample users
  const user1 = await prisma.user.upsert({
    where: { username: 'john_doe' },
    update: {},
    create: {
      username: 'john_doe',
      email: 'john@example.com',
      role: 'USER',
    },
  });

  const user2 = await prisma.user.upsert({
    where: { username: 'jane_smith' },
    update: {},
    create: {
      username: 'jane_smith',
      email: 'jane@example.com',
      role: 'ADMIN',
    },
  });

  // Create sample tasks
  const task1 = await prisma.task.upsert({
    where: {
      title_assigneeId: {
        title: 'Complete project documentation',
        assigneeId: user1.id,
      },
    },
    update: {},
    create: {
      title: 'Complete project documentation',
      description: 'Write comprehensive documentation for the project',
      status: 'IN_PROGRESS',
      priority: 'HIGH',
      dueDate: new Date('2024-04-01'),
      assigneeId: user1.id,
    },
  });

  const task2 = await prisma.task.upsert({
    where: {
      title_assigneeId: {
        title: 'Review pull requests',
        assigneeId: user2.id,
      },
    },
    update: {},
    create: {
      title: 'Review pull requests',
      description: 'Review and merge pending pull requests',
      status: 'TODO',
      priority: 'MEDIUM',
      dueDate: new Date('2024-03-25'),
      assigneeId: user2.id,
    },
  });

  const task3 = await prisma.task.upsert({
    where: {
      title_assigneeId: {
        title: 'Bug fix: Login page',
        assigneeId: user1.id,
      },
    },
    update: {},
    create: {
      title: 'Bug fix: Login page',
      description: 'Fix the login page validation issue',
      status: 'DONE',
      priority: 'HIGH',
      dueDate: new Date('2024-03-20'),
      assigneeId: user1.id,
    },
  });

  console.log({ user1, user2, task1, task2, task3 });
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  }); 