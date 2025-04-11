


const svg = document.getElementsByTagName('svg')[0];

const styleTag = document.createElementNS("http://www.w3.org/2000/svg", "style");
styleTag.textContent = `
  .selected { stroke: black !important; }
  .caption-selected { font-weight: bold; }
  .default { fill: #e9e9e9; }
`;
svg.insertBefore(styleTag, svg.firstChild);

document.querySelectorAll('*[id^=station-]').forEach(el => {
  el.removeAttribute('style');
  if (el.classList.contains('state')) {
    el.classList.add('default');
  }
});

const selectedStationIds = new Set();

document.addEventListener("click", handleClick);

async function handleClick(e) {
  let el = e.target;

  while (el && el !== document) {
    if (el.id && (el.id.startsWith("station-") || el.id.startsWith("caption-"))) {
      break;
    }
    el = el.parentNode;
  }

  if (!el || !el.id) return;

  const stationNum = el.id.split("-")[1];
  const stationId = `station-${stationNum}`;

  if (selectedStationIds.has(stationId)) {
    deselectStation(stationId);
    selectedStationIds.delete(stationId);
    return;
  } else {
    const allowed = await canSelectStation(stationId);
    if (!allowed) return;

    selectStation(stationId);
    selectedStationIds.add(stationId);
  }
  e.stopPropagation();
}

function canSelectStation(stationId) {
  return new Promise((resolve) => {
    try {
      const callbackName = `callback_${Date.now()}`;
      window[callbackName] = (result) => {
        resolve(result === "true");
        delete window[callbackName];
      };
      androidObj.onStationClickedWithResult(stationId, "select", callbackName);
    } catch (e) {
      resolve(false);
    }
  });
}

function selectStation(stationId) {
  const stationElem = document.getElementById(stationId);
  if (!stationElem) return;

  const circle = stationElem.tagName.toLowerCase() === "circle"
    ? stationElem
    : stationElem.querySelector("circle");
  if (!circle) return;

  const caption = document.getElementById("caption-" + stationId.split("-")[1]);

  if (!circle.hasAttribute("data-original-stroke")) {
    const origStroke = circle.getAttribute("stroke") || "";
    circle.setAttribute("data-original-stroke", origStroke);
  }

  circle.classList.add("selected");
  circle.setAttribute("stroke", "black");

  if (caption) caption.classList.add("caption-selected");

  const name = caption ? caption.textContent.trim() : "Без названия";
  document.getElementById('l_value').innerHTML = name;

  window.androidObj.onStationClick(`${stationId}:select`);
}

function deselectStation(stationId) {
  if (!stationId) return;

  const stationElem = document.getElementById(stationId);
  if (!stationElem) return;

  const circle = stationElem.tagName.toLowerCase() === "circle"
    ? stationElem
    : stationElem.querySelector("circle");
  if (!circle) return;

  circle.classList.remove("selected");

  const origStroke = circle.getAttribute("data-original-stroke");
  if (origStroke) {
    circle.setAttribute("stroke", origStroke);
  }

  const caption = document.getElementById("caption-" + stationId.split("-")[1]);
  if (caption) caption.classList.remove("caption-selected");

  document.getElementById('l_value').innerHTML = '';

  window.androidObj.onStationClick(`${stationId}:deselect`);
}


function updateFromAndroid(message) {
  document.getElementById('l_value').innerHTML = message;
}
