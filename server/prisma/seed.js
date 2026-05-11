const fs = require('fs');
const path = require('path');
const bcrypt = require('bcrypt');
const {
  PrismaClient,
  Prisma,
  ActionType,
  AttemptStatus,
  CourseLevel,
  CourseStatus,
  CouponScope,
  EnrollmentStatus,
  InstructorSubscriptionPaymentStatus,
  InstructorSubscriptionStatus,
  MaterialStatus,
  MaterialType,
  NotificationType,
  OrderStatus,
  PaymentGateway,
  Provider,
  ReportStatus,
  ReportType,
  Role,
  WithdrawalStatus,
} = require('@prisma/client');

function loadEnvFile(filePath) {
  if (!fs.existsSync(filePath)) return;

  const fileContent = fs.readFileSync(filePath, 'utf8');
  for (const line of fileContent.split(/\r?\n/)) {
    const trimmedLine = line.trim();
    if (!trimmedLine || trimmedLine.startsWith('#')) continue;

    const separatorIndex = trimmedLine.indexOf('=');
    if (separatorIndex === -1) continue;

    const key = trimmedLine.slice(0, separatorIndex).trim();
    if (!key || process.env[key] !== undefined) continue;

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

const prisma = new PrismaClient();
const PASSWORD = '123456';
const PLATFORM_SHARE_RATE = 30;
const INSTRUCTOR_SHARE_RATE = 70;
const PENDING_RELEASE_DAYS = 30;

const videos = [
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/videos/11274341-uhd-3840-2160-25fps.mp4',
    size: 5013657,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/videos/3982250-uhd-3840-2160-30fps.mp4',
    size: 4596166,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/videos/4298113-uhd-3840-2160-25fps.mp4',
    size: 3252525,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/videos/4494856-uhd-3840-2160-25fps.mp4',
    size: 1589070,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/videos/4974769-hd-1920-1080-25fps.mp4',
    size: 2950550,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/videos/6672610-uhd-3840-2160-24fps.mp4',
    size: 1597603,
  },
];

const docs = [
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/mock-pdfs/movers-2.pdf',
    size: 6307561,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/mock-pdfs/starters-1-sb.pdf',
    size: 15628899,
  },
  {
    url: 'https://awenevlehjlpiyfxlpky.supabase.co/storage/v1/object/public/materials/mock-pdfs/starters-2-sb.pdf',
    size: 30212281,
  },
];

const users = [
  {
    email: 'admin@skillforge.dev',
    fullName: 'SkillForge Admin',
    role: Role.ADMIN,
    skills: ['Operations', 'Moderation', 'Finance'],
    learningGoals: null,
  },
  {
    email: 'khoa@skillforge.dev',
    fullName: 'Nguyen Minh Khoa',
    role: Role.INSTRUCTOR,
    skills: ['HTML', 'CSS', 'React', 'Machine Learning', 'Figma'],
    learningGoals: null,
  },
  {
    email: 'han@skillforge.dev',
    fullName: 'Tran Gia Han',
    role: Role.INSTRUCTOR,
    skills: ['JavaScript', 'Node.js', 'PostgreSQL', 'Python', 'Docker'],
    learningGoals: null,
  },
  {
    email: 'nam@skillforge.dev',
    fullName: 'Le Hoang Nam',
    role: Role.STUDENT,
    skills: ['Frontend', 'JavaScript'],
    learningGoals: 'Become a full-stack developer.',
  },
  {
    email: 'anh@skillforge.dev',
    fullName: 'Ngo Minh Anh',
    role: Role.STUDENT,
    skills: ['UI', 'React'],
    learningGoals: 'Ship polished web apps with React.',
  },
  {
    email: 'tung@skillforge.dev',
    fullName: 'Vu Thanh Tung',
    role: Role.STUDENT,
    skills: ['Databases', 'DevOps'],
    learningGoals: 'Improve backend and deployment skills.',
  },
  {
    email: 'linh@skillforge.dev',
    fullName: 'Pham Thanh Linh',
    role: Role.STUDENT,
    skills: ['Python', 'Data Analysis'],
    learningGoals: 'Move into a data science role.',
  },
];

