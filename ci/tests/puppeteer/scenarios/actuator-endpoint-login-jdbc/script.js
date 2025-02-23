const cas = require('../../cas.js');
const assert = require('assert');

(async () => {
    let value = `casuser:pa$$word`;
    let buff = Buffer.alloc(value.length, value);
    let authzHeader = `Basic ${buff.toString('base64')}`;
    console.log(`Authorization header: ${authzHeader}`);

    const url = "https://localhost:8443/cas/actuator/health";
    let body = await cas.doRequest(url, "GET",
        {
            'Authorization': authzHeader,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36'
        }, 200);
    let json = JSON.parse(body);
    console.dir(json, {depth: null, colors: true});

    assert(json.status !== undefined);
    assert(json.components.memory.details.freeMemory !== undefined);
    assert(json.components.memory.details.totalMemory !== undefined);

})();

