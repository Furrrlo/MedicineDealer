<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Benvenuto in MEDICINE DEALER</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script defer src="${pageContext.request.contextPath}/node_modules/@fortawesome/fontawesome-free/js/all.min.js"></script>

    <script defer src="${pageContext.request.contextPath}/node_modules/jxon/jxon.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/validate.js"></script>

    <script>
        function home(){ location.href = "${pageContext.request.contextPath}/home/?id_porta=" + deviceID;}
    </script>
</head>

<body>
<%@ include file="/navbar.jsp"%>

<br>

<div class="container">
    <div class="columns">
        <div class="section">
            <div class="column">
                <h1 class="subtitle">Questi sono tutti i farmaci legati al portamedicine</h1>

                <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth" id="medicine_table">
                    <th>Nome Medicina</th>
                    <th>Clicca per rimuovere</th>
                </table>
            </div>

            <div class="control">
                <button class="button is-rounded is-fullwidth" onclick="home()">TORNA ALLA HOME</button>
            </div>
        </div>
    </div>

</div>

<script>
    let deviceID = null;

    window.addEventListener('load',function () {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        deviceID = urlParams.get("id_porta");

        const path = '${pageContext.request.contextPath}/api/farmaci/?id_porta_medicine='
            + deviceID;

        fetch(path).then(async response =>{
            if(!response.ok)
                throw response.status + ":" + (await response.text());
            return JXON.stringToJs(await response.text());
        }).then(medicines => {
            let medicine = medicines.medicine.medicina;
            if(medicine.forEach){
                printMedicines(medicine)
            }else{
                printMedicines([medicine]);
            }
            console.log(medicines);
        }).catch(ex => {
            console.error(ex);
        })
    })

    function printMedicines(medicines) {
        const table = document.getElementById("medicine_table");
        let i = 1;

        medicines.forEach(medicine => {
            let row = table.insertRow(i);

            let cell1 = row.insertCell(0);
            cell1.innerHTML = medicine.name;

            let cell2 = row.insertCell(1);
            let btn = document.createElement("BUTTON");
            btn.innerHTML = "ELIMINA";
            btn.id = medicine.aic_farmaco;
            cell2.appendChild(btn);
            btn.onclick = function (){
                let aic = btn.id;
                const path = '${pageContext.request.contextPath}/api/farmaci/?aic='
                    + aic;
                fetch(path,{
                    method: 'DELETE'
                }).then(async response => {
                    if(!response.ok)
                        throw response.status + ":" + (await response.text());
                }).catch(ex => {
                    console.error(ex);
                })

            }
        })
    }
</script>

</body>
</html>