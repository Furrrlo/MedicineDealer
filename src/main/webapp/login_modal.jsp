<%@ page contentType="text/html;charset=UTF-8" %>

<div id="login-modal" class="modal">
    <div class="modal-background"></div>
    <div class="modal-card">
        <header class="modal-card-head">
            <p class="modal-card-title" id="modal_title">
                Sessione scaduta
            </p>
        </header>
        <section class="modal-card-body">
            <div class="content">
                La tua sessione è scaduta, devi rieffettuare l'autenticazione.
            </div>
        </section>
        <footer class="modal-card-foot">
            <button class="button is-success">Vai alla pagina di login</button>
        </footer>
    </div>
</div>

<script>
    const LoginModal = (() => {

        const modal = document.getElementById('login-modal');
        const loginButton = modal.querySelector('button.button');

        function LoginModal() {}

        LoginModal.open = () => {
            modal.classList.add('is-active');
        };

        loginButton.addEventListener('click', () => {
            location.href = "${pageContext.request.contextPath}/login";
        });

        return LoginModal;
    })();
</script>