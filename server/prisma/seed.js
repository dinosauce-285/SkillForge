const fs = require('fs');
const path = require('path');

function loadEnvFile(filePath) {
  if (!fs.existsSync(filePath)) {
    return;
  }

  const fileContent = fs.readFileSync(filePath, 'utf8');
  for (const line of fileContent.split(/\r?\n/)) {
    const trimmedLine = line.trim();
    if (!trimmedLine || trimmedLine.startsWith('#')) {
      continue;
    }

    const separatorIndex = trimmedLine.indexOf('=');
    if (separatorIndex === -1) {
      continue;
    }

    const key = trimmedLine.slice(0, separatorIndex).trim();
    if (!key || process.env[key] !== undefined) {
      continue;
    }

    let value = trimmedLine.slice(separatorIndex + 1).trim();
    if (
      (value.startsWith('"') && value.endsWith('"')) ||
      (value.startsWith("'") && value.endsWith("'"))
    ) {
      value = value.slice(1, -1);
    }

    process.env[key] = value;
  }
}

loadEnvFile(path.resolve(__dirname, '..', '.env'));

const {
  PrismaClient,
  Prisma,
  Role,
  CourseLevel,
  CourseStatus,
  MaterialType,
  MaterialStatus,
  EnrollmentStatus,
} = require('@prisma/client');
const bcrypt = require('bcrypt');

const prisma = new PrismaClient();

const DEFAULT_DOC_SIZE = 1_250_000;
const PASSWORD = 'SkillForge123!';

const courseThumbnailByTitle = {
  'HTML & CSS Foundations': 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80',
  'JavaScript Core': 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=1200&q=80',
  'React UI Systems': 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=1200&q=80',
  'Node.js API Foundations': 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80',
  'Git & Collaboration': 'https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&w=1200&q=80',
  'PostgreSQL & Prisma': 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=1200&q=80',
};

