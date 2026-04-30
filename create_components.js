const fs = require('fs');
const path = require('path');

const components = ['dashboard', 'book-listing', 'cart', 'chatbot', 'add-book', 'profile', 'login', 'register'];

components.forEach(c => {
  const dir = path.join('d:/book-revive-frontend/src/app/pages', c);
  fs.mkdirSync(dir, { recursive: true });
  
  const classNameWords = c.split('-').map(w => w.charAt(0).toUpperCase() + w.slice(1));
  const className = classNameWords.join('') + 'Component';
  
  const content = `import { Component } from '@angular/core';

@Component({
  selector: 'app-${c}',
  standalone: true,
  template: '<p>${c} works!</p>'
})
export class ${className} {}
`;

  fs.writeFileSync(path.join(dir, `${c}.component.ts`), content);
});

console.log('Successfully created all components.');
