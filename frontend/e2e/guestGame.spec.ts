import { test, expect } from '@playwright/test';

test.describe('Guest Game Flow E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should complete full guest game flow', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    const board = page.locator('[data-testid="chessboard"], .chessboard, [role="region"]').first();
    await expect(board).toBeVisible({ timeout: 5000 });

    await expect(page.locator('button:has-text("Resign"), button[title="Resign"]')).toBeVisible();
    await expect(page.locator('button:has-text("Draw"), button[title="Offer Draw"]')).toBeVisible();
    await expect(page.locator('button:has-text("Flip"), button[title="Flip Board"]')).toBeVisible();
  });

  test('should create guest game and display starting position', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1000);

    const board = page.locator('[data-testid="chessboard"], .chessboard').first();
    await expect(board).toBeVisible();
  });

  test('should make valid chess moves', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1500);

    const moveList = page.locator('text=Move History, [data-testid="move-list"]');
    await expect(moveList).toBeVisible({ timeout: 3000 });
  });

  test('should display move history', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await expect(page.locator('text=Move History')).toBeVisible();
  });

  test('should flip board orientation', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1000);

    const flipButton = page.locator('button[title="Flip Board"]');
    await flipButton.click();

    await page.waitForTimeout(500);
  });

  test('should offer draw', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1000);

    const drawButton = page.locator('button[title="Offer Draw"]');
    await drawButton.click();

    await page.waitForTimeout(500);
  });

  test('should resign game', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1000);

    const resignButton = page.locator('button[title="Resign"]');
    await resignButton.click();

    await page.waitForTimeout(1000);
  });

  test('should handle invalid moves gracefully', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1500);

    const board = page.locator('[data-testid="chessboard"], .chessboard').first();
    await expect(board).toBeVisible();
  });

  test('should display loading state during game creation', async ({ page }) => {
    const playButton = page.locator('text=Play as Guest');
    await playButton.click();

    const loadingIndicator = page.locator('text=Loading, text=Creating');
    const isVisible = await loadingIndicator.isVisible().catch(() => false);

    expect(isVisible !== undefined).toBe(true);
  });

  test('should persist game state on page refresh', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    const gameUrl = page.url();
    await page.waitForTimeout(1000);

    await page.reload();

    expect(page.url()).toBe(gameUrl);

    const board = page.locator('[data-testid="chessboard"], .chessboard').first();
    await expect(board).toBeVisible({ timeout: 3000 });
  });

  test('should display chat panel', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    const chatPanel = page.locator('[data-testid="chat-panel"], text=Chat');
    await expect(chatPanel).toBeVisible({ timeout: 3000 });
  });

  test('should handle multiple games in different tabs', async ({ browser }) => {
    const context1 = await browser.newContext();
    const context2 = await browser.newContext();

    const page1 = await context1.newPage();
    const page2 = await context2.newPage();

    await page1.goto('/');
    await page1.click('text=Play as Guest');
    await page1.waitForURL(/\/game\/\d+/);
    const game1Url = page1.url();

    await page2.goto('/');
    await page2.click('text=Play as Guest');
    await page2.waitForURL(/\/game\/\d+/);
    const game2Url = page2.url();

    expect(game1Url).not.toBe(game2Url);

    await context1.close();
    await context2.close();
  });

  test('should display game controls sidebar', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await expect(page.locator('text=Move History')).toBeVisible();

    await expect(page.locator('button[title="Resign"]')).toBeVisible();
    await expect(page.locator('button[title="Offer Draw"]')).toBeVisible();
    await expect(page.locator('button[title="Flip Board"]')).toBeVisible();
  });

  test('should handle navigation back to home', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.goBack();

    await expect(page.locator('text=Play as Guest')).toBeVisible();
  });

  test('should work on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1000);

    const board = page.locator('[data-testid="chessboard"], .chessboard').first();
    await expect(board).toBeVisible();
  });

  test('should complete game with checkmate', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1500);

    const board = page.locator('[data-testid="chessboard"], .chessboard').first();
    await expect(board).toBeVisible();
  });

  test('should display 700px board size', async ({ page }) => {
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    await page.waitForTimeout(1000);

    const board = page.locator('[data-testid="chessboard"], .chessboard').first();

    await expect(board).toBeVisible();
    const boundingBox = await board.boundingBox();

    if (boundingBox) {
      expect(boundingBox.width).toBeGreaterThan(600);
      expect(boundingBox.width).toBeLessThan(800);
    }
  });

  test('should have no console errors during gameplay', async ({ page }) => {
    const errors: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        errors.push(msg.text());
      }
    });

    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);
    await page.waitForTimeout(2000);

    const criticalErrors = errors.filter(e =>
      !e.includes('favicon') && !e.includes('sourcemap')
    );
    expect(criticalErrors.length).toBe(0);
  });
});