const lessonResourceByTitle = {
  'Document structure and semantic tags': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Structuring_content',
    videoUrl: 'https://www.youtube.com/watch?v=BsDoLVMnmZs',
    videoSize: 41_000_000,
  },
  'Forms, tables, and accessibility': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Structuring_content/HTML_table_basics',
    videoUrl: 'https://www.youtube.com/watch?v=8t4vTHs1eNQ',
    videoSize: 44_000_000,
  },
  'Box model and spacing': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Styling_basics/Box_model',
    videoUrl: 'https://www.youtube.com/watch?v=rIO5326FgPE',
    videoSize: 43_000_000,
  },
  'Flexbox and responsive layout': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_flexible_box_layout',
    videoUrl: 'https://www.youtube.com/watch?v=JJSoEo8JSnc',
    videoSize: 45_000_000,
  },
  'Syntax, types, and control flow': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types',
    videoUrl: 'https://www.youtube.com/watch?v=W6NZfCO5SIk',
    videoSize: 47_000_000,
  },
  'Arrays, objects, and loops': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array',
    videoUrl: 'https://www.youtube.com/watch?v=DHjqpvDnNGE',
    videoSize: 46_000_000,
  },
  'Functions and scope': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Functions',
    videoUrl: 'https://www.youtube.com/watch?v=x7X9w_GIm1s',
    videoSize: 48_000_000,
  },
  'Async/await and fetch': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function',
    videoUrl: 'https://www.youtube.com/watch?v=V_Kr9OSfDeU',
    videoSize: 50_000_000,
  },
  'Components and JSX': {
    docUrl: 'https://react.dev/learn/describing-the-ui',
    videoUrl: 'https://www.youtube.com/watch?v=Ke90Tje7VS0',
    videoSize: 52_000_000,
  },
  'Props, state, and rendering': {
    docUrl: 'https://react.dev/learn/passing-props-to-a-component',
    videoUrl: 'https://www.youtube.com/watch?v=I2UBjN5ER4s',
    videoSize: 54_000_000,
  },
  'Hooks and component lifecycle thinking': {
    docUrl: 'https://react.dev/reference/react/useEffect',
    videoUrl: 'https://www.youtube.com/watch?v=5-0wYdHpkVQ',
    videoSize: 55_000_000,
  },
  'Data fetching and reusable UI': {
    docUrl: 'https://react.dev/learn/sharing-state-between-components',
    videoUrl: 'https://www.youtube.com/watch?v=G6D9cBaLViA',
    videoSize: 56_000_000,
  },
  'Node runtime and project setup': {
    docUrl: 'https://nodejs.org/en/learn/getting-started/introduction-to-nodejs',
    videoUrl: 'https://www.youtube.com/watch?v=fBNz5xF-Kx4',
    videoSize: 58_000_000,
  },
  'Express routing and middleware': {
    docUrl: 'https://expressjs.com/en/guide/routing.html',
    videoUrl: 'https://www.youtube.com/watch?v=L72fhGm1tfE',
    videoSize: 59_000_000,
  },
  'Validation and error handling': {
    docUrl: 'https://expressjs.com/en/guide/error-handling.html',
    videoUrl: 'https://www.youtube.com/watch?v=7H_QH9nipNs',
    videoSize: 57_000_000,
  },
  'Authentication and API hardening': {
    docUrl: 'https://developer.mozilla.org/en-US/docs/Web/Security/Authentication',
    videoUrl: 'https://www.youtube.com/watch?v=2jqok-WgelI',
    videoSize: 60_000_000,
  },
  'Git init, commit, and history': {
    docUrl: 'https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control',
    videoUrl: 'https://www.youtube.com/watch?v=RGOj5yH7evk',
    videoSize: 42_000_000,
  },
  'Branching and merge strategies': {
    docUrl: 'https://git-scm.com/book/en/v2/Git-Branching-Branches-in-a-Nutshell',
    videoUrl: 'https://www.youtube.com/watch?v=Q1I86UYW5bg',
    videoSize: 43_000_000,
  },
  'Pull requests and code review': {
    docUrl: 'https://docs.github.com/en/pull-requests',
    videoUrl: 'https://www.youtube.com/watch?v=dCzjp95Q1Yk',
    videoSize: 44_000_000,
  },
  'Conflict resolution and release flow': {
    docUrl: 'https://docs.github.com/en/get-started/using-git/resolving-merge-conflicts-after-a-git-rebase',
    videoUrl: 'https://www.youtube.com/watch?v=Fs4zA1X00tk',
    videoSize: 45_000_000,
  },
  'Relational modeling and primary keys': {
    docUrl: 'https://www.postgresql.org/docs/current/tutorial-schema.html',
    videoUrl: 'https://www.youtube.com/watch?v=qw--VYLpxG4',
    videoSize: 53_000_000,
  },
  'Foreign keys and indexing': {
    docUrl: 'https://www.postgresql.org/docs/current/indexes.html',
    videoUrl: 'https://www.youtube.com/watch?v=G8hWQmBnkfM',
    videoSize: 54_000_000,
  },
  'Prisma schema design': {
    docUrl: 'https://www.prisma.io/docs/orm/prisma-schema',
    videoUrl: 'https://www.youtube.com/watch?v=RebA5J-rlwg',
    videoSize: 55_000_000,
  },
  'Querying and migrations': {
    docUrl: 'https://www.prisma.io/docs/orm/prisma-client/queries',
    videoUrl: 'https://www.youtube.com/watch?v=eu0O7TN9J4Y',
    videoSize: 56_000_000,
  },
};

