/* eslint-disable @typescript-eslint/no-var-requires */
const fs = require('fs');
const { resolve } = require('path');
const propertiesReader = require('properties-reader');

function compileSinglePropertiesFile2JS(filename) {
    if (!/\.properties/g.test(filename)) {
        return;
    }
    
    const filePrefix = filename.split('.')[0].replace('show_', '');

    let content = '';

    const properties = propertiesReader(resolve(__dirname, `../../../i18n/${filename}`));

    properties.each((key, value) => {
        content += `    '${key}': '${value}',\n`;
    });

    content = `var Store = {
  i18n : {
${content}
  }}
window.Store = Store;`

    fs.writeFileSync(
        resolve(
            __dirname,
            `${filePrefix}.js`
        ),
        content
    );
}
function compilePropertiesFiles2JS() {
    fs.readdirSync(resolve(__dirname, '../../../i18n'))
        .forEach(file => compileSinglePropertiesFile2JS(file));
}

compilePropertiesFiles2JS();
