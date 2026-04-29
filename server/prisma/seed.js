const fs = require('fs');
const path = require('path');

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
    if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith("'") && value.endsWith("'"))) {
      value = value.slice(1, -1);
    }
    process.env[key] = value;
  }
}

loadEnvFile(path.resolve(__dirname, '..', '.env'));

const { PrismaClient, Prisma, Role, CourseLevel, CourseStatus, MaterialType, MaterialStatus, EnrollmentStatus } = require('@prisma/client');
const bcrypt = require('bcrypt');

const prisma = new PrismaClient();
const PASSWORD = '123456';
const DEFAULT_DOC_SIZE = 1_250_000;

// ─── BLUEPRINTS ────────────────────────────────────────────────────────────────

const courseBlueprints = [
  {
    title: 'HTML & CSS Foundations',
    subtitle: 'Build accessible pages with semantic HTML and modern CSS.',
    summary: 'Learn how to structure content correctly, then style it with layout systems that scale from mobile to desktop.',
    categoryName: 'Web Development',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 0, isFree: true,
    thumbnailUrl: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=UB1O30fR-EE',
    tagNames: ['HTML', 'CSS', 'Responsive Design'],
    chapters: [
      { title: 'HTML Basics', docUrl: 'https://developer.mozilla.org/en-US/docs/Web/HTML', videoUrl: 'https://www.youtube.com/watch?v=BsDoLVMnmZs', videoSize: 41_000_000,
        lessons: ['Document structure and semantic tags', 'Forms, tables, and accessibility'] },
      { title: 'Modern CSS', docUrl: 'https://developer.mozilla.org/en-US/docs/Web/CSS', videoUrl: 'https://www.youtube.com/watch?v=rIO5326FgPE', videoSize: 43_000_000,
        lessons: ['Box model and spacing', 'Flexbox and responsive layout'] },
    ],
  },
  {
    title: 'JavaScript Core',
    subtitle: 'Write dependable browser and runtime code with modern JavaScript.',
    summary: 'Cover language fundamentals, object patterns, and asynchronous behavior that every developer needs.',
    categoryName: 'JavaScript',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 19.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=hdI2bqOjy3c',
    tagNames: ['JavaScript', 'Async', 'ES6'],
    chapters: [
      { title: 'Language Essentials', docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript', videoUrl: 'https://www.youtube.com/watch?v=W6NZfCO5SIk', videoSize: 47_000_000,
        lessons: ['Syntax, types, and control flow', 'Arrays, objects, and loops'] },
      { title: 'Async JavaScript', docUrl: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide', videoUrl: 'https://www.youtube.com/watch?v=V_Kr9OSfDeU', videoSize: 50_000_000,
        lessons: ['Functions and scope', 'Async/await and fetch'] },
    ],
  },
  {
    title: 'React UI Systems',
    subtitle: 'Compose predictable user interfaces with reusable React patterns.',
    summary: 'Move from JSX basics to data flow, hooks, and reusable UI composition for real product screens.',
    categoryName: 'Frontend Engineering',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 29.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=w7ejDZ8SWv8',
    tagNames: ['React', 'UI', 'Hooks'],
    chapters: [
      { title: 'Components and State', docUrl: 'https://react.dev/learn', videoUrl: 'https://www.youtube.com/watch?v=Ke90Tje7VS0', videoSize: 52_000_000,
        lessons: ['Components and JSX', 'Props, state, and rendering'] },
      { title: 'Data and Reusability', docUrl: 'https://react.dev/reference/react', videoUrl: 'https://www.youtube.com/watch?v=G6D9cBaLViA', videoSize: 56_000_000,
        lessons: ['Hooks and component lifecycle thinking', 'Data fetching and reusable UI'] },
    ],
  },
  {
    title: 'Node.js API Foundations',
    subtitle: 'Build reliable REST APIs with Node.js, Express, and clean service logic.',
    summary: 'Learn how to wire request handling, validation, error handling, and authentication into a maintainable backend.',
    categoryName: 'Backend Engineering',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 34.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=fBNz5xF-Kx4',
    tagNames: ['Node.js', 'Express', 'API'],
    chapters: [
      { title: 'Runtime and Routing', docUrl: 'https://nodejs.org/en/learn/getting-started/introduction-to-nodejs', videoUrl: 'https://www.youtube.com/watch?v=L72fhGm1tfE', videoSize: 58_000_000,
        lessons: ['Node runtime and project setup', 'Express routing and middleware'] },
      { title: 'Production Patterns', docUrl: 'https://expressjs.com/en/guide/routing.html', videoUrl: 'https://www.youtube.com/watch?v=7H_QH9nipNs', videoSize: 60_000_000,
        lessons: ['Validation and error handling', 'Authentication and API hardening'] },
    ],
  },
  {
    title: 'Git & Collaboration',
    subtitle: 'Use Git and GitHub safely in a team workflow.',
    summary: 'Practice branching, review flow, conflict resolution, and release habits that keep teams moving.',
    categoryName: 'Developer Tools',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 0, isFree: true,
    thumbnailUrl: 'https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=RGOj5yH7evk',
    tagNames: ['Git', 'GitHub', 'Collaboration'],
    chapters: [
      { title: 'Version Control Basics', docUrl: 'https://git-scm.com/book/en/v2', videoUrl: 'https://www.youtube.com/watch?v=RGOj5yH7evk', videoSize: 42_000_000,
        lessons: ['Git init, commit, and history', 'Branching and merge strategies'] },
      { title: 'Team Workflow', docUrl: 'https://docs.github.com/en/get-started', videoUrl: 'https://www.youtube.com/watch?v=dCzjp95Q1Yk', videoSize: 44_000_000,
        lessons: ['Pull requests and code review', 'Conflict resolution and release flow'] },
    ],
  },
  {
    title: 'PostgreSQL & Prisma',
    subtitle: 'Model data correctly and ship with safe database migrations.',
    summary: 'Learn relational design, indexing, and Prisma workflows that turn a schema into a production-ready database layer.',
    categoryName: 'Databases',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 24.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=qw--VYLpxG4',
    tagNames: ['PostgreSQL', 'Prisma', 'SQL'],
    chapters: [
      { title: 'Relational Design', docUrl: 'https://www.postgresql.org/docs/current/tutorial.html', videoUrl: 'https://www.youtube.com/watch?v=qw--VYLpxG4', videoSize: 53_000_000,
        lessons: ['Relational modeling and primary keys', 'Foreign keys and indexing'] },
      { title: 'Prisma Workflow', docUrl: 'https://www.prisma.io/docs/getting-started', videoUrl: 'https://www.youtube.com/watch?v=RebA5J-rlwg', videoSize: 55_000_000,
        lessons: ['Prisma schema design', 'Querying and migrations'] },
    ],
  },
  {
    title: 'Python for Data Science',
    subtitle: 'Analyze and visualize real-world data with Python.',
    summary: 'From NumPy arrays to Pandas DataFrames and Matplotlib charts — build the foundation for machine learning projects.',
    categoryName: 'Data Science',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 39.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=rfscVS0vtbw',
    tagNames: ['Python', 'Data Science', 'NumPy'],
    chapters: [
      { title: 'Python Essentials', docUrl: 'https://docs.python.org/3/tutorial/', videoUrl: 'https://www.youtube.com/watch?v=rfscVS0vtbw', videoSize: 62_000_000,
        lessons: ['Variables, lists, and loops', 'Functions and modules'] },
      { title: 'Data Analysis', docUrl: 'https://pandas.pydata.org/docs/', videoUrl: 'https://www.youtube.com/watch?v=vmEHCJofslg', videoSize: 65_000_000,
        lessons: ['NumPy arrays and operations', 'Pandas DataFrames and cleaning'] },
    ],
  },
  {
    title: 'Machine Learning with scikit-learn',
    subtitle: 'Build and evaluate ML models end-to-end.',
    summary: 'Implement regression, classification, clustering, and evaluation pipelines using Python and scikit-learn.',
    categoryName: 'AI & Machine Learning',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 49.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=pqNCD_5r0IU',
    tagNames: ['Python', 'Machine Learning', 'scikit-learn'],
    chapters: [
      { title: 'Supervised Learning', docUrl: 'https://scikit-learn.org/stable/supervised_learning.html', videoUrl: 'https://www.youtube.com/watch?v=pqNCD_5r0IU', videoSize: 68_000_000,
        lessons: ['Linear regression from scratch', 'Classification with decision trees'] },
      { title: 'Model Evaluation', docUrl: 'https://scikit-learn.org/stable/model_selection.html', videoUrl: 'https://www.youtube.com/watch?v=0B5eIE_1vpU', videoSize: 70_000_000,
        lessons: ['Train/test split and cross-validation', 'Precision, recall, and F1'] },
    ],
  },
  {
    title: 'Docker & Kubernetes Essentials',
    subtitle: 'Containerize applications and orchestrate them at scale.',
    summary: 'Go from writing a Dockerfile to deploying multi-service apps on Kubernetes with health checks and rolling updates.',
    categoryName: 'DevOps & Cloud',
    instructorEmail: 'han@skillforge.dev',
    level: CourseLevel.INTERMEDIATE,
    price: 44.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=Gjnup-PuquQ',
    tagNames: ['Docker', 'Kubernetes', 'DevOps'],
    chapters: [
      { title: 'Containers with Docker', docUrl: 'https://docs.docker.com/get-started/', videoUrl: 'https://www.youtube.com/watch?v=Gjnup-PuquQ', videoSize: 72_000_000,
        lessons: ['Dockerfile and image layers', 'Docker Compose for local development'] },
      { title: 'Orchestration with Kubernetes', docUrl: 'https://kubernetes.io/docs/home/', videoUrl: 'https://www.youtube.com/watch?v=s_o8dwzRlu4', videoSize: 75_000_000,
        lessons: ['Pods, deployments, and services', 'ConfigMaps, secrets, and rolling updates'] },
    ],
  },
  {
    title: 'UI/UX Design Fundamentals',
    subtitle: 'Design intuitive interfaces with proven UX principles.',
    summary: 'Learn typography, color theory, wireframing, and user research methods that turn ideas into polished product designs.',
    categoryName: 'Design',
    instructorEmail: 'khoa@skillforge.dev',
    level: CourseLevel.BEGINNER,
    price: 22.99, isFree: false,
    thumbnailUrl: 'https://images.unsplash.com/photo-1561070791-2526d30994b5?auto=format&fit=crop&w=1200&q=80',
    promoVideoUrl: 'https://www.youtube.com/watch?v=c9Wg6Cb_YlU',
    tagNames: ['UI', 'UX', 'Figma'],
    chapters: [
      { title: 'Design Principles', docUrl: 'https://www.nngroup.com/articles/ten-usability-heuristics/', videoUrl: 'https://www.youtube.com/watch?v=c9Wg6Cb_YlU', videoSize: 58_000_000,
        lessons: ['Typography and color theory', 'Gestalt principles and visual hierarchy'] },
      { title: 'Prototyping', docUrl: 'https://help.figma.com/hc/en-us/articles/360040314193', videoUrl: 'https://www.youtube.com/watch?v=FTFaQWZBqQ8', videoSize: 60_000_000,
        lessons: ['Wireframing with Figma', 'User testing and iterating on feedback'] },
    ],
  },
];