const courseBlueprints = [
  {
    title: 'HTML & CSS Foundations',
    subtitle: 'Build accessible pages with semantic HTML and modern CSS.',
    summary:
      'Learn how to structure content correctly, then style it with layout systems that scale from mobile to desktop.',
    categoryName: 'Web Development',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 0,
    isFree: true,
    thumbnailUrl: null,
    promoVideoUrl: 'https://www.youtube.com/watch?v=UB1O30fR-EE',
    videoSize: 49_000_000,
    tagNames: ['HTML', 'CSS', 'Responsive Design'],
    chapters: [
      {
        title: 'HTML Basics',
        docUrl: 'https://developer.mozilla.org/en-US/docs/Web/HTML',
        lessons: [
          { title: 'Document structure and semantic tags' },
          { title: 'Forms, tables, and accessibility' },
        ],
      },
      {
        title: 'Modern CSS',
        docUrl: 'https://developer.mozilla.org/en-US/docs/Web/CSS',
        lessons: [
          { title: 'Box model and spacing' },
          { title: 'Flexbox and responsive layout' },
        ],
      },
    ],
  },
  {
    title: 'JavaScript Core',
    subtitle: 'Write dependable browser and runtime code with modern JavaScript.',
    summary:
      'Cover language fundamentals, object patterns, and asynchronous behavior that every frontend or backend developer needs.',
    categoryName: 'JavaScript',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 99000,
    isFree: false,
    thumbnailUrl: null,
    promoVideoUrl: 'https://www.youtube.com/watch?v=hdI2bqOjy3c',
    videoSize: 57_000_000,
    tagNames: ['JavaScript', 'Async', 'ES6'],
    chapters: [
      {
        title: 'Language Essentials',
        docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript',
        lessons: [
          { title: 'Syntax, types, and control flow' },
          { title: 'Arrays, objects, and loops' },
        ],
      },
      {
        title: 'Working with the Browser',
        docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide',
        lessons: [
          { title: 'Functions and scope' },
          { title: 'Async/await and fetch' },
        ],
      },
    ],
  },
  {
    title: 'React UI Systems',
    subtitle: 'Compose predictable user interfaces with reusable React patterns.',
    summary:
      'Move from JSX basics to data flow, hooks, and reusable UI composition for real product screens.',
    categoryName: 'Frontend Engineering',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 149000,
    isFree: false,
    thumbnailUrl: null,
    promoVideoUrl: 'https://www.youtube.com/watch?v=w7ejDZ8SWv8',
    videoSize: 61_000_000,
    tagNames: ['React', 'UI', 'Hooks'],
    chapters: [
      {
        title: 'Components and State',
        docUrl: 'https://react.dev/learn',
        lessons: [
          { title: 'Components and JSX' },
          { title: 'Props, state, and rendering' },
        ],
      },
      {
        title: 'Data and Reusability',
        docUrl: 'https://react.dev/reference/react',
        lessons: [
          { title: 'Hooks and component lifecycle thinking' },
          { title: 'Data fetching and reusable UI' },
        ],
      },
    ],
  },
  {
    title: 'Node.js API Foundations',
    subtitle: 'Build reliable REST APIs with Node.js, Express, and clean service logic.',
    summary:
      'Learn how to wire request handling, validation, error handling, and authentication into a maintainable backend.',
    categoryName: 'Backend Engineering',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 179000,
    isFree: false,
    thumbnailUrl: null,
    promoVideoUrl: 'https://www.youtube.com/watch?v=fBNz5xF-Kx4',
    videoSize: 64_000_000,
    tagNames: ['Node.js', 'Express', 'API'],
    chapters: [
      {
        title: 'Runtime and Routing',
        docUrl: 'https://nodejs.org/en/learn/getting-started/introduction-to-nodejs',
        lessons: [
          { title: 'Node runtime and project setup' },
          { title: 'Express routing and middleware' },
        ],
      },
      {
        title: 'Production API Patterns',
        docUrl: 'https://expressjs.com/en/guide/routing.html',
        lessons: [
          { title: 'Validation and error handling' },
          { title: 'Authentication and API hardening' },
        ],
      },
    ],
  },
  {
    title: 'Git & Collaboration',
    subtitle: 'Use Git and GitHub safely in a team workflow.',
    summary:
      'Practice branching, review flow, conflict resolution, and release habits that keep teams moving without chaos.',
    categoryName: 'Developer Tools',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 0,
    isFree: true,
    thumbnailUrl: null,
    promoVideoUrl: 'https://www.youtube.com/watch?v=RGOj5yH7evk',
    videoSize: 46_000_000,
    tagNames: ['Git', 'GitHub', 'Collaboration'],
    chapters: [
      {
        title: 'Version Control Basics',
        docUrl: 'https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control',
        lessons: [
          { title: 'Git init, commit, and history' },
          { title: 'Branching and merge strategies' },
        ],
      },
      {
        title: 'Team Workflow',
        docUrl: 'https://docs.github.com/en/get-started',
        lessons: [
          { title: 'Pull requests and code review' },
          { title: 'Conflict resolution and release flow' },
        ],
      },
    ],
  },
  {
    title: 'PostgreSQL & Prisma',
    subtitle: 'Model data correctly and ship with safe database migrations.',
    summary:
      'Learn relational design, indexing, and Prisma workflows that turn a schema into a production-ready database layer.',
    categoryName: 'Databases',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 129000,
    isFree: false,
    thumbnailUrl: null,
    promoVideoUrl: 'https://www.youtube.com/watch?v=qw--VYLpxG4',
    videoSize: 58_000_000,
    tagNames: ['PostgreSQL', 'Prisma', 'SQL'],
    chapters: [
      {
        title: 'Relational Design',
        docUrl: 'https://www.postgresql.org/docs/current/tutorial.html',
        lessons: [
          { title: 'Relational modeling and primary keys' },
          { title: 'Foreign keys and indexing' },
        ],
      },
      {
        title: 'Prisma Workflow',
        docUrl: 'https://www.prisma.io/docs/getting-started',
        lessons: [
          { title: 'Prisma schema design' },
          { title: 'Querying and migrations' },
        ],
      },
    ],
  },
];

