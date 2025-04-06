window.androidObj = function AndroidClass() {};

var node = '.selected {stroke: black;} .caption-selected {font-weight: bold;}'; // custom style class has been injected into the SVG body inside HTML
var nodex = '.default{fill:#e9e9e9;}';
var svg = document.getElementsByTagName('svg')[0];

let styleTag = document.createElementNS("http://www.w3.org/2000/svg", "style");
styleTag.textContent = `
  .selected { stroke: black !important; }
  .caption-selected { font-weight: bold; }
  .default { fill: #e9e9e9; }
`;
svg.insertBefore(styleTag, svg.firstChild);

document.addEventListener("click", doSomething);

var query = '*[id^=station-]';
var tablePathList = document.querySelectorAll(query);
var table;
for (table = 0; table < tablePathList.length; table++) {
    tablePathList[table].removeAttribute('style');
    if (tablePathList[table].classList.contains('state')) {
        document.getElementById(tablePathList[table].id).classList.add('default');
    }
}

// Храним ID текущей выделенной станции
let currentSelectedStationId = null;

function doSomething(e) {
  if (e.target !== e.currentTarget) {
    let el = e.target;

    // Поднимаемся вверх по дереву, пока не найдём элемент с нужным id
    while (el && el !== document) {
      if (el.id && (el.id.startsWith("station-") || el.id.startsWith("caption-"))) {
        break;
      }
      el = el.parentNode;
    }

    if (!el || !el.id) return;

    let clickedId = el.id;

    if (!clickedId.startsWith("station-") && !clickedId.startsWith("caption-")) return;

    let stationNum = clickedId.split("-")[1];
    let stationId = `station-${stationNum}`;
    let captionId = `caption-${stationNum}`;

    let stationElement = document.getElementById(stationId);
    let captionElement = document.getElementById(captionId);

    let stationCircle = null;

    if (!stationElement) return;

    if (stationElement.tagName.toLowerCase() === "circle") {
      stationCircle = stationElement;
    } else {
      stationCircle = stationElement.querySelector("circle");
    }

    if (!stationCircle) return;

    // Если повторное нажатие по уже выделенной станции — снимаем выделение
    if (currentSelectedStationId === stationId) {
      stationCircle.classList.remove("selected");

      // Восстановим оригинальный цвет
      const originalStroke = stationCircle.getAttribute("data-original-stroke");
      if (originalStroke) {
        stationCircle.setAttribute("stroke", originalStroke);
      }

      if (captionElement) captionElement.classList.remove("caption-selected");
      document.getElementById('l_value').innerHTML = '';
      window.androidObj.textToAndroid('');
      currentSelectedStationId = null;
      return;
    }


    // Удаляем выделение со всех станций
    document.querySelectorAll('circle[id^="station-"], g[id^="station-"] circle').forEach(c => {
      c.classList.remove("selected");
      //c.removeAttribute("stroke");
    });

    document.querySelectorAll('[id^="caption-"]').forEach(c => c.classList.remove("caption-selected"));

    // Добавляем новое выделение
    stationCircle.classList.add("selected");
    // Сохраняем оригинальный цвет один раз
    if (!stationCircle.hasAttribute("data-original-stroke")) {
      const originalStroke = stationCircle.getAttribute("stroke") || "";
      stationCircle.setAttribute("data-original-stroke", originalStroke);
    }
    stationCircle.setAttribute("stroke", "black");


    if (captionElement) {
      captionElement.classList.add("caption-selected");
    }

    let stationName = captionElement ? captionElement.textContent.trim() : "Без названия";

    document.getElementById('l_value').innerHTML = stationName;
    window.androidObj.textToAndroid(stationName);

    currentSelectedStationId = stationId;
  }

  e.stopPropagation();
}

function updateFromAndroid(message) {
  document.getElementById('l_value').innerHTML = message;
}