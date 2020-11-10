const PORT = 8000;

const http = require('http');
const fs = require('fs');
const os = require('os');
const url = require('url');

const contentTypes = {
    "/client.html":  "text/html",
    "/client.css":   "text/css",
    "/client.js":    "application/javascript",
    "/formatter.js": "application/javascript",
    "/data.json":    "application/json"
};

function requestHandler(request, response)
{
    console.log(`Request from ${request.socket.remoteAddress} -- ${request.method} ${request.url}`);
        
    const contentType = contentTypes[request.url];
    let fileContent = null;
    if(contentType)
    {
        try
        {
            fileContent = fs.readFileSync("./" + request.url);
        }
        catch(err) 
        {
            console.log(`Error reading '${request.url}': ${err}`);
        }
    }
    else
    {
        console.log(`Unknown resource`);
    }
    
    if(fileContent)
    {    
        console.log('Sending file contents');
        response.writeHead(200, {
            'Content-Length': fileContent.length,
            'Content-Type': contentType
        });        
        response.end(fileContent);
    }
    else
    {
        console.log('Sending 404 message');
        response.writeHead(404, {
            'Content-Type': 'text/plain'
        });
        response.end("404 - not found");
    }
    
    console.log("");
}


const server = http.createServer(requestHandler);

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
                
        console.log(`HTTP server, listening on port ${PORT}, at IPs ${addresses.join(", ")} (plus loopback)`);
    }
});
