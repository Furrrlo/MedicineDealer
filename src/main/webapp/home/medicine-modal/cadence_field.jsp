<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field ">
    <div class="field has-addons">
        <p class="control">
            <a class="button is-static is-small is-rounded">Ogni</a>
        </p>
        <p class="control">
            <input id="interval-input" class="input is-small is-rounded" type="number" min="1" required/>
        </p>
        <p class="control">
            <span class="select is-small is-rounded">
                <select id="period-select">
                    <option value="Giornaliera">giorni</option>
                    <option value="Settimanale">settimane</option>
                </select>
            </span>
        </p>
    </div>
    <p class="help is-danger err"></p>
</div>

<div class="week-days-container is-hidden">
    <%@ include file="week_days_field.jsp" %>
</div>

<script>
    window.addEventListener('load', function() {
        const cadenceSelect = document.getElementById('period-select');
        const weekDaysContainer = document.querySelector('.week-days-container');

        cadenceSelect.forceChange = () => {
            if(cadenceSelect.value === 'Settimanale')
                weekDaysContainer.classList.remove('is-hidden');
            else
                weekDaysContainer.classList.add('is-hidden');
        };

        cadenceSelect.addEventListener('change', () => cadenceSelect.forceChange());
    });
</script>