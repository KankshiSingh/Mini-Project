const jsdom = require("jsdom");
const { JSDOM, VirtualConsole } = jsdom;

process.on('unhandledRejection', r => console.log('Unhandled Promise Rejection:', r));

const virtualConsole = new VirtualConsole();
virtualConsole.on("error", (...args) => { console.log("ERROR:", ...args); });

JSDOM.fromURL("http://localhost:4200", {
  runScripts: "dangerously",
  virtualConsole,
  resources: "usable",
  pretendToBeVisual: true
}).then(dom => {
  setTimeout(() => {
    console.log("Body length:", dom.window.document.body.innerHTML.length);
  }, 5000);
});
