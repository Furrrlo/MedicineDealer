<%@ page contentType="text/html; charset=UTF-8" %>

<div class="field">
    <label class="label">I giorni</label>
    <div class="field is-grouped is-grouped-multiline">
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="monday">
                Lun
            </label>
        </p>
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="tuesday">
                Mar
            </label>
        </p>
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="wednesday">
                Mer
            </label>
        </p>
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="thursday">
                Gio
            </label>
        </p>
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="friday">
                Ven
            </label>
        </p>
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="saturday">
                Sab
            </label>
        </p>
        <p class="control">
            <label class="checkbox">
                <input type="checkbox" id="sunday">
                Dom
            </label>
        </p>
    </div>
    <p class="help is-danger err"></p>
</div>

<script>
    window.addEventListener('load', function() {
        // Select at least 1 day
        let daysInput = [
            document.querySelector('input#monday'),
            document.querySelector('input#tuesday'),
            document.querySelector('input#wednesday'),
            document.querySelector('input#thursday'),
            document.querySelector('input#friday'),
            document.querySelector('input#saturday'),
            document.querySelector('input#sunday')
        ];

        daysInput.forEach(input => input.addCustomValidator(() => {
            let noneSelected = daysInput.every(element => !element.checked);

            if(noneSelected)
                return "Seleziona almeno un giorno della settimana";
            return true;
        }));
    });
</script>