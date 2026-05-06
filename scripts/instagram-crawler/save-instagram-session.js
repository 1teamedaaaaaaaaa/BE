#!/usr/bin/env node

const fs = require("fs");
const path = require("path");
const readline = require("readline");
const { chromium } = require("playwright");

const DEFAULT_SESSION_FILE = path.join(__dirname, "storage", "instagram-session.json");

function parseArgs(argv) {
  const args = {};
  for (let i = 2; i < argv.length; i += 1) {
    const token = argv[i];
    if (!token.startsWith("--")) continue;
    const key = token.slice(2);
    const next = argv[i + 1];
    if (!next || next.startsWith("--")) {
      args[key] = "true";
      continue;
    }
    args[key] = next;
    i += 1;
  }
  return args;
}

function waitForEnter(prompt) {
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
  });

  return new Promise((resolve) => {
    rl.question(prompt, () => {
      rl.close();
      resolve();
    });
  });
}

async function main() {
  const args = parseArgs(process.argv);
  const sessionFile = args.sessionFile?.trim() || DEFAULT_SESSION_FILE;
  const sessionDir = path.dirname(sessionFile);

  fs.mkdirSync(sessionDir, { recursive: true });

  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext({
    viewport: { width: 1440, height: 1200 }
  });
  const page = await context.newPage();

  try {
    await page.goto("https://www.instagram.com/accounts/login/", {
      waitUntil: "domcontentloaded",
      timeout: 60000
    });

    process.stdout.write("브라우저에서 인스타그램 로그인을 완료하세요.\n");
    await waitForEnter("로그인 완료 후 엔터를 누르세요: ");

    await context.storageState({ path: sessionFile });
    process.stdout.write(`세션 저장 완료: ${sessionFile}\n`);
  } finally {
    await context.close();
    await browser.close();
  }
}

main().catch((error) => {
  process.stderr.write(`${error.message || "Unknown session save error"}\n`);
  process.exit(1);
});
