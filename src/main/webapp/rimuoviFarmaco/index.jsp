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
</head>

<body>
<%@ include file="/navbar.jsp"%>
<%@ include file="/login_modal.jsp"%>
<br>

<div class="container">
    <div class="columns">
        <div class="section">
            <div class="column">
                <h1 class="subtitle">Questi sono tutti i farmaci legati al portamedicine</h1>

                <%-- Row template --%>
                <table class="is-hidden">
                    <tr class="medicine-row-template">
                        <td class="medicine-name"></td>
                        <td class="has-text-centered">
                            <button class="delete-button button">ELIMINA</button>
                        </td>
                    </tr>
                </table>
                <%-- Container--%>
                <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth" id="medicine_table">
                    <thead>
                    <tr>
                        <th>Nome Medicina</th>
                        <th>Clicca per rimuovere</th>
                    </tr>
                    </thead>
                    <tbody id="medicine-table-body">

                    </tbody>
                </table>
            </div>

            <div class="control">
                <button id="home-btn" class="button is-rounded is-fullwidth">TORNA ALLA HOME</button>
            </div>
        </div>
    </div>

</div>

<script>
    window.addEventListener('load',function () {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const deviceID = urlParams.get("id_porta_medicine");

        document.getElementById('home-btn').addEventListener('click', () => {
            location.href = "${pageContext.request.contextPath}/home/?id_porta_medicine=" + deviceID;
        });

        const path = '${pageContext.request.contextPath}/api/farmaci/?id_porta_medicine=' + deviceID;
        fetch(path).then(async response =>{
            if(!response.ok)
                throw response.status + ":" + (await response.text());
            return JXON.stringToJs(await response.text());
        }).then(medicines => {
            const medicine = medicines.medicine.medicina;
            if(!medicine)
                return [];
            if(!medicine.forEach)
                return [medicine];
            return medicine;
        }).then(medicines => {
            printMedicines(medicines)
        }).catch(ex => {
            console.error(ex);
        });

        function printMedicines(medicines) {
            const container = document.getElementById('medicine-table-body');
            const templateClass = 'medicine-row-template';
            const template = document.querySelector('.' + templateClass);

            medicines.forEach(medicine => {
                const row = template.cloneNode(true);
                row.classList.remove(templateClass);

                container.appendChild(row);

                row.querySelector('.medicine-name').innerHTML = medicine.name;
                row.querySelector('.delete-button').addEventListener('click', () => {

                    const path = '${pageContext.request.contextPath}/api/eventi/?'
                        + 'id_porta_medicine=' + deviceID + '&'
                        + 'aic_farmaco=' + medicine.aic_farmaco;
                    fetch(path, {
                        method: 'DELETE'
                    }).then(async response => {

                        if(response.status === 401) {
                            LoginModal.open();
                            throw response.status + ": " + (await response.text());
                        }

                        if(!response.ok)
                            throw response.status + ":" + (await response.text());
                        container.removeChild(row);

                    }).catch(ex => {
                        console.error(ex);
                    });
                });
            });
        }
    });
</script>

</body>
</html>