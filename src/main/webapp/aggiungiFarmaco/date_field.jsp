<%@ page contentType="text/html; charset=UTF-8" %>

<div class="field">
    <label class="label">Quando devi prenderla per la prima volta?</label>
    <div class="control">
        <input id="start-date-input" class="input is-rounded" type="date" required/>
    </div>
    <p class="help is-danger err"></p>
</div>

<script>
    window.addEventListener('load', function() {
        // Check that the start date is not before today
        document.getElementById('start-date-input').addCustomValidator(input => {
            let start = new Date(input.value);
            let today = new Date();

            if(start.getTime() <= today.getTime())
                return "L'inizio non può essere prima di oggi";
            return true;
        });
    });
</script>