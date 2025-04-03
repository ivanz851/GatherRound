import {SvgPanZoom} from "vue-svg-pan-zoom";
import map from "../assets/get-scheme-metadata.json"

export default {
    name: "Map",

    data: () => {
        return {stationByName: {}}
    },

    components: {
        SvgPanZoom
    },

    methods: {
        initStationClicks() {
            // Привязка кликов к кружкам (станциям)
            document.querySelectorAll('circle[id^="station-"]').forEach(circle => {
                this.bindStationClick(circle, 'station');
            });

            // Привязка кликов к названиям станций (text)
            document.querySelectorAll('text[id^="caption-"]').forEach(text => {
                this.bindStationClick(text, 'caption');
            });
        },

        bindStationClick(element, prefix) {
            const id = element.id.split('-')[1];
            element.style.cursor = 'pointer';
            element.addEventListener('click', () => {
                this.handleStationClick(id);
            });
        },

        handleStationClick(id) {
            const caption = document.querySelector(`#caption-${id}`);
            const name = caption?.textContent?.trim() || 'Неизвестно';

            alert(`Вы нажали: ${id} (${name})`); // ← ВСТАВЬ ВОТ ЭТО
            console.log(`Вы нажали на станцию ${id}: ${name}`);

            // Здесь можно вызывать твою активацию
            if (typeof this.activate === 'function') {
                this.activate(`station-${id}`);
            }

            // Или вызвать внешний обработчик, например на Android
            if (window.Android?.onStationClick) {
                window.Android.onStationClick(`station-${id}`);
            }
        },

        applyDynamicViewBox() {
            const svg = document.querySelector("svg");
            if (svg) {
                const width = window.innerWidth;
                const height = window.innerHeight;
                svg.setAttribute("viewBox", `0 0 ${width} ${height}`);
            }
        },

    },
    mounted() {

        this.$nextTick(() => {
            console.log("Vue запущен !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            alert("Vue mounted Vue запущен !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")


            this.applyDynamicViewBox();
            this.initStationClicks();
        });
    }
}