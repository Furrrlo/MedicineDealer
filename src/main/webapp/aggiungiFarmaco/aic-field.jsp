<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">
    <div class="control is-expanded">
        <div id="aic-dropdown" class="dropdown is-fullwidth">
            <div class="dropdown-trigger">
                <input id="aic-input" class="input is-rounded" autocomplete="off"
                       type="search" placeholder="Nome medicina"/>
            </div>
            <div class="dropdown-menu">
                <div class="dropdown-content" style="min-height: 22px">
                    <%-- Spinner --%>
                    <div class="loader-wrapper">
                        <div class="loader is-loading" style="height: 20px; width: 20px"></div>
                    </div>
                    <%-- Template--%>
                    <div class="is-hidden">
                        <a class="dropdown-item aic-dropdown-item-template"></a>
                    </div>
                    <%-- Stuff goes here--%>
                </div>
            </div>
        </div>
    </div>
    <p class="help is-danger err"></p>
</div>



<script>
    window.addEventListener('load', function() {

        const dropdown = document.getElementById('aic-dropdown');
        const input = document.getElementById('aic-input');

        const dropdownContent = dropdown.querySelector('.dropdown-content');
        const spinner = dropdownContent.querySelector('.loader-wrapper');
        const templateClass = 'aic-dropdown-item-template';
        const template = dropdown.querySelector('.aic-dropdown-item-template');

        input.addCustomValidator(() => {
            if(!input.aic_value)
                return "Devi selezionare un farmaco dal dropdown";
            return true;
        });

        input.addEventListener('input', async () => {
            // Delete any old value
            input.aic_value = null;

            const prevVal = input.value;
            if(prevVal.length < 2) {
                dropdown.classList.remove('is-active');
                return;
            }

            // Only fetch if the value hasn't been edited for more than 500 ms
            await new Promise(r => setTimeout(r, 500));
            if(prevVal !== input.value)
                return;

            cleanDropDown();
            dropdown.classList.add('is-active');
            spinner.classList.add('is-active');

            const path = '${pageContext.request.contextPath}/api/farmaci/' + input.value;
            const medicines = await fetch(path).then(async response =>{
                if(!response.ok)
                    throw response.status + ":" + (await response.text());
                return JXON.stringToJs(await response.text());
            }).then(medicines => {
                if(!medicines.medicine.medicina)
                    return [];
                if(!medicines.medicine.medicina.forEach)
                    return [ medicines.medicine.medicina ];
                return medicines.medicine.medicina;
            });

            loadDropDown(medicines);
            spinner.classList.remove('is-active');
        });

        function cleanDropDown() {
            while (dropdownContent.lastChild) {
                dropdownContent.removeChild(dropdownContent.lastChild);
                // Keep the spinner and the template
                if(dropdownContent.childElementCount <= 2)
                    break;
            }
        }

        function loadDropDown(medicines) {
            medicines.slice(0, 10).forEach(medicine => {
                const node = template.cloneNode(true);
                node.classList.remove(templateClass);

                node.innerText = medicine.name;
                node.addEventListener('click', () => {
                    input.value = medicine.name;
                    input.aic_value = medicine.aic_farmaco;
                    input.setCustomError('');

                    dropdown.classList.remove('is-active');
                });

                dropdownContent.appendChild(node);
            });
        }
    });
</script>