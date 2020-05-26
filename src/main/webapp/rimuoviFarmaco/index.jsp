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
        function home(){ location.href = "${pageContext.request.contextPath}/home";}
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

                <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
                    <th>Nome Medicina</th>
                    <th>Clicca per rimuovere</th>

                    <tr>
                        <td>Banana</td>
                        <td><div class="control">
                            <button class="buttonis-fullwidth">X</button>
                        </div></td>
                    </tr>

                    <tr>
                        <td>Mela</td>
                        <td><div class="control">
                            <button class="buttonis-fullwidth">X</button>
                        </div></td>
                    </tr>

                    <tr>
                        <td>kiwi</td>
                        <td><div class="control">
                            <button class="buttonis-fullwidth">X</button>
                        </div></td>
                    </tr>
                </table>
            </div>

            <div class="control">
                <button class="button is-rounded is-fullwidth" onclick="home()">TORNA ALLA HOME</button>
            </div>
        </div>
    </div>

</div>

<script>
    window.addEventListener('load',function () {
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        let deviceID = urlParams.get("id_porta_medicine");

        fetch('${pageContext.request.contextPath}/api/farmaci&id_porta_medicine='
            + deviceID).then(async response =>{
            if(!response.ok)
                throw response.status + ":" + (await response.text());
        }).then(medicines => {
            console.log(medicines);
        }).catch(ex => {
            console.error(ex);
        })
    })
</script>

</body>
</html>