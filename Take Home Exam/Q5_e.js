$(window).on(
    "load",
    () => {
        $("#theButton").on(
            "click",
            () => {
                fetch(
                    "/data",
                    { method: "GET" }
                ).then(
                    response => {
                        if (response.ok) {
                            return response.text();
                        } else {
                            throw new Error(response.statusText);
                        }
                    }
                ).then(
                    data => {
                        $("#theNumberField").text(data);
                    }
                ).catch(
                    err => alert(err)
                );
            }
        );
    }
);