const fs = require('fs');
const path = require('path');

function removeEmojis(str) {
    // Regex matches most emojis
    return str.replace(/[\p{Emoji_Presentation}\p{Extended_Pictographic}]/gu, '');
}

function processDirectory(directory) {
    const files = fs.readdirSync(directory);
    for (const file of files) {
        const fullPath = path.join(directory, file);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (fullPath.endsWith('.ts') || fullPath.endsWith('.html')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            const noEmojiContent = removeEmojis(content);
            if (content !== noEmojiContent) {
                fs.writeFileSync(fullPath, noEmojiContent, 'utf8');
                console.log(`Updated ${fullPath}`);
            }
        }
    }
}

processDirectory(path.join(__dirname, 'src'));
console.log('Finished removing emojis.');