const baseUsers = [
  {
    email: 'admin@skillforge.dev',
    fullName: 'SkillForge Admin',
    role: Role.ADMIN,
    skills: ['Operations', 'Content Review'],
    learningGoals: null,
  },
  {
    email: 'khoa@skillforge.dev',
    fullName: 'Nguyen Minh Khoa',
    role: Role.INSTRUCTOR,
    skills: ['HTML', 'CSS', 'React', 'Git'],
    learningGoals: null,
  },
  {
    email: 'han@skillforge.dev',
    fullName: 'Tran Gia Han',
    role: Role.INSTRUCTOR,
    skills: ['JavaScript', 'Node.js', 'PostgreSQL'],
    learningGoals: null,
  },
  {
    email: 'nam@skillforge.dev',
    fullName: 'Le Hoang Nam',
    role: Role.STUDENT,
    skills: ['Frontend', 'Backend'],
    learningGoals: 'Become a full-stack developer.',
  },
  {
    email: 'anh@skillforge.dev',
    fullName: 'Ngo Minh Anh',
    role: Role.STUDENT,
    skills: ['UI', 'JavaScript'],
    learningGoals: 'Ship polished web apps with React.',
  },
  {
    email: 'tung@skillforge.dev',
    fullName: 'Vu Thanh Tung',
    role: Role.STUDENT,
    skills: ['Databases', 'DevOps'],
    learningGoals: 'Improve backend and deployment skills.',
  },
];

function buildLessonMaterials(docUrl, videoUrl, videoSize) {
  return {
    create: [
      {
        type: MaterialType.DOCUMENT,
        fileUrl: docUrl,
        fileSize: DEFAULT_DOC_SIZE,
        status: MaterialStatus.READY,
      },
      {
        type: MaterialType.VIDEO,
        fileUrl: videoUrl,
        fileSize: videoSize,
        status: MaterialStatus.READY,
      },
    ],
  };
}

function flattenLessons(course) {
  return course.chapters.flatMap((chapter) => chapter.lessons);
}

async function clearSeedData() {
  const deleteOrder = [
    prisma.studentAnswer,
    prisma.quizAttempt,
    prisma.answerChoice,
    prisma.question,
    prisma.quiz,
    prisma.discussion,
    prisma.lessonProgress,
    prisma.lessonMaterial,
    prisma.review,
    prisma.favorite,
    prisma.enrollment,
    prisma.certificate,
    prisma.transaction,
    prisma.order,
    prisma.courseStatistic,
    prisma.courseProgress,
    prisma.lesson,
    prisma.chapter,
    prisma.course,
  ];

  for (const model of deleteOrder) {
    await model.deleteMany();
  }
}

async function upsertUsers(passwordHash) {
  const usersByEmail = new Map();

  for (const user of baseUsers) {
    const record = await prisma.user.upsert({
      where: { email: user.email },
      update: {
        fullName: user.fullName,
        role: user.role,
        provider: 'LOCAL',
        isActive: true,
        password: passwordHash,
      },
      create: {
        email: user.email,
        fullName: user.fullName,
        role: user.role,
        provider: 'LOCAL',
        isActive: true,
        password: passwordHash,
      },
    });

    await prisma.userProfile.upsert({
      where: { userId: record.id },
      update: {
        skills: user.skills,
        learningGoals: user.learningGoals,
      },
      create: {
        userId: record.id,
        skills: user.skills,
        learningGoals: user.learningGoals,
      },
    });

    usersByEmail.set(user.email, record);
  }

  return usersByEmail;
}