const baseUsers = [
  { email: 'admin@skillforge.dev', fullName: 'SkillForge Admin', role: Role.ADMIN, skills: ['Operations', 'Content Review'], learningGoals: null },
  { email: 'khoa@skillforge.dev', fullName: 'Nguyen Minh Khoa', role: Role.INSTRUCTOR, skills: ['HTML', 'CSS', 'React', 'Git', 'Figma'], learningGoals: null },
  { email: 'han@skillforge.dev', fullName: 'Tran Gia Han', role: Role.INSTRUCTOR, skills: ['JavaScript', 'Node.js', 'PostgreSQL', 'Python', 'Docker'], learningGoals: null },
  { email: 'nam@skillforge.dev', fullName: 'Le Hoang Nam', role: Role.STUDENT, skills: ['Frontend'], learningGoals: 'Become a full-stack developer.' },
  { email: 'anh@skillforge.dev', fullName: 'Ngo Minh Anh', role: Role.STUDENT, skills: ['UI', 'JavaScript'], learningGoals: 'Ship polished web apps with React.' },
  { email: 'tung@skillforge.dev', fullName: 'Vu Thanh Tung', role: Role.STUDENT, skills: ['Databases', 'DevOps'], learningGoals: 'Improve backend and deployment skills.' },
  { email: 'linh@skillforge.dev', fullName: 'Pham Thanh Linh', role: Role.STUDENT, skills: ['Python', 'Data Analysis'], learningGoals: 'Transition into a data scientist role.' },
  { email: 'duc@skillforge.dev', fullName: 'Nguyen Hoang Duc', role: Role.STUDENT, skills: ['Design', 'Frontend'], learningGoals: 'Master UI/UX and build a design portfolio.' },
];

