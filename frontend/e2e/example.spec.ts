import { test, expect } from '@playwright/test';

test.describe('Application Health Check', () => {
  test('should load the homepage', async ({ page }) => {
    await page.goto('/');

    // Wait for React to render
    await page.waitForLoadState('networkidle');

    // Check that the page title is present
    await expect(page).toHaveTitle(/Checkmate|Chess/i);

    // Check that we can see some content
    const body = page.locator('body');
    await expect(body).toBeVisible();
  });

  test('should have accessible navigation', async ({ page }) => {
    await page.goto('/');

    // Check for proper heading structure
    const heading = page.locator('h1, h2').first();
    await expect(heading).toBeVisible();
  });
});

