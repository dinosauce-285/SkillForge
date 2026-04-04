#!/usr/bin/env node

import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

const ROOT = process.cwd();

const ALLOWED_EXTENSIONS = new Set([
  '.js',
  '.jsx',
  '.ts',
  '.tsx',
  '.kt',
  '.kts',
  '.java',
  '.json',
  '.md',
  '.yml',
  '.yaml',
  '.xml',
  '.gradle',
  '.properties',
]);

const EXCLUDED_DIRS = new Set([
  '.git',
  '.idea',
  'node_modules',
  'build',
  'dist',
  '.gradle',
  '.kotlin',
  'out',
  'coverage',
]);

const EXCLUDED_FILES = new Set([
  'test.md',
]);

const VIETNAMESE_CHAR_REGEX = /[\u0102\u0103\u00C2\u00E2\u00CA\u00EA\u00D4\u00F4\u01A0\u01A1\u01AF\u01B0\u0110\u0111\u00C0-\u00C3\u00C8-\u00CA\u00CC-\u00CD\u00D2-\u00D5\u00D9-\u00DA\u00DD\u00E0-\u00E3\u00E8-\u00EA\u00EC-\u00ED\u00F2-\u00F5\u00F9-\u00FA\u00FD\u1EA0-\u1EF9]/u;
const EMOJI_REGEX = /[\u{1F300}-\u{1FAFF}\u{1F1E6}-\u{1F1FF}\u{2600}-\u{27BF}]/u;

function walk(dir, files = []) {
  const entries = readdirSync(dir, { withFileTypes: true });

  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);
    const relativePath = path.relative(ROOT, fullPath).replaceAll('\\', '/');

    if (entry.isDirectory()) {
      if (EXCLUDED_DIRS.has(entry.name)) {
        continue;
      }
      walk(fullPath, files);
      continue;
    }

    const ext = path.extname(entry.name);
    if (ALLOWED_EXTENSIONS.has(ext) && !EXCLUDED_FILES.has(relativePath)) {
      files.push({ fullPath, relativePath });
    }
  }

  return files;
}

function scanFile(file) {
  const content = readFileSync(file.fullPath, 'utf8');
  const lines = content.split(/\r?\n/);
  const violations = [];

  for (let i = 0; i < lines.length; i += 1) {
    const line = lines[i];
    if (VIETNAMESE_CHAR_REGEX.test(line)) {
      violations.push({ type: 'Vietnamese', line: i + 1 });
    }
    if (EMOJI_REGEX.test(line)) {
      violations.push({ type: 'Emoji', line: i + 1 });
    }
  }

  return violations;
}

function main() {
  const files = walk(ROOT);
  const report = [];

  for (const file of files) {
    const violations = scanFile(file);
    if (violations.length > 0) {
      report.push({ file: file.relativePath, violations });
    }
  }

  if (report.length === 0) {
    console.log('Text policy check passed: no Vietnamese text or emoji found in scanned files.');
    return;
  }

  console.error('Text policy check failed. Remove Vietnamese text and emoji from scanned files.');
  for (const entry of report) {
    for (const violation of entry.violations) {
      console.error(`- ${entry.file}:${violation.line} [${violation.type}] policy violation`);
    }
  }
  process.exit(1);
}

main();
