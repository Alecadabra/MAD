$(window).on(
    "load",
    () => {
        $("#theButton").on(
            "click",
            () => {
                let number = parseFloat($("#theNumberField").val())

                if (isNaN(number)) {
                    alert("You must enter a number")
                } else {
                    $("#theParagraph").text(`${number * 10}`)
                }
            }
        )
    }
)