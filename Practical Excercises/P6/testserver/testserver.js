const PORT = 8000;
const CHUNK_SIZE = 10;     // Send X bytes at a time
const CHUNK_DELAY = 20;    // Wait Y ms between sending chunks
const IGNORE_URL = false;   // If true, return data regardless of whether the URL is correct.
const CORRUPTION_PROB = 0; // Probability (0-1) that the returned content will be corrupted.

const REQUIRED_PATH = "/testwebservice/rest";
const REQUIRED_PARAMS = {
    'method': 'thedata.getit',
    'api_key': '01189998819991197253',
    'format': 'json'
};

// The server will send the following, converted into JSON. (FYI, since this is Javascript, the 
// JSON will look the same as the original JavaScript code.)
content = {
    "factions": [
        { 
            "name": "Covenant", 
            "strength": 100, 
            "relationship": "enemy" 
        },
        {
            "name": "Sirius Cybernetics Corporation, Complaints Division", 
            "strength": 50, 
            "relationship": "neutral" 
        },
        { 
            "name": "Twelve Colonies", 
            "strength": 25, 
            "relationship": "ally" 
        }
    ]
};



// --- Server Logic ---

const https = require('https');
const fs = require('fs');
const os = require('os');
const url = require('url');

const options = {
    key: fs.readFileSync('key.pem'),
    cert: fs.readFileSync('cert.crt')
};

function requestHandler(request, response)
{
    console.log(`Request from ${request.socket.remoteAddress} -- ${request.method} ${request.url}`);
    
    const reqUrl = new url.URL(`https://localhost${request.url}`);
    console.log(`  * path = ${reqUrl.pathname}, parameters = ${reqUrl.searchParams}`);
    
    // Ensure the request is correct.
    let errors = [];
    if(IGNORE_URL)
    {
        console.log("  * ignoring path and parameters (IGNORE_URL == true)")
    }
    else
    {
        if(reqUrl.pathname === REQUIRED_PATH)
        {
            console.log("  * correct path");
        }
        else
        {
            errors.push("wrong path"); 
            console.log("  * wrong path -- rejecting request!");
        }
        
        for(let p in REQUIRED_PARAMS)
        {
            if(!reqUrl.searchParams.has(p))
            {
                errors.push(`missing '${p}' value`);
                console.log(`  * missing '${p}' value -- rejecting request!`);
            }
            else if(reqUrl.searchParams.get(p) !== REQUIRED_PARAMS[p])
            {
                errors.push(`wrong '${p}' value`);
                console.log(`  * incorrect '${p}' value -- rejecting request!`);
            }
            else
            {
                console.log(`  * correct '${p}' value`);
            }
        }
    }
        
    if(errors.length === 0)
    {        
        // No errors found in the request, so return the data.
        let textContent = JSON.stringify(content);
        
        if(Math.random() < CORRUPTION_PROB)
        {
            console.log('  * result corrupted');
            
            // Corrupt the return result by picking and replacing a bracket, brace or quote.            
            const candidateIndices = []
            for(let i = 0; i < textContent.length; i++)
            {
                if(/[\[\]{}"]/.test(textContent.charAt(i)))
                {
                    candidateIndices.push(i);
                }
            }
            
            const index = candidateIndices[Math.floor(Math.random() * candidateIndices.length)];
            textContent = textContent.substring(0, index) + "@@@@@" + 
                          textContent.substring(index + 1);
        }
            
        response.writeHead(200, {
            'Content-Length': textContent.length,
            'Content-Type': 'text/plain'
        });
        
        respondIncrementally(textContent, response);
    }
    else
    {
        // Something was wrong in the request, so behave as if we couldn't find the data.
        response.writeHead(200, {
            'Content-Type': 'text/plain'
        });
        response.end("404 - not found\n\n" + errors.join('\n'));
    }
    
    console.log("");
}

function respondIncrementally(partialContent, response)
{
    if(partialContent.length <= CHUNK_SIZE)
    {
        response.end(partialContent);
    }
    else
    {
        response.write(partialContent.substring(0, CHUNK_SIZE));
        setTimeout(respondIncrementally, CHUNK_DELAY, 
                   partialContent.substring(CHUNK_SIZE), response);
    }
}

const server = https.createServer(options, requestHandler);

server.listen(PORT, (err) => {
    if(err) 
    {
        console.log('Server failed to start: ', err);
    }
    else
    {
        // Find the machine's externally-visible IP address(es), for display purposes only.
        const addresses = []
        const interfaces = os.networkInterfaces();
        for(let intf in interfaces)
        {
            const addressList = interfaces[intf]
            for(let i = 0; i < addressList.length; i++)
            {
                if(!addressList[i].internal)
                {
                    addresses.push(addressList[i].address);
                }
            }
        }
                
        console.log(`HTTPS server, listening on port ${PORT}, at IPs ${addresses.join(", ")} (plus loopback)`);
        console.log(`Artificial slow-down: sending ${CHUNK_SIZE} byte(s) at a time, with ${CHUNK_DELAY}ms delay between them`);
        console.log(`Corruption probability = ${CORRUPTION_PROB}`);
    }
});