const allCategories = ['Web Development', 'JavaScript', 'Frontend Engineering', 'Backend Engineering', 'Developer Tools', 'Databases', 'Data Science', 'AI & Machine Learning', 'DevOps & Cloud', 'Design'];
const allTags = ['HTML', 'CSS', 'Responsive Design', 'JavaScript', 'Async', 'ES6', 'React', 'UI', 'Hooks', 'Node.js', 'Express', 'API', 'Git', 'GitHub', 'Collaboration', 'PostgreSQL', 'Prisma', 'SQL', 'Python', 'Data Science', 'NumPy', 'Machine Learning', 'scikit-learn', 'Docker', 'Kubernetes', 'DevOps', 'UX', 'Figma'];

// ─── HELPERS ──────────────────────────────────────────────────────────────────

function buildMaterials(docUrl, videoUrl, videoSize) {
  return {
    create: [
      { type: MaterialType.DOCUMENT, fileUrl: docUrl, fileSize: DEFAULT_DOC_SIZE, status: MaterialStatus.READY },
      { type: MaterialType.VIDEO, fileUrl: videoUrl, fileSize: videoSize, status: MaterialStatus.READY },
    ],
  };
}

function flattenLessons(course) {
  return course.chapters.flatMap((ch) => ch.lessons);
}

