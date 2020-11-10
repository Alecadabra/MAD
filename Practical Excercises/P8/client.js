function showData(data)
{
    $("#displayarea").text(data);
}

$(window).on("load", function() 
{
    $("#fetchBtn").on(
        "click",
        () => {
            let url = "/data.json";
            fetch(
                url,
                { method: "GET" }
            ).then(
                response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error(response.statusText);
                    }
                }
            ).then(
                data => formatData(data)
            ).then(
                data => showData(data)
            ).catch(
                err => alert(err)
            );
        }
    );
    
    showData("No data retrieved yet");
});