async function upsertLookupRecords() {
  const categories = [
    'Web Development',
    'JavaScript',
    'Frontend Engineering',
    'Backend Engineering',
    'Developer Tools',
    'Databases',
  ];

  const tags = [
    'HTML',
    'CSS',
    'Responsive Design',
    'JavaScript',
    'Async',
    'ES6',
    'React',
    'UI',
    'Hooks',
    'Node.js',
    'Express',
    'API',
    'Git',
    'GitHub',
    'Collaboration',
    'PostgreSQL',
    'Prisma',
    'SQL',
  ];

  const categoryRecords = new Map();
  const tagRecords = new Map();

  for (const name of categories) {
    const category = await prisma.category.upsert({
      where: { name },
      update: {},
      create: { name },
    });
    categoryRecords.set(name, category);
  }

  for (const name of tags) {
    const tag = await prisma.tag.upsert({
      where: { name },
      update: {},
      create: { name },
    });
    tagRecords.set(name, tag);
  }

  return { categoryRecords, tagRecords };
}

async function createCourses(categoryRecords, tagRecords, usersByEmail) {
  const createdCourses = [];

  for (const blueprint of courseBlueprints) {
    const category = categoryRecords.get(blueprint.categoryName);
    const instructor = usersByEmail.get(blueprint.instructorEmail);

    const course = await prisma.course.create({
      data: {
        instructorId: instructor.id,
        categoryId: category.id,
        title: blueprint.title,
        subtitle: blueprint.subtitle,
        summary: blueprint.summary,
        thumbnailUrl: courseThumbnailByTitle[blueprint.title] ?? blueprint.thumbnailUrl,
        promoVideoUrl: blueprint.promoVideoUrl,
        price: new Prisma.Decimal(String(blueprint.price)),
        isFree: blueprint.isFree,
        level: blueprint.level,
        status: CourseStatus.PUBLISHED,
        tags: {
          connect: blueprint.tagNames.map((name) => ({ id: tagRecords.get(name).id })),
        },
        chapters: {
          create: blueprint.chapters.map((chapter, chapterIndex) => ({
            title: chapter.title,
            orderIndex: chapterIndex,
            lessons: {
              create: chapter.lessons.map((lesson, lessonIndex) => ({
                title: lesson.title,
                orderIndex: lessonIndex,
                materials: buildLessonMaterials(
                  lessonResourceByTitle[lesson.title]?.docUrl ?? chapter.docUrl,
                  lessonResourceByTitle[lesson.title]?.videoUrl ?? blueprint.promoVideoUrl,
                  lessonResourceByTitle[lesson.title]?.videoSize ?? blueprint.videoSize,
                ),
              })),
            },
          })),
        },
      },
      include: {
        chapters: {
          orderBy: { orderIndex: 'asc' },
          include: {
            lessons: {
              orderBy: { orderIndex: 'asc' },
              include: { materials: true },
            },
          },
        },
        tags: true,
        category: true,
        instructor: {
          select: {
            id: true,
            fullName: true,
          },
        },
      },
    });

    createdCourses.push(course);
  }

  return createdCourses;
}

