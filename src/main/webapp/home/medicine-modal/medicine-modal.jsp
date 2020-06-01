<%@ page contentType="text/html;charset=UTF-8" %>

<div id="medicine-modal" class="modal">
    <div class="modal-background"></div>
    <div class="modal-card">
        <form action="${pageContext.request.contextPath}/api/eventi">
            <header class="modal-card-head">
                <p class="modal-card-title"></p>
                <button class="delete" type="button" aria-label="close"></button>
            </header>
            <section class="modal-card-body">
                <div class="loader-wrapper">
                    <div class="loader is-loading"></div>
                </div>

                <%@ include file="form-content.jsp" %>
            </section>
            <footer class="modal-card-foot">
                <input type="submit" class="submit-bnt button is-primary is-rounded" />
            </footer>
        </form>
    </div>
</div>

<script>
    const MedicineModal = (() => {
        const modal = document.getElementById("medicine-modal");
        const closeButton = modal.querySelector(".delete");

        const form = modal.querySelector('form');
        const title = modal.querySelector('.modal-card-title');
        const submitButton = modal.querySelector('.submit-bnt');

        function MedicineModal() {}

        MedicineModal.open = event => {
            modal.classList.add('is-active');
            form.resetCustomErrors();

            if(!event) {
                event = {}
                document.getElementById('form-mode-input').value = 'add';

                title.innerText = "AGGIUNGI UNA NUOVA MEDICINA";
                submitButton.value = "AGGIUNGI FARMACO";
            } else {
                document.getElementById('form-mode-input').value = 'edit';

                title.innerText = "MODIFICA MEDICINA";
                submitButton.value = "MODIFICA FARMACO";
            }

            const portaMedicineSelect = document.getElementById('porta-medicine-container');
            const deviceId = portaMedicineSelect.options[portaMedicineSelect.selectedIndex].value;

            document.getElementById('device-id-input').value = event.id_porta_medicine || deviceId;
            document.getElementById('event-id-input').value = event.id || '';
            document.getElementById("aic-input").value = event.nome_farmaco || '';
            document.getElementById("aic-input").aic_value = event.aic_farmaco || null;
            document.getElementById("start-date-input").value = event.data || null;

            HoursController.clean();
            if(!event.orari || !event.orari.length || event.orari.length <= 0) {
                HoursController.addField();
            } else {
                event.orari.forEach(ora => {
                    const newField = HoursController.addField();
                    newField.querySelector('input').value = ora.substring(0, ora.length - 3);
                });
            }

            document.getElementById('repeat-checkbox').checked = event.cadenza || false;
            document.getElementById('repeat-checkbox').forceChange();

            document.getElementById('interval-input').value = event.cadenza && event.cadenza.intervallo ? event.cadenza.intervallo : '';
            document.getElementById('period-select').value = event.cadenza && event.cadenza.settimanale ? 'Settimanale' : 'Giornaliera';
            document.getElementById('period-select').forceChange();

            document.getElementById("monday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.lunedi === 'true';
            document.getElementById("tuesday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.martedi === 'true';
            document.getElementById("wednesday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.mercoledi === 'true';
            document.getElementById("thursday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.giovedi === 'true';
            document.getElementById("friday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.venerdi === 'true';
            document.getElementById("saturday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.sabato === 'true';
            document.getElementById("sunday").checked = event.cadenza && event.cadenza.settimanale && event.cadenza.settimanale.domenica === 'true';

            document.querySelector('input[type="radio"][name="end"][value="mai"]').checked =
                !event.cadenza || !event.cadenza.fine;

            document.querySelector('input[type="radio"][name="end"][value="date"]').checked =
                event.cadenza && event.cadenza.fine && event.cadenza.fine.data;
            document.querySelector('input[type="radio"][name="end"][value="date"]').forceChange();
            document.getElementById('end-date-input').value = event.cadenza && event.cadenza.fine && event.cadenza.fine.data ?
                event.cadenza.fine.data : '';

            document.querySelector('input[type="radio"][name="end"][value="assunzioni"]').checked =
                event.cadenza && event.cadenza.fine && event.cadenza.fine.occorenze;
            document.querySelector('input[type="radio"][name="end"][value="assunzioni"]').forceChange();
            document.getElementById('num-ass-input').value = event.cadenza && event.cadenza.fine && event.cadenza.fine.occorenze ?
                event.cadenza.fine.occorenze : '';
        };

        MedicineModal.close = () => {
            modal.classList.remove('is-active');
        };

        // Listeners

        closeButton.addEventListener('click', () => {
            MedicineModal.close();
        });

        window.addEventListener('click', (event) => {
            if(event.target.classList.contains('modal-background'))
                MedicineModal.close();
        });

        return MedicineModal;
    })();
</script>