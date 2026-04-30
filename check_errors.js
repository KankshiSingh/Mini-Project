const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();
  
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', error => console.log('PAGE ERROR:', error.message, error.stack));
  
  try {
    await page.goto('http://localhost:4200', {waitUntil: 'networkidle0', timeout: 10000});
  } catch(e) {
    console.log('Error navigating:', e.message);
  }
  
  await browser.close();
})();
