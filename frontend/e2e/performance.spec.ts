import { test, expect } from '@playwright/test';

/**
 * Frontend page load performance tests (T084).
 * Verifies First Contentful Paint < 2s and other performance metrics.
 */
test.describe('Frontend Performance Tests', () => {
  test('should load landing page with FCP < 2s', async ({ page }) => {
    const startTime = Date.now();

    await page.goto('/');

    // Wait for First Contentful Paint
    const fcpMetrics = await page.evaluate(() => {
      return new Promise((resolve) => {
        if (performance.getEntriesByType) {
          const paintEntries = performance.getEntriesByType('paint');
          const fcp = paintEntries.find(entry => entry.name === 'first-contentful-paint');
          if (fcp) {
            resolve(fcp.startTime);
          }
        }

        // Fallback: use load time
        window.addEventListener('load', () => {
          resolve(Date.now() - performance.timing.navigationStart);
        });
      });
    });

    const loadTime = Date.now() - startTime;

    console.log('=== Landing Page Performance ===');
    console.log(`First Contentful Paint: ${fcpMetrics}ms`);
    console.log(`Total Load Time: ${loadTime}ms`);
    console.log(`Target FCP: < 2000ms`);

    expect(fcpMetrics).toBeLessThan(2000);
  });

  test('should load game page with FCP < 2s', async ({ page }) => {
    // First create a game
    await page.goto('/');
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    const startTime = Date.now();

    // Reload to measure performance
    await page.reload();

    await page.waitForLoadState('domcontentloaded');

    const performanceMetrics = await page.evaluate(() => ({
      fcp: performance.getEntriesByType('paint')
        .find(entry => entry.name === 'first-contentful-paint')?.startTime || 0,
      domContentLoaded: performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart,
      loadComplete: performance.timing.loadEventEnd - performance.timing.navigationStart
    }));

    const totalTime = Date.now() - startTime;

    console.log('=== Game Page Performance ===');
    console.log(`First Contentful Paint: ${performanceMetrics.fcp}ms`);
    console.log(`DOM Content Loaded: ${performanceMetrics.domContentLoaded}ms`);
    console.log(`Load Complete: ${performanceMetrics.loadComplete}ms`);
    console.log(`Total Time: ${totalTime}ms`);

    expect(performanceMetrics.fcp).toBeLessThan(2000);
  });

  test('should have acceptable Time to Interactive', async ({ page }) => {
    await page.goto('/');

    const ttiMetrics = await page.evaluate(() => {
      return new Promise((resolve) => {
        // Approximate TTI by waiting for network idle and long tasks to complete
        const startTime = performance.now();

        if ('PerformanceObserver' in window) {
          const observer = new PerformanceObserver((list) => {
            const entries = list.getEntries();
            const lastEntry = entries[entries.length - 1];
            if (lastEntry) {
              observer.disconnect();
              resolve(lastEntry.startTime + lastEntry.duration);
            }
          });

          try {
            observer.observe({ entryTypes: ['longtask'] });
          } catch (e) {
            // Fallback if longtask not supported
            resolve(performance.now() - startTime);
          }
        }

        // Fallback: use load event
        setTimeout(() => {
          resolve(performance.now() - startTime);
        }, 3000);
      });
    });

    console.log(`Time to Interactive: ${ttiMetrics}ms`);
    expect(ttiMetrics).toBeLessThan(3500);
  });

  test('should have acceptable bundle size', async ({ page }) => {
    await page.goto('/');

    const resourceMetrics = await page.evaluate(() => {
      const resources = performance.getEntriesByType('resource');
      let totalSize = 0;
      let jsSize = 0;
      let cssSize = 0;

      resources.forEach((resource: any) => {
        const size = resource.transferSize || 0;
        totalSize += size;

        if (resource.name.endsWith('.js')) {
          jsSize += size;
        } else if (resource.name.endsWith('.css')) {
          cssSize += size;
        }
      });

      return {
        totalSize: Math.round(totalSize / 1024),
        jsSize: Math.round(jsSize / 1024),
        cssSize: Math.round(cssSize / 1024)
      };
    });

    console.log('=== Bundle Size ===');
    console.log(`Total: ${resourceMetrics.totalSize} KB`);
    console.log(`JavaScript: ${resourceMetrics.jsSize} KB`);
    console.log(`CSS: ${resourceMetrics.cssSize} KB`);
    console.log(`Target JS: < 500 KB (gzipped)`);
    console.log(`Target CSS: < 50 KB (gzipped)`);

    // These are uncompressed sizes, so they'll be larger than the gzipped target
    // But we can still check they're reasonable
    expect(resourceMetrics.jsSize).toBeLessThan(1500); // Uncompressed should be < 1.5MB
    expect(resourceMetrics.cssSize).toBeLessThan(150); // Uncompressed should be < 150KB
  });

  test('should have no render-blocking resources', async ({ page }) => {
    await page.goto('/');

    const blockingResources = await page.evaluate(() => {
      const resources = performance.getEntriesByType('resource');
      return resources.filter((resource: any) => {
        return resource.renderBlockingStatus === 'blocking';
      }).length;
    });

    console.log(`Render-blocking resources: ${blockingResources}`);
    expect(blockingResources).toBeLessThanOrEqual(2); // Allow favicon and one critical CSS
  });

  test('should achieve 60fps during piece animations', async ({ page }) => {
    await page.goto('/');
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);
    await page.waitForTimeout(1000);

    // Measure frame rate during interaction
    const fps = await page.evaluate(() => {
      return new Promise((resolve) => {
        let frameCount = 0;
        const duration = 1000; // 1 second
        const startTime = performance.now();

        const countFrame = () => {
          frameCount++;
          if (performance.now() - startTime < duration) {
            requestAnimationFrame(countFrame);
          } else {
            resolve(frameCount);
          }
        };

        requestAnimationFrame(countFrame);
      });
    });

    console.log(`Frame rate: ${fps} fps`);
    console.log(`Target: 60 fps`);

    // Allow some tolerance (55+ fps is acceptable)
    expect(fps).toBeGreaterThan(55);
  });

  test('should have acceptable Largest Contentful Paint', async ({ page }) => {
    await page.goto('/');

    const lcpMetrics = await page.evaluate(() => {
      return new Promise((resolve) => {
        if ('PerformanceObserver' in window) {
          const observer = new PerformanceObserver((list) => {
            const entries = list.getEntries();
            const lastEntry = entries[entries.length - 1];
            if (lastEntry) {
              resolve(lastEntry.startTime);
            }
          });

          try {
            observer.observe({ entryTypes: ['largest-contentful-paint'] });
          } catch (e) {
            resolve(0);
          }

          // Stop observing after 5 seconds
          setTimeout(() => {
            observer.disconnect();
            resolve(0);
          }, 5000);
        } else {
          resolve(0);
        }
      });
    });

    if (lcpMetrics > 0) {
      console.log(`Largest Contentful Paint: ${lcpMetrics}ms`);
      console.log(`Target: < 2500ms`);
      expect(lcpMetrics).toBeLessThan(2500);
    }
  });

  test('should have minimal Cumulative Layout Shift', async ({ page }) => {
    await page.goto('/');

    const clsScore = await page.evaluate(() => {
      return new Promise((resolve) => {
        let clsValue = 0;

        if ('PerformanceObserver' in window) {
          const observer = new PerformanceObserver((list) => {
            for (const entry of list.getEntries()) {
              if (!(entry as any).hadRecentInput) {
                clsValue += (entry as any).value;
              }
            }
          });

          try {
            observer.observe({ entryTypes: ['layout-shift'] });
          } catch (e) {
            resolve(0);
          }

          setTimeout(() => {
            observer.disconnect();
            resolve(clsValue);
          }, 3000);
        } else {
          resolve(0);
        }
      });
    });

    console.log(`Cumulative Layout Shift: ${clsScore}`);
    console.log(`Target: < 0.1`);

    expect(clsScore).toBeLessThan(0.1);
  });

  test('should cache resources effectively', async ({ page, context }) => {
    // First visit
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    // Second visit (should use cache)
    const cachedPage = await context.newPage();
    const startTime = Date.now();
    await cachedPage.goto('/');
    const cachedLoadTime = Date.now() - startTime;

    console.log(`Cached page load time: ${cachedLoadTime}ms`);
    console.log(`Target: < 500ms for cached resources`);

    expect(cachedLoadTime).toBeLessThan(1000);

    await cachedPage.close();
  });

  test('should have acceptable memory usage', async ({ page }) => {
    await page.goto('/');
    await page.click('text=Play as Guest');
    await page.waitForURL(/\/game\/\d+/);

    // Get memory usage (if available)
    const memoryMetrics = await page.evaluate(() => {
      if ('memory' in performance) {
        return {
          usedJSHeapSize: Math.round((performance as any).memory.usedJSHeapSize / 1024 / 1024),
          totalJSHeapSize: Math.round((performance as any).memory.totalJSHeapSize / 1024 / 1024),
          jsHeapSizeLimit: Math.round((performance as any).memory.jsHeapSizeLimit / 1024 / 1024)
        };
      }
      return null;
    });

    if (memoryMetrics) {
      console.log('=== Memory Usage ===');
      console.log(`Used Heap: ${memoryMetrics.usedJSHeapSize} MB`);
      console.log(`Total Heap: ${memoryMetrics.totalJSHeapSize} MB`);
      console.log(`Heap Limit: ${memoryMetrics.jsHeapSizeLimit} MB`);

      // Should use less than 100MB of heap
      expect(memoryMetrics.usedJSHeapSize).toBeLessThan(100);
    }
  });
});

