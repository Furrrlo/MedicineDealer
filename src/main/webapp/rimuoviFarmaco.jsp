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
        function home(){ location.href = "home.html";}
    </script>
</head>

<body>
<section class="hero is-primary">
    <div class="hero-body">
        <div class="container">
            <h1 class="title is-1" >MEDICINE DEALER</h1>
            <h2 class="subtitle">GESTISCI L'ACQUISIZIONE DI MEDICINALI CON SEMPLICITA'</h2>
        </div>
    </div>
</section>

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
</body>
</html>