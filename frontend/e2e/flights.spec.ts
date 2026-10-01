import { test, expect } from '@playwright/test';

test('should load flights on the dashboard', async ({ page }) => {
  const flightsResponsePromise = page.waitForResponse(response =>
    response.url() === 'http://localhost:8080/api/flights' &&
    response.request().method() === 'GET'
  );

  await page.goto('/');

  const response = await flightsResponsePromise;
  expect(response.status()).toBe(200);

  await expect(
    page.getByRole('heading', { name: 'Flight Overview' })
  ).toBeVisible();

  const flightRow = page.getByRole('row').filter({
    has: page.getByRole('cell', { name: 'TX23', exact: true }),
  });

  await expect(flightRow).toBeVisible();
});

test('should create, edit and delete a flight', async ({ page, request }) => {
  const flightNumber = `E${Date.now().toString(36).toUpperCase()}`;

  try {
    await page.goto('/');

    await page.getByRole('link', {
      name: 'Add Flight',
      exact: true,
    }).click();

    await page.getByLabel('Flight Number', { exact: true })
      .fill(flightNumber);

    await page.getByLabel('Origin', { exact: true })
      .fill('Istanbul');

    await page.getByLabel('Destination', { exact: true })
      .fill('London');

    await page.getByLabel('Status', { exact: true })
      .selectOption('SCHEDULED');

    await page.getByLabel('Departure Time (local)', { exact: true })
      .fill('2026-12-20T14:30');

    await page.getByLabel('Gate', { exact: true })
      .fill('A12');

    await page.getByRole('button', {
      name: 'Add Flight',
      exact: true,
    }).click();

    await expect(
      page.getByRole('status')
        .filter({ hasText: 'Flight added successfully.' })
    ).toBeVisible();

    await page.getByRole('link', {
      name: 'Back to dashboard',
    }).click();

    await page.getByLabel('Search by Flight Number')
      .fill(flightNumber);

    const flightRow = page.getByRole('row').filter({
      has: page.getByRole('cell', {
        name: flightNumber,
        exact: true,
      }),
    });

    await expect(flightRow).toBeVisible();
    await expect(
      flightRow.getByRole('cell', { name: 'London', exact: true })
    ).toBeVisible();

       await flightRow.getByRole('link', {
  name: 'Edit',
  exact: true,
}).click();

// Uçuş verisi yüklenip form doldurulana kadar bekle.
await expect(
  page.getByLabel('Destination', { exact: true })
).toHaveValue('London');

await expect(
  page.getByLabel('Flight Number', { exact: true })
).toBeDisabled();

await page.getByLabel('Destination', { exact: true })
  .fill('Paris');

await page.getByLabel('Status', { exact: true })
  .selectOption('DELAYED');

await page.getByLabel('Gate', { exact: true })
  .fill('B10');

await page.getByRole('button', {
  name: 'Save Changes',
  exact: true,
}).click();

await expect(
  page.getByRole('status')
    .filter({ hasText: 'Flight updated successfully.' })
).toBeVisible();

await page.getByRole('link', {
  name: 'Back to dashboard',
}).click();

await page.getByLabel('Search by Flight Number')
  .fill(flightNumber);

await expect(
  flightRow.getByRole('cell', { name: 'Paris', exact: true })
).toBeVisible();

await expect(
  flightRow.getByRole('cell', { name: 'DELAYED', exact: true })
).toBeVisible();

await expect(
  flightRow.getByRole('cell', { name: 'B10', exact: true })
).toBeVisible();

// Delete tıklanınca açılacak onay penceresini kabul et.
page.once('dialog', async dialog => {
  await dialog.accept();
});

await flightRow.getByRole('button', {
  name: 'Delete',
  exact: true,
}).click();

await expect(flightRow).toHaveCount(0);

// Sayfayı yenileyerek kaydın backend'den de silindiğini kontrol et.
await page.reload();

await page.getByLabel('Search by Flight Number')
  .fill(flightNumber);

await expect(
  page.getByText('No flights match your filters.', { exact: true })
).toBeVisible();

await expect(flightRow).toHaveCount(0);


  } finally {
    const cleanupResponse = await request.delete(
      `http://localhost:8080/api/flights/${encodeURIComponent(flightNumber)}`
    );

    expect([204, 404]).toContain(cleanupResponse.status());
  }
});