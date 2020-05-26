<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">
    <div class="control">
        <label class="checkbox">
            <input id="repeat-checkbox" type="checkbox">
            Si ripete
        </label>
    </div>
</div>

<div class="repeat-container field is-hidden">
    <%@ include file="cadence_field.jsp" %>
    <%@ include file="end_field.jsp" %>
</div>

<script>
    window.addEventListener('load', function() {
        const repeatCheckbox = document.getElementById('repeat-checkbox');
        const repeatContainer = document.querySelector('.repeat-container');

        repeatCheckbox.addEventListener('change', () => {
            repeatContainer.classList.toggle('is-hidden');
        });
    });
</script>