const courseBlueprints = [
  {
    title: 'HTML & CSS Foundations',
    subtitle: 'Build accessible pages with semantic HTML and modern CSS.',
    summary:
      'Learn how to structure content correctly, then style it with layout systems that scale from mobile to desktop.',
    category: 'Web Development',
    instructor: 'khoa@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 0,
    isFree: true,
    tags: ['HTML', 'CSS', 'Responsive Design'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'JavaScript Core',
    subtitle: 'Write dependable browser and runtime code with modern JavaScript.',
    summary:
      'Cover language fundamentals, object patterns, and asynchronous behavior that every developer needs.',
    category: 'JavaScript',
    instructor: 'han@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 19.99,
    isFree: false,
    tags: ['JavaScript', 'Async', 'ES6'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'React UI Systems',
    subtitle: 'Compose predictable user interfaces with reusable React patterns.',
    summary:
      'Move from JSX basics to data flow, hooks, and reusable UI composition for real product screens.',
    category: 'Frontend Engineering',
    instructor: 'khoa@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 29.99,
    isFree: false,
    tags: ['React', 'UI', 'Hooks'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'Node.js API Foundations',
    subtitle: 'Build reliable REST APIs with Node.js and clean service logic.',
    summary:
      'Wire request handling, validation, error handling, authentication, and testing into a maintainable backend.',
    category: 'Backend Engineering',
    instructor: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 34.99,
    isFree: false,
    tags: ['Node.js', 'Express', 'API'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'PostgreSQL & Prisma',
    subtitle: 'Model data correctly and ship with safe database migrations.',
    summary:
      'Learn relational design, indexing, and Prisma workflows that turn a schema into a production-ready database layer.',
    category: 'Databases',
    instructor: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 24.99,
    isFree: false,
    tags: ['PostgreSQL', 'Prisma', 'SQL'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'Python for Data Science',
    subtitle: 'Analyze and visualize real-world data with Python.',
    summary:
      'From NumPy arrays to Pandas DataFrames and Matplotlib charts, build the foundation for machine learning projects.',
    category: 'Data Science',
    instructor: 'han@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 39.99,
    isFree: false,
    tags: ['Python', 'Data Science', 'NumPy'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'Machine Learning with scikit-learn',
    subtitle: 'Build and evaluate ML models end-to-end.',
    summary:
      'Implement regression, classification, clustering, and evaluation pipelines using Python and scikit-learn.',
    category: 'AI & Machine Learning',
    instructor: 'khoa@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 49.99,
    isFree: false,
    tags: ['Python', 'Machine Learning', 'scikit-learn'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'Docker & Kubernetes Essentials',
    subtitle: 'Containerize applications and orchestrate them at scale.',
    summary:
      'Go from writing a Dockerfile to deploying multi-service apps on Kubernetes with health checks and rolling updates.',
    category: 'DevOps & Cloud',
    instructor: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 44.99,
    isFree: false,
    tags: ['Docker', 'Kubernetes', 'DevOps'],
    thumbnailUrl:
      'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?auto=format&fit=crop&w=1200&q=80',
  },
];

const categories = [
  'Web Development',
  'JavaScript',
  'Frontend Engineering',
  'Backend Engineering',
  'Databases',
  'Data Science',
  'AI & Machine Learning',
  'DevOps & Cloud',
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
  'PostgreSQL',
  'Prisma',
  'SQL',
  'Python',
  'Data Science',
  'NumPy',
  'Machine Learning',
  'scikit-learn',
  'Docker',
  'Kubernetes',
  'DevOps',
];

function decimal(value) {
  return new Prisma.Decimal(String(value));
}

function dateDaysAgo(days) {
  const value = new Date();
  value.setDate(value.getDate() - days);
  return value;
}

function releaseDate(createdAt) {
  const value = new Date(createdAt);
  value.setDate(value.getDate() + PENDING_RELEASE_DAYS);
  return value;
}

function snapshotData(amount, coupon = null, createdAt = new Date()) {
  const original = Number(amount);
  const discountPercent = coupon?.discountPercent ?? 0;
  const discountAmount = Math.round(original * (discountPercent / 100) * 100) / 100;
  const paid = Math.max(0, Math.round((original - discountAmount) * 100) / 100);
  const instructorGross = Math.round(original * (INSTRUCTOR_SHARE_RATE / 100) * 100) / 100;
  const platformGross = Math.round(original * (PLATFORM_SHARE_RATE / 100) * 100) / 100;
  const platformDiscount = coupon?.scope === CouponScope.PLATFORM ? discountAmount : 0;
  const instructorDiscount = coupon?.scope === CouponScope.INSTRUCTOR ? discountAmount : 0;

  return {
    originalCoursePrice: decimal(original),
    customerPaidAmount: decimal(paid),
    couponId: coupon?.id ?? null,
    couponCode: coupon?.code ?? null,
    couponScope: coupon?.scope ?? null,
    discountAmount: decimal(discountAmount),
    discountAbsorbedByPlatform: decimal(platformDiscount),
    discountAbsorbedByInstructor: decimal(instructorDiscount),
    platformShareRate: PLATFORM_SHARE_RATE,
    instructorShareRate: INSTRUCTOR_SHARE_RATE,
    instructorGrossRevenue: decimal(instructorGross),
    instructorNetRevenue: decimal(Math.max(0, instructorGross - instructorDiscount)),
    platformGrossRevenue: decimal(platformGross),
    platformNetRevenue: decimal(Math.max(0, platformGross - platformDiscount)),
    pendingReleaseDate: releaseDate(createdAt),
  };
}

async function clearAll() {
  const models = [
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
    prisma.notification,
    prisma.certificate,
    prisma.orderFinancialSnapshot,
    prisma.transaction,
    prisma.order,
    prisma.coupon,
    prisma.withdrawalRequest,
    prisma.wallet,
    prisma.instructorSubscription,
    prisma.report,
    prisma.auditLog,
    prisma.courseStatistic,
    prisma.courseProgress,
    prisma.enrollment,
    prisma.lesson,
    prisma.chapter,
    prisma.course,
    prisma.userProfile,
    prisma.user,
    prisma.tag,
    prisma.category,
  ];

  for (const model of models) {
    await model.deleteMany();
  }
}

async function seedUsers(passwordHash) {
  const userMap = new Map();

  for (const user of users) {
    const created = await prisma.user.create({
      data: {
        email: user.email,
        password: passwordHash,
        fullName: user.fullName,
        role: user.role,
        provider: Provider.LOCAL,
        isActive: true,
        profile: {
          create: {
            avatarUrl: `https://api.dicebear.com/8.x/initials/svg?seed=${encodeURIComponent(user.fullName)}`,
            skills: user.skills,
            learningGoals: user.learningGoals,
          },
        },
      },
    });
    userMap.set(user.email, created);
  }

  return userMap;
}

async function seedLookups() {
  const categoryMap = new Map();
  const tagMap = new Map();

  for (const name of categories) {
    const created = await prisma.category.create({ data: { name } });
    categoryMap.set(name, created);
  }

  for (const name of tags) {
    const created = await prisma.tag.create({ data: { name } });
    tagMap.set(name, created);
  }

  return { categoryMap, tagMap };
}

function lessonNames(courseTitle, chapterIndex) {
  if (chapterIndex === 0) {
    return [
      `${courseTitle}: core concepts`,
      `${courseTitle}: guided practice`,
    ];
  }
  return [
    `${courseTitle}: applied workflow`,
    `${courseTitle}: project checklist`,
  ];
}

async function seedCourses(categoryMap, tagMap, userMap) {
  const createdCourses = [];

  for (const [courseIndex, blueprint] of courseBlueprints.entries()) {
    const video = videos[courseIndex % videos.length];
    const promo = videos[(courseIndex + 1) % videos.length];

    const course = await prisma.course.create({
      data: {
        instructorId: userMap.get(blueprint.instructor).id,
        categoryId: categoryMap.get(blueprint.category).id,
        title: blueprint.title,
        subtitle: blueprint.subtitle,
        summary: blueprint.summary,
        thumbnailUrl: blueprint.thumbnailUrl,
        promoVideoUrl: promo.url,
        price: decimal(blueprint.price),
        isFree: blueprint.isFree,
        level: blueprint.level,
        status: CourseStatus.PUBLISHED,
        tags: {
          connect: blueprint.tags.map((name) => ({ id: tagMap.get(name).id })),
        },
        chapters: {
          create: [0, 1].map((chapterIndex) => ({
            title: chapterIndex === 0 ? 'Foundations' : 'Applied Project',
            orderIndex: chapterIndex,
            lessons: {
              create: lessonNames(blueprint.title, chapterIndex).map((title, lessonIndex) => {
                const doc = docs[(courseIndex + lessonIndex) % docs.length];
                return {
                  title,
                  orderIndex: lessonIndex,
                  materials: {
                    create: [
                      {
                        type: MaterialType.VIDEO,
                        fileUrl: video.url,
                        fileSize: video.size,
                        status: MaterialStatus.READY,
                      },
                      {
                        type: MaterialType.DOCUMENT,
                        fileUrl: doc.url,
                        fileSize: doc.size,
                        status: MaterialStatus.READY,
                      },
                    ],
                  },
                };
              }),
            },
            quizzes: {
              create: [
                {
                  title: `${blueprint.title} checkpoint`,
                  timeLimit: 15,
                  passingScore: 70,
                  randomizeQuestions: true,
                  isEssay: false,
                  orderIndex: 0,
                  questions: {
                    create: [
                      {
                        content: `What is the main goal of ${blueprint.title}?`,
                        explanation: 'The course focuses on practical skill building.',
                        orderIndex: 0,
                        points: 10,
                        choices: {
                          create: [
                            { content: 'Build practical skills', isCorrect: true, orderIndex: 0 },
                            { content: 'Memorize unrelated facts', isCorrect: false, orderIndex: 1 },
                            { content: 'Skip project work', isCorrect: false, orderIndex: 2 },
                          ],
                        },
                      },
                      {
                        content: 'Which habit matters most while learning?',
                        explanation: 'Consistent practice turns lessons into usable skills.',
                        orderIndex: 1,
                        points: 10,
                        choices: {
                          create: [
                            { content: 'Practice consistently', isCorrect: true, orderIndex: 0 },
                            { content: 'Avoid feedback', isCorrect: false, orderIndex: 1 },
                            { content: 'Ignore examples', isCorrect: false, orderIndex: 2 },
                          ],
                        },
                      },
                    ],
                  },
                },
                {
                  title: `${blueprint.title} reflection`,
                  timeLimit: 20,
                  passingScore: 60,
                  isEssay: true,
                  orderIndex: 1,
                  questions: {
                    create: [
                      {
                        content: 'Describe how you would apply this course in a real project.',
                        minWords: 25,
                        points: 20,
                        orderIndex: 0,
                      },
                    ],
                  },
                },
              ],
            },
          })),
        },
      },
      include: {
        chapters: {
          orderBy: { orderIndex: 'asc' },
          include: {
            lessons: { orderBy: { orderIndex: 'asc' } },
            quizzes: {
              orderBy: { orderIndex: 'asc' },
              include: {
                questions: {
                  orderBy: { orderIndex: 'asc' },
                  include: { choices: { orderBy: { orderIndex: 'asc' } } },
                },
              },
            },
          },
        },
      },
    });

    createdCourses.push(course);
  }

  return createdCourses;
}

async function seedInstructorCommerce(userMap) {
  const khoa = userMap.get('khoa@skillforge.dev');
  const han = userMap.get('han@skillforge.dev');

  const khoaWallet = await prisma.wallet.create({
    data: {
      userId: khoa.id,
      availableBalance: decimal(620),
      pendingBalance: decimal(180),
    },
  });

  await prisma.wallet.create({
    data: {
      userId: han.id,
      availableBalance: decimal(480),
      pendingBalance: decimal(240),
    },
  });

  await prisma.withdrawalRequest.create({
    data: {
      walletId: khoaWallet.id,
      amount: decimal(120),
      bankInfo: 'VCB - 0123456789 - Nguyen Minh Khoa',
      status: WithdrawalStatus.PENDING,
      note: 'Monthly instructor payout request',
    },
  });

  await prisma.instructorSubscription.create({
    data: {
      userId: khoa.id,
      planCode: 'PRO_INSTRUCTOR_MONTHLY',
      amount: decimal(19.99),
      currency: 'USD',
      status: InstructorSubscriptionStatus.ACTIVE,
      paymentStatus: InstructorSubscriptionPaymentStatus.SUCCEEDED,
    },
  });

  await prisma.instructorSubscription.create({
    data: {
      userId: han.id,
      planCode: 'PRO_INSTRUCTOR_MONTHLY',
      amount: decimal(19.99),
      currency: 'USD',
      status: InstructorSubscriptionStatus.ACTIVE,
      paymentStatus: InstructorSubscriptionPaymentStatus.SUCCEEDED,
    },
  });
}

async function seedCoupons(userMap) {
  const coupons = [
    {
      code: 'WELCOME10',
      discountPercent: 10,
      description: 'Platform welcome coupon',
      scope: CouponScope.PLATFORM,
      instructorId: null,
      maxUses: 500,
    },
    {
      code: 'REACT20',
      discountPercent: 20,
      description: 'React course promo by Khoa',
      scope: CouponScope.INSTRUCTOR,
      instructorId: userMap.get('khoa@skillforge.dev').id,
      maxUses: 100,
    },
    {
      code: 'BACKEND30',
      discountPercent: 30,
      description: 'Backend bundle promo by Han',
      scope: CouponScope.INSTRUCTOR,
      instructorId: userMap.get('han@skillforge.dev').id,
      maxUses: 80,
    },
  ];

  const couponMap = new Map();
  for (const coupon of coupons) {
    const created = await prisma.coupon.create({
      data: {
        code: coupon.code,
        discountPercent: coupon.discountPercent,
        description: coupon.description,
        scope: coupon.scope,
        instructorId: coupon.instructorId,
        maxUses: coupon.maxUses,
        expiresAt: dateDaysAgo(-60),
        isActive: true,
      },
    });
    couponMap.set(created.code, created);
  }

  return couponMap;
}

function allLessons(course) {
  return course.chapters.flatMap((chapter) => chapter.lessons);
}

function allQuizzes(course) {
  return course.chapters.flatMap((chapter) => chapter.quizzes);
}

async function createCompletedOrder({ student, course, coupon = null, daysAgo = 1 }) {
  const createdAt = dateDaysAgo(daysAgo);
  const snapshot = snapshotData(Number(course.price), coupon, createdAt);

  const order = await prisma.order.create({
    data: {
      userId: student.id,
      courseId: course.id,
      amount: snapshot.customerPaidAmount,
      status: OrderStatus.COMPLETED,
      couponId: coupon?.id ?? null,
      createdAt,
      updatedAt: createdAt,
      transaction: {
        create: {
          gateway: PaymentGateway.MOMO,
          externalTransactionId: `MOMO-${student.id.slice(0, 8)}-${course.id.slice(0, 8)}`,
          amount: snapshot.customerPaidAmount,
          createdAt,
        },
      },
      financialSnapshot: {
        create: snapshot,
      },
    },
  });

  return order;
}

async function seedLearningActivity(courses, userMap, couponMap) {
  const plans = [
    {
      email: 'nam@skillforge.dev',
      courseIndexes: [0, 1, 2],
      completedLessons: [4, 3, 1],
    },
    {
      email: 'anh@skillforge.dev',
      courseIndexes: [0, 2, 5],
      completedLessons: [2, 4, 1],
    },
    {
      email: 'tung@skillforge.dev',
      courseIndexes: [3, 4, 7],
      completedLessons: [3, 2, 1],
    },
    {
      email: 'linh@skillforge.dev',
      courseIndexes: [5, 6],
      completedLessons: [4, 2],
    },
  ];

  const reviewText = [
    'Clear structure and immediately useful exercises.',
    'The examples helped me connect the concepts quickly.',
    'Good pacing, practical projects, and helpful explanations.',
    'I would recommend this course to anyone starting this topic.',
  ];

  for (const plan of plans) {
    const student = userMap.get(plan.email);
    for (const [index, courseIndex] of plan.courseIndexes.entries()) {
      const course = courses[courseIndex];
      const lessons = allLessons(course);
      const quizzes = allQuizzes(course);
      const completedCount = Math.min(plan.completedLessons[index], lessons.length);
      const progressPercent = lessons.length === 0 ? 0 : Math.round((completedCount / lessons.length) * 100);
      const coupon = index === 1 ? couponMap.get('WELCOME10') : null;

      await prisma.enrollment.create({
        data: {
          userId: student.id,
          courseId: course.id,
          status: EnrollmentStatus.ACTIVE,
          progress: progressPercent,
          enrolledAt: dateDaysAgo(16 - index * 3),
        },
      });

      await createCompletedOrder({
        student,
        course,
        coupon,
        daysAgo: 16 - index * 3,
      });

      await prisma.courseProgress.create({
        data: {
          userId: student.id,
          courseId: course.id,
          progress: progressPercent / 100,
          isCompleted: progressPercent === 100,
          lastAccessed: dateDaysAgo(index),
        },
      });

      for (let lessonIndex = 0; lessonIndex < completedCount; lessonIndex += 1) {
        await prisma.lessonProgress.create({
          data: {
            userId: student.id,
            lessonId: lessons[lessonIndex].id,
            isCompleted: true,
            lastWatchedPosition: 300 + lessonIndex * 90,
            timeSpentSeconds: 900 + lessonIndex * 120,
          },
        });
      }

      if (quizzes[0]) {
        const quiz = quizzes[0];
        const attempt = await prisma.quizAttempt.create({
          data: {
            studentId: student.id,
            quizId: quiz.id,
            startTime: dateDaysAgo(index + 2),
            endTime: dateDaysAgo(index + 2),
            score: progressPercent >= 50 ? 85 : 55,
            isPassed: progressPercent >= 50,
            status: AttemptStatus.GRADED,
            instructorFeedback:
              progressPercent >= 50 ? 'Strong work. Keep practicing.' : 'Review the first chapter and try again.',
          },
        });

        for (const question of quiz.questions) {
          const correctChoice = question.choices.find((choice) => choice.isCorrect);
          await prisma.studentAnswer.create({
            data: {
              attemptId: attempt.id,
              questionId: question.id,
              selectedChoiceId: correctChoice?.id ?? null,
              pointsAwarded: progressPercent >= 50 ? question.points : 0,
            },
          });
        }
      }

      if (completedCount >= 2) {
        await prisma.review.create({
          data: {
            studentId: student.id,
            courseId: course.id,
            rating: progressPercent === 100 ? 5 : 4,
            content: reviewText[(courseIndex + index) % reviewText.length],
            instructorReply: progressPercent === 100 ? 'Thank you for completing the course.' : null,
          },
        });
      }

      if (progressPercent === 100) {
        await prisma.certificate.create({
          data: {
            studentId: student.id,
            courseId: course.id,
            certificateCode: `SF-${student.email.split('@')[0].toUpperCase()}-${courseIndex + 1}`,
            pdfUrl: `https://example.com/certificates/${student.id}-${course.id}.pdf`,
            imageUrl: `https://example.com/certificates/${student.id}-${course.id}.png`,
          },
        });
      }
    }
  }

  for (const course of courses) {
    const [enrollmentCount, reviews] = await Promise.all([
      prisma.enrollment.count({ where: { courseId: course.id, status: EnrollmentStatus.ACTIVE } }),
      prisma.review.findMany({ where: { courseId: course.id } }),
    ]);
    const averageRating =
      reviews.length === 0 ? 0 : reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length;

    await prisma.course.update({
      where: { id: course.id },
      data: {
        studentCount: enrollmentCount,
        averageRating: Number(averageRating.toFixed(1)),
      },
    });

    await prisma.courseStatistic.create({
      data: {
        courseId: course.id,
        date: new Date(new Date().toISOString().slice(0, 10)),
        revenue: decimal(enrollmentCount * Number(course.price)),
        enrollmentCount,
        avgCompletionRate: enrollmentCount === 0 ? 0 : 0.52,
      },
    });
  }
}

async function seedFavorites(courses, userMap) {
  const favorites = [
    ['nam@skillforge.dev', 4],
    ['nam@skillforge.dev', 5],
    ['anh@skillforge.dev', 1],
    ['anh@skillforge.dev', 7],
    ['tung@skillforge.dev', 0],
    ['linh@skillforge.dev', 2],
    ['linh@skillforge.dev', 7],
  ];

  for (const [email, courseIndex] of favorites) {
    const student = userMap.get(email);
    const course = courses[courseIndex];
    const existingEnrollment = await prisma.enrollment.findUnique({
      where: {
        userId_courseId: {
          userId: student.id,
          courseId: course.id,
        },
      },
    });

    if (existingEnrollment) continue;

    await prisma.favorite.create({
      data: {
        userId: student.id,
        courseId: course.id,
      },
    });
  }
}

async function seedDiscussions(courses, userMap) {
  const nam = userMap.get('nam@skillforge.dev');
  const han = userMap.get('han@skillforge.dev');
  const lesson = courses[0].chapters[0].lessons[0];

  const parent = await prisma.discussion.create({
    data: {
      lessonId: lesson.id,
      userId: nam.id,
      content: 'How should I decide between flexbox and grid for this layout?',
      timestampTag: 120,
      isPinned: true,
    },
  });

  await prisma.discussion.create({
    data: {
      lessonId: lesson.id,
      userId: han.id,
      parentId: parent.id,
      content: 'Use flexbox for one-dimensional alignment and grid for two-dimensional page structure.',
      timestampTag: 150,
    },
  });
}

async function seedReportsAndAudit(userMap, courses) {
  const admin = userMap.get('admin@skillforge.dev');
  const nam = userMap.get('nam@skillforge.dev');

  await prisma.report.create({
    data: {
      reporterId: nam.id,
      targetId: courses[1].id,
      type: ReportType.COURSE,
      reason: 'Demo report for moderation workflow.',
      status: ReportStatus.PENDING,
    },
  });

  await prisma.auditLog.create({
    data: {
      adminId: admin.id,
      action: ActionType.UPDATE,
      entityType: 'Course',
      entityId: courses[0].id,
      details: {
        note: 'Seeded audit event for admin dashboard testing.',
      },
    },
  });
}

async function seedNotifications(userMap, courses) {
  const nam = userMap.get('nam@skillforge.dev');
  const khoa = userMap.get('khoa@skillforge.dev');

  await prisma.notification.createMany({
    data: [
      {
        recipientId: nam.id,
        type: NotificationType.ORDER_CREATED,
        title: 'Enrollment successful',
        message: `You are now enrolled in ${courses[0].title}.`,
        metadata: { courseId: courses[0].id, courseTitle: courses[0].title },
      },
      {
        recipientId: khoa.id,
        actorId: nam.id,
        type: NotificationType.COURSE_ENROLLMENT_CREATED,
        title: 'New course enrollment',
        message: `A student enrolled in ${courses[0].title}.`,
        metadata: { courseId: courses[0].id, courseTitle: courses[0].title },
      },
      {
        recipientId: nam.id,
        actorId: khoa.id,
        type: NotificationType.DISCUSSION_REPLY_CREATED,
        title: 'New discussion reply',
        message: 'Your question received a reply from the instructor.',
        metadata: { courseId: courses[0].id },
        readAt: new Date(),
      },
    ],
  });
}

async function main() {
  const passwordHash = await bcrypt.hash(PASSWORD, 10);

  console.log('[seed] Clearing database');
  await clearAll();

  console.log('[seed] Users and profiles');
  const userMap = await seedUsers(passwordHash);

  console.log('[seed] Categories and tags');
  const { categoryMap, tagMap } = await seedLookups();

  console.log('[seed] Instructor wallets and subscriptions');
  await seedInstructorCommerce(userMap);

  console.log('[seed] Coupons');
  const couponMap = await seedCoupons(userMap);

  console.log('[seed] Courses, chapters, lessons, materials, quizzes');
  const courses = await seedCourses(categoryMap, tagMap, userMap);

  console.log('[seed] Orders, transactions, enrollments, progress, reviews, certificates');
  await seedLearningActivity(courses, userMap, couponMap);

  console.log('[seed] Favorites');
  await seedFavorites(courses, userMap);

  console.log('[seed] Discussions, reports, audit logs, notifications');
  await seedDiscussions(courses, userMap);
  await seedReportsAndAudit(userMap, courses);
  await seedNotifications(userMap, courses);

  console.log('\nSeed complete.');
  console.log(`Password for all accounts: ${PASSWORD}`);
  console.table(
    users.map((user) => ({
      email: user.email,
      role: user.role,
      name: user.fullName,
    })),
  );
  console.log('Coupons: WELCOME10, REACT20, BACKEND30');
}

main()
  .catch((error) => {
    console.error('Seed failed:', error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
