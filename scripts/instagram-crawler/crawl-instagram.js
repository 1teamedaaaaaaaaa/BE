#!/usr/bin/env node

const { chromium } = require("playwright");

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

function requireArg(args, name) {
  const value = args[name];
  if (!value || value.trim() === "") {
    throw new Error(`Missing required argument: --${name}`);
  }
  return value.trim();
}

function normalizeCount(raw) {
  if (raw == null) return 0;
  const text = String(raw).trim().toLowerCase().replace(/,/g, "");
  const match = text.match(/^([\d.]+)(k|m)?$/);
  if (!match) {
    const numeric = Number.parseInt(text.replace(/[^\d]/g, ""), 10);
    return Number.isNaN(numeric) ? 0 : numeric;
  }
  const value = Number.parseFloat(match[1]);
  const unit = match[2];
  if (unit === "k") return Math.round(value * 1000);
  if (unit === "m") return Math.round(value * 1000000);
  return Math.round(value);
}

function parseTimestamp(value) {
  if (!value) return null;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return null;
  return date;
}

function toOutputTimestamp(date) {
  if (!date) return null;
  return date.toISOString();
}

async function closeLoginPopup(page) {
  const dismissTexts = ["Not now", "Cancel", "Later"];
  for (const text of dismissTexts) {
    const locator = page.getByText(text, { exact: true });
    if (await locator.count()) {
      try {
        await locator.first().click({ timeout: 1000 });
        return;
      } catch (_) {
        // ignore
      }
    }
  }
}

async function collectPostLinks(page, maxPosts) {
  const links = new Set();

  for (let i = 0; i < 8 && links.size < maxPosts; i += 1) {
    const hrefs = await page.$$eval("a[href]", (elements) =>
      elements
        .map((element) => element.getAttribute("href"))
        .filter((href) => href && (/^\/p\//.test(href) || /^\/reel\//.test(href)))
    );

    hrefs.forEach((href) => links.add(`https://www.instagram.com${href}`));

    await page.mouse.wheel(0, 2500);
    await page.waitForTimeout(1200);
  }

  return Array.from(links).slice(0, maxPosts);
}

async function extractPostData(page, permalink) {
  return page.evaluate(() => {
    const time = document.querySelector("time");
    const metaDescription = document.querySelector('meta[property="og:description"]')?.content || "";
    const metaTitle = document.querySelector('meta[property="og:title"]')?.content || "";
    const articleText = document.querySelector("article")?.innerText || "";
    const caption =
      document.querySelector("article h1")?.textContent?.trim() ||
      document.querySelector("article ul li div > div > div span")?.textContent?.trim() ||
      "";

    const combinedText = [metaDescription, metaTitle, articleText].join(" ");

    const likeMatch =
      combinedText.match(/([\d.,]+[kKmM]?)\s+likes?/) ||
      combinedText.match(/liked by [^ ]+ and ([\d.,]+[kKmM]?) others/);

    const commentMatch =
      combinedText.match(/View all ([\d.,]+[kKmM]?) comments?/) ||
      combinedText.match(/([\d.,]+[kKmM]?) comments?/);

    const mediaType = permalink.includes("/reel/") ? "REEL" : "POST";

    return {
      caption,
      timestamp: time?.getAttribute("datetime") || "",
      likeCountRaw: likeMatch ? likeMatch[1] : "0",
      commentCountRaw: commentMatch ? commentMatch[1] : "0",
      mediaType
    };
  });
}

async function crawlPublicInstagram({ username, sinceDate, maxPosts }) {
  const browser = await chromium.launch({
    headless: true
  });

  const context = await browser.newContext({
    viewport: { width: 1440, height: 1400 },
    userAgent:
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"
  });

  const page = await context.newPage();
  const since = parseTimestamp(sinceDate);

  if (!since) {
    throw new Error(`Invalid since date: ${sinceDate}`);
  }

  try {
    const profileUrl = `https://www.instagram.com/${username}/`;
    await page.goto(profileUrl, { waitUntil: "domcontentloaded", timeout: 60000 });
    await page.waitForTimeout(2500);
    await closeLoginPopup(page);

    const links = await collectPostLinks(page, maxPosts);
    const posts = [];

    for (const permalink of links) {
      await page.goto(permalink, { waitUntil: "domcontentloaded", timeout: 60000 });
      await page.waitForTimeout(1500);

      const extracted = await extractPostData(page, permalink);
      const timestamp = parseTimestamp(extracted.timestamp);

      if (!timestamp) {
        continue;
      }

      if (timestamp < since) {
        continue;
      }

      const mediaId = permalink.split("/").filter(Boolean).pop();
      posts.push({
        mediaId,
        caption: extracted.caption,
        mediaType: extracted.mediaType,
        permalink,
        timestamp: toOutputTimestamp(timestamp),
        likeCount: normalizeCount(extracted.likeCountRaw),
        commentCount: normalizeCount(extracted.commentCountRaw)
      });
    }

    const totalLikeCount = posts.reduce((sum, post) => sum + post.likeCount, 0);
    const totalCommentCount = posts.reduce((sum, post) => sum + post.commentCount, 0);

    return {
      contentCount: posts.length,
      totalLikeCount,
      totalCommentCount,
      posts
    };
  } finally {
    await context.close();
    await browser.close();
  }
}

async function main() {
  const args = parseArgs(process.argv);
  const username = requireArg(args, "username");
  const sinceDate = requireArg(args, "since");
  const maxPosts = Number.parseInt(args.maxPosts || "100", 10);

  const result = await crawlPublicInstagram({
    username,
    sinceDate,
    maxPosts: Number.isNaN(maxPosts) ? 100 : maxPosts
  });

  process.stdout.write(`${JSON.stringify(result)}\n`);
}

main().catch((error) => {
  const payload = {
    error: true,
    message: error.message || "Unknown crawler error"
  };
  process.stderr.write(`${JSON.stringify(payload)}\n`);
  process.exit(1);
});
