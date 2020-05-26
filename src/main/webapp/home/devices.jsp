<%@ page contentType="text/html;charset=UTF-8" %>

<div class="field">
    <div class="control">
        <div class="select is-rounded is-fullwidth">
            <!-- Template -->
            <select class="is-hidden">
                <option class="porta-medicine-template"></option>
            </select>
            <!-- Container -->
            <select id="porta-medicine-container"></select>
        </div>
    </div>
</div>

<script>
    const Devices = ((exports) => {
        const container = document.querySelector('#porta-medicine-container');
        const templateClass = 'porta-medicine-template';
        const template = document.querySelector('.' + templateClass);

        exports.reloadDevices = () => {
            return fetchDevices().then(devices => {
                cleanDevices();
                loadDevices(devices);
            }).catch(ex => {
                console.error(ex);
            });
        };

        function fetchDevices() {
            return fetch('${pageContext.request.contextPath}/api/porta_medicine').then(async response => {

                if(response.status === 401) {
                    LoginModal.open();
                    throw response.status + ": " + (await response.text());
                }

                if(!response.ok)
                    throw response.status + ": " + (await response.text());
                return JXON.stringToJs(await response.text());
            }).then(devices => {
                // XML is kinda shit to use
                let obj = devices.porta_medicine.porta_medicina;
                if(obj.forEach)
                    return obj;
                return [ obj ];
            });
        }

        function cleanDevices() {
            while (container.firstChild)
                container.removeChild(container.firstChild);
        }

        function loadDevices(devices) {
            if(!devices.forEach)
                return;

            devices.forEach(device => {
                const deviceNode = template.cloneNode(true);
                deviceNode.classList.remove(templateClass);

                deviceNode.value = device.id;
                deviceNode.textContent = device.nome;

                container.appendChild(deviceNode);
            });
        }

        return exports;
    })({});
</script>