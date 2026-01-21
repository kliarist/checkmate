import { test, expect } from '@playwright/test';

test.describe('Application Health Check', () => {
  test('should load the homepage', async ({ page }) => {
    await page.goto('/');

    await page.waitForLoadState('networkidle');

    await expect(page).toHaveTitle(/Checkmate|Chess/i);

    const body = page.locator('body');
    await expect(body).toBeVisible();
  });

  test('should have accessible navigation', async ({ page }) => {
    await page.goto('/');

    const heading = page.locator('h1, h2').first();
    await expect(heading).toBeVisible();
  });
});

