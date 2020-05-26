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

<section class="section">
    <div class="columns is-centered">
        <div class="column is-half">
            <%@ include file="form.jsp" %>
        </div>
    </div>
</section>

</body>
</html>