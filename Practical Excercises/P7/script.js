$(window).on(
    "load",
    function() {
        const operand1Input = document.getElementById("operand1Input")
        const operand2Input = document.getElementById("operand2Input")
        
        $("#plusBtn").on(
            "click",
            function() {
                let operand1 = parseFloat($("#operand1Input").val())
                let operand2 = parseFloat($("#operand2Input").val())

                if (isNaN(operand1) || isNaN(operand2)) {
                    alert("Must enter numbers")
                } else {
                    $("#resultInput").val(operand1 + operand2)
                }
            }
        )

        $("#minusBtn").on(
            "click",
            function() {
                let operand1 = parseFloat($("#operand1Input").val())
                let operand2 = parseFloat($("#operand2Input").val())

                if (isNaN(operand1) || isNaN(operand2)) {
                    alert("Must enter numbers")
                } else {
                    $("#resultInput").val(operand1 - operand2)
                }
            }
        )

        $("#multBtn").on(
            "click",
            function() {
                let operand1 = parseFloat($("#operand1Input").val())
                let operand2 = parseFloat($("#operand2Input").val())

                if (isNaN(operand1) || isNaN(operand2)) {
                    alert("Must enter numbers")
                } else {
                    $("#resultInput").val(operand1 * operand2)
                }
            }
        )

        $("#divBtn").on(
            "click",
            function() {
                let operand1 = parseFloat($("#operand1Input").val())
                let operand2 = parseFloat($("#operand2Input").val())

                if (isNaN(operand1) || isNaN(operand2)) {
                    alert("Must enter numbers")
                } else {
                    $("#resultInput").val(operand1 / operand2)
                }
            }
        )
    }
)