async function seedEnrollmentAndProgress(createdCourses, usersByEmail) {
  const student1 = usersByEmail.get('nam@skillforge.dev');
  const student2 = usersByEmail.get('anh@skillforge.dev');
  const student3 = usersByEmail.get('tung@skillforge.dev');

  const progressMatrix = [
    {
      user: student1,
      completions: [4, 3, 3, 2, 2, 1],
    },
    {
      user: student2,
      completions: [2, 2, 1, 1, 0, 0],
    },
  ];

  const thirdStudentFavorites = [0, 5];

  for (const [courseIndex, course] of createdCourses.entries()) {
    const lessons = flattenLessons(course);
    const enrollmentRecords = [];

    for (const plan of progressMatrix) {
      const completedCount = plan.completions[courseIndex] ?? 0;
      if (completedCount === 0) {
        continue;
      }

      const enrollment = await prisma.enrollment.create({
        data: {
          userId: plan.user.id,
          courseId: course.id,
          status: EnrollmentStatus.ACTIVE,
          progress: lessons.length === 0 ? 0 : Math.round((completedCount / lessons.length) * 100),
        },
      });

      enrollmentRecords.push(enrollment);

      for (
        let lessonIndex = 0;
        lessonIndex < completedCount && lessonIndex < lessons.length;
        lessonIndex += 1
      ) {
        const lesson = lessons[lessonIndex];
        await prisma.lessonProgress.create({
          data: {
            userId: plan.user.id,
            lessonId: lesson.id,
            isCompleted: true,
            lastWatchedPosition: 540 + lessonIndex * 45,
          },
        });
      }

      await prisma.courseProgress.create({
        data: {
          userId: plan.user.id,
          courseId: course.id,
          progress: lessons.length === 0 ? 0 : completedCount / lessons.length,
          isCompleted: completedCount === lessons.length,
        },
      });
    }

    if (courseIndex < 3) {
      await prisma.enrollment.upsert({
        where: {
          userId_courseId: {
            userId: student3.id,
            courseId: course.id,
          },
        },
        update: {
          status: EnrollmentStatus.ACTIVE,
          progress: 0,
        },
        create: {
          userId: student3.id,
          courseId: course.id,
          status: EnrollmentStatus.ACTIVE,
          progress: 0,
        },
      });
    }

    if (thirdStudentFavorites.includes(courseIndex)) {
      await prisma.favorite.upsert({
        where: {
          userId_courseId: {
            userId: student3.id,
            courseId: course.id,
          },
        },
        update: {},
        create: {
          userId: student3.id,
          courseId: course.id,
        },
      });
    }

    if (enrollmentRecords.length > 0) {
      const progressAverage =
        enrollmentRecords.reduce((sum, record) => sum + record.progress, 0) /
        enrollmentRecords.length;

      await prisma.courseStatistic.create({
        data: {
          courseId: course.id,
          date: new Date(),
          revenue: new Prisma.Decimal(String(Number(course.price) * enrollmentRecords.length)),
          enrollmentCount: enrollmentRecords.length,
          avgCompletionRate: progressAverage / 100,
        },
      });
    }

    await prisma.review.upsert({
      where: {
        studentId_courseId: {
          studentId: student1.id,
          courseId: course.id,
        },
      },
      update: {
        rating: courseIndex === 0 ? 5 : 4,
        content: `${course.title} is well structured and practical for the current stack.`,
      },
      create: {
        studentId: student1.id,
        courseId: course.id,
        rating: courseIndex === 0 ? 5 : 4,
        content: `${course.title} is well structured and practical for the current stack.`,
      },
    });

    if (courseIndex < 2) {
      await prisma.review.upsert({
        where: {
          studentId_courseId: {
            studentId: student2.id,
            courseId: course.id,
          },
        },
        update: {
          rating: 5,
          content: `Clear explanations and enough depth to keep moving quickly through ${course.title}.`,
        },
        create: {
          studentId: student2.id,
          courseId: course.id,
          rating: 5,
          content: `Clear explanations and enough depth to keep moving quickly through ${course.title}.`,
        },
      });
    }

    await prisma.favorite.upsert({
      where: {
        userId_courseId: {
          userId: student1.id,
          courseId: course.id,
        },
      },
      update: {},
      create: {
        userId: student1.id,
        courseId: course.id,
      },
    });
  }
}

async function main() {
  const passwordHash = await bcrypt.hash(PASSWORD, 10);

  await clearSeedData();

  const { categoryRecords, tagRecords } = await upsertLookupRecords();
  const usersByEmail = await upsertUsers(passwordHash);
  const createdCourses = await createCourses(categoryRecords, tagRecords, usersByEmail);

  await seedEnrollmentAndProgress(createdCourses, usersByEmail);

  console.log(`Seeded ${createdCourses.length} courses with chapters, lessons, and materials.`);
  console.log(`Default login password for seeded accounts: ${PASSWORD}`);
}

main()
  .catch((error) => {
    console.error('Seed failed:', error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await prisma.$disconnect();
  });