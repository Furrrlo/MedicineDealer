<%@ page contentType="text/html;charset=UTF-8"  %>

<div class="modal" id="day_click_modal">
    <div class="modal-background"></div>
    <div class="modal-card">
        <header class="modal-card-head">
            <p class="modal-card-title" id="modal_title"></p>
            <button class="delete" aria-label="close"></button>
        </header>
        <section class="modal-card-body">

            <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth" id="medicine_table">
                <th>Nome Medicina</th>
                <th>Ora Acquisione</th>
                <th>Assunta</th>
                <th>Slot</th>
                <th>Clicca per rimuovere solo per oggi</th>
            </table>
        </section>
        <footer class="modal-card-foot"></footer>
    </div>
</div>

<script>
    const DayClickModal = (() => {
        const modal = document.getElementById("day_click_modal");
        const closeButton = modal.querySelector(".delete");

        const table = document.getElementById("medicine_table");
        const title = document.getElementById("modal_title");

        function DayClickModal() {}

        DayClickModal.open = (date, events) => {
            title.innerHTML = date;

            let i = 1;
            events.forEach(event => {
                let row = table.insertRow(i);

                let cell1 = row.insertCell(0);
                cell1.innerHTML = event.title;

                let cell2 = row.insertCell(1);
                cell2.innerHTML = event.start.getHours() + ":" + event.start.getMinutes();
                // let cell3

                let cell3 = row.insertCell(2);
                //TODO Assunta yes or no
                let cell4 = row.insertCell(3);
                //TODO Slot in which the medicine is

                let cell5 = row.insertCell(4);
                let btn = document.createElement("BUTTON");
                btn.innerHTML = "ELIMINA";
                btn.onclick = function (){

                };
                cell5.appendChild(btn);
            })

            modal.classList.add('is-active');
        };

        DayClickModal.close = () => {
            for(let n = table.rows.length - 1;n > 0;n--)
                table.deleteRow(n);
            modal.classList.remove('is-active');
        };

        // Listeners

        closeButton.addEventListener('click', () => {
            DayClickModal.close();
        });

        window.addEventListener('click', (event) => {
            if(event.target.classList.contains('modal-background'))
                DayClickModal.close();
        });

        return DayClickModal;
    })();

</script>