// ─── CLEAR ────────────────────────────────────────────────────────────────────

async function clearAll() {
  console.log('[seed] Clearing all data...');
  const models = [
    prisma.studentAnswer, prisma.quizAttempt, prisma.answerChoice, prisma.question, prisma.quiz,
    prisma.discussion, prisma.lessonProgress, prisma.lessonMaterial, prisma.review, prisma.favorite,
    prisma.certificate, prisma.coupon, prisma.transaction, prisma.order,
    prisma.courseStatistic, prisma.courseProgress, prisma.enrollment,
    prisma.lesson, prisma.chapter, prisma.course,
    prisma.userProfile, prisma.user,
  ];
  for (const model of models) {
    try { await model.deleteMany(); } catch (_) {}
  }
  // Clear lookup tables separately (no FK to courses)
  try { await prisma.tag.deleteMany(); } catch (_) {}
  try { await prisma.category.deleteMany(); } catch (_) {}
}

// ─── USERS ────────────────────────────────────────────────────────────────────

async function seedUsers(passwordHash) {
  const map = new Map();
  for (const u of baseUsers) {
    const record = await prisma.user.upsert({
      where: { email: u.email },
      update: { fullName: u.fullName, role: u.role, provider: 'LOCAL', isActive: true, password: passwordHash },
      create: { email: u.email, fullName: u.fullName, role: u.role, provider: 'LOCAL', isActive: true, password: passwordHash },
    });
    await prisma.userProfile.upsert({
      where: { userId: record.id },
      update: { skills: u.skills, learningGoals: u.learningGoals },
      create: { userId: record.id, skills: u.skills, learningGoals: u.learningGoals },
    });
    map.set(u.email, record);
  }
  return map;
}

// ─── LOOKUP ───────────────────────────────────────────────────────────────────

async function seedLookup() {
  const categoryMap = new Map();
  const tagMap = new Map();
  for (const name of allCategories) {
    const r = await prisma.category.upsert({ where: { name }, update: {}, create: { name } });
    categoryMap.set(name, r);
  }
  for (const name of allTags) {
    const r = await prisma.tag.upsert({ where: { name }, update: {}, create: { name } });
    tagMap.set(name, r);
  }
  return { categoryMap, tagMap };
}

// ─── COURSES ──────────────────────────────────────────────────────────────────

async function seedCourses(categoryMap, tagMap, userMap) {
  const created = [];
  for (const bp of courseBlueprints) {
    const category = categoryMap.get(bp.categoryName);
    const instructor = userMap.get(bp.instructorEmail);
    const course = await prisma.course.create({
      data: {
        instructorId: instructor.id,
        categoryId: category.id,
        title: bp.title, subtitle: bp.subtitle, summary: bp.summary,
        thumbnailUrl: bp.thumbnailUrl, promoVideoUrl: bp.promoVideoUrl,
        price: new Prisma.Decimal(String(bp.price)),
        isFree: bp.isFree, level: bp.level,
        status: CourseStatus.PUBLISHED,
        tags: { connect: bp.tagNames.filter(n => tagMap.has(n)).map(n => ({ id: tagMap.get(n).id })) },
        chapters: {
          create: bp.chapters.map((ch, ci) => ({
            title: ch.title, orderIndex: ci,
            lessons: {
              create: ch.lessons.map((title, li) => ({
                title, orderIndex: li,
                materials: buildMaterials(ch.docUrl, ch.videoUrl, ch.videoSize),
              })),
            },
          })),
        },
      },
      include: {
        chapters: { orderBy: { orderIndex: 'asc' }, include: { lessons: { orderBy: { orderIndex: 'asc' }, include: { materials: true } } } },
      },
    });
    created.push(course);
  }
  return created;
}

// ─── ENROLLMENTS, PROGRESS, REVIEWS ──────────────────────────────────────────

async function seedActivity(courses, userMap) {
  const students = [
    { user: userMap.get('nam@skillforge.dev'),  completions: [4,3,3,2,2,1,0,0,0,0] },
    { user: userMap.get('anh@skillforge.dev'),  completions: [2,2,1,1,0,0,2,1,0,2] },
    { user: userMap.get('tung@skillforge.dev'), completions: [0,1,0,2,0,2,0,0,2,0] },
    { user: userMap.get('linh@skillforge.dev'), completions: [0,0,0,0,0,0,4,3,0,0] },
    { user: userMap.get('duc@skillforge.dev'),  completions: [2,0,2,0,0,0,0,0,0,4] },
  ];

  const reviewContents = [
    'Extremely well structured — I completed this in a weekend and could immediately apply it.',
    'Clear explanations with just the right level of depth. Highly recommend.',
    'Great content. Some sections could use more exercises but overall fantastic.',
    'This course accelerated my learning significantly. Worth every cent.',
    'Best resource I have found on this topic. The instructor explains things simply.',
  ];

  for (const [ci, course] of courses.entries()) {
    const lessons = flattenLessons(course);
    const enrollments = [];

    for (const [si, plan] of students.entries()) {
      const completed = plan.completions[ci] ?? 0;
      if (completed === 0) continue;

      const progress = lessons.length === 0 ? 0 : Math.round((completed / lessons.length) * 100);
      const enrollment = await prisma.enrollment.create({
        data: { userId: plan.user.id, courseId: course.id, status: EnrollmentStatus.ACTIVE, progress },
      });
      enrollments.push(enrollment);

      for (let li = 0; li < completed && li < lessons.length; li++) {
        await prisma.lessonProgress.create({
          data: { userId: plan.user.id, lessonId: lessons[li].id, isCompleted: true, lastWatchedPosition: 300 + li * 60 },
        });
      }

      await prisma.courseProgress.create({
        data: { userId: plan.user.id, courseId: course.id,
          progress: lessons.length === 0 ? 0 : completed / lessons.length,
          isCompleted: completed >= lessons.length },
      });

      // Review for enrolled students
      if (completed >= 2) {
        await prisma.review.create({
          data: {
            studentId: plan.user.id, courseId: course.id,
            rating: [5,5,4,4,5][si % 5],
            content: reviewContents[(si + ci) % reviewContents.length],
          },
        });
      }
    }

    if (enrollments.length > 0) {
      const revenue = enrollments.length * Number(course.price);
      const avgProgress = enrollments.reduce((s, e) => s + e.progress, 0) / enrollments.length;
      await prisma.courseStatistic.create({
        data: { courseId: course.id, date: new Date(), revenue: new Prisma.Decimal(String(revenue)), enrollmentCount: enrollments.length, avgCompletionRate: avgProgress / 100 },
      });
    }
  }
}

// ─── COUPONS ──────────────────────────────────────────────────────────────────

async function seedCoupons(userMap) {
  const khoa = userMap.get('khoa@skillforge.dev');
  const han = userMap.get('han@skillforge.dev');
  const coupons = [
    { code: 'WELCOME10', discountPercent: 10, instructorId: khoa.id },
    { code: 'REACT20', discountPercent: 20, instructorId: khoa.id },
    { code: 'SUMMER15', discountPercent: 15, instructorId: han.id },
    { code: 'BACKEND30', discountPercent: 30, instructorId: han.id },
    { code: 'FREESHIP5', discountPercent: 5, instructorId: khoa.id, isActive: false },
  ];
  for (const c of coupons) {
    await prisma.coupon.create({ data: { code: c.code, discountPercent: c.discountPercent, instructorId: c.instructorId, isActive: c.isActive ?? true } });
  }
}

// ─── MAIN ─────────────────────────────────────────────────────────────────────

async function main() {
  const passwordHash = await bcrypt.hash(PASSWORD, 10);

  await clearAll();
  console.log('[seed] DB cleared');

  const { categoryMap, tagMap } = await seedLookup();
  console.log(`[seed] ${allCategories.length} categories, ${allTags.length} tags`);

  const userMap = await seedUsers(passwordHash);
  console.log(`[seed] ${baseUsers.length} users`);

  const courses = await seedCourses(categoryMap, tagMap, userMap);
  console.log(`[seed] ${courses.length} courses`);

  await seedActivity(courses, userMap);
  console.log('[seed] Enrollments, progress, reviews, statistics');

  await seedCoupons(userMap);
  console.log('[seed] Coupons: WELCOME10 (10%), REACT20 (20%), SUMMER15 (15%), BACKEND30 (30%)');

  console.log(`\n${'-'.repeat(60)}`);
  console.log('SEEDED ACCOUNTS (password: ' + PASSWORD + ')');
  console.log('─'.repeat(60));
  console.log('INSTRUCTORS:');
  console.log('  khoa@skillforge.dev  →  Nguyen Minh Khoa  (INSTRUCTOR)');
  console.log('  han@skillforge.dev   →  Tran Gia Han       (INSTRUCTOR)');
  console.log('STUDENTS:');
  console.log('  nam@skillforge.dev   →  Le Hoang Nam       (STUDENT)  — enrolled in courses 1-4');
  console.log('  anh@skillforge.dev   →  Ngo Minh Anh       (STUDENT)  — enrolled in courses 1-4,7,8,10');
  console.log('  tung@skillforge.dev  →  Vu Thanh Tung      (STUDENT)  — enrolled in courses 2,4,6,9');
  console.log('  linh@skillforge.dev  →  Pham Thanh Linh    (STUDENT)  — enrolled in courses 7,8');
  console.log('  duc@skillforge.dev   →  Nguyen Hoang Duc   (STUDENT)  — enrolled in courses 1,3,10');
  console.log('ADMIN:');
  console.log('  admin@skillforge.dev →  SkillForge Admin   (ADMIN)');
  console.log('COUPONS:');
  console.log('  WELCOME10  → 10% off  (khoa)');
  console.log('  REACT20    → 20% off  (khoa)');
  console.log('  SUMMER15   → 15% off  (han)');
  console.log('  BACKEND30  → 30% off  (han)');
  console.log('-'.repeat(60));
}

main()
  .catch((e) => { console.error('Seed failed:', e); process.exitCode = 1; })
  .finally(async () => { await prisma.$disconnect(); });