import "ol/ol.css";
import GeoJSON from "ol/format/GeoJSON";
import { Map, View } from "ol";
import OSM from "ol/source/OSM";
import Style from "ol/style/Style";
import Fill from "ol/style/Fill";
import Stroke from "ol/style/Stroke";
import CircleStyle from "ol/style/Circle";
import VectorSource from "ol/source/Vector";
import Polygon from "ol/geom/Polygon";
import { Tile as TileLayer, Vector, Vector as VectorLayer } from "ol/layer";
import Draw from "ol/interaction/Draw";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import * as turf from "@turf/turf";
import MousePosition from "ol/control/MousePosition";
import { createStringXY } from "ol/coordinate";
import { defaults as defaultControls } from "ol/control";
import LineString from "ol/geom/LineString";
import Text from "ol/style/Text";
import { getLength } from "ol/sphere";
import Overlay from "ol/Overlay";

$("#app").css("visibility", "hidden");
$("#app2").css("visibility", "hidden");
$("#reloadPage").css("visibility","hidden");
$("#saveTodB").css("visibility", "hidden")
function hideSecondDiv(){
$("#spatialResolution").css("visibility", "hidden");
$("#spatialResolutionLabel").css("visibility", "hidden");
$("#digitalResolutionWidth").css("visibility", "hidden");
$("#digitalResolutionWidthLabel").css("visibility", "hidden");
$("#digitalResolutionHeight").css("visibility", "hidden");
$("#digitalResolutionHeightLabel").css("visibility", "hidden");
$("#sensorWidth").css("visibility", "hidden");
$("#sensorWidthLabel").css("visibility", "hidden");
$("#focalLength").css("visibility", "hidden");
$("#focalLengthLabel").css("visibility", "hidden");
$("#fligthAltitude").css("visibility","hidden")
$("#drawTrajectory").css("visibility", "hidden")
$("#beforeVizButton").css("visibility", "hidden")
$("#visualizeBound").css("visibility", "hidden")
$("#boundDate").css("visibility", "hidden")
$("#finalDiv").css("visibility", "hidden")

}
hideSecondDiv()

function stateChange(newState) {
  setTimeout(function() {
    if (newState == -1) {
      measureTooltipElement.parentNode.removeChild(measureTooltipElement);
    }
    if (newState == 0) {
      divText.warningText = "";
    }
    if(newState==1){
      $("#spatialResolution").css("visibility", "visible");
      $("#spatialResolutionLabel").css("visibility", "visible");

    }
  }, 2500);
}

$("#finalSubmit").css("visibility", "hidden");

const fillStyle = new Fill({
  color: [66, 245, 96, 0.4],
});
const strokeStyle = new Stroke({
  color: [46, 45, 45, 1],
  witdth: 1.2,
});
const circelStyle = new CircleStyle({
  fill: new Fill({
    color: [245, 49, 5, 1],
  }),
  radius: 7,
  stroke: strokeStyle,
});
var source1 = new VectorSource({ wrapX: false });

var vector = new VectorLayer({
  source: source1,
});

var raster = new TileLayer({
  source: new OSM(),
});


var measureTooltipElement;
var measureTooltip;
var pointerMoveHandler = function(evt) {
  if (evt.dragging) {
    return;
  }
};

var mousePositionControl = new MousePosition({
  coordinateFormat: createStringXY(4),
  projection: "EPSG:4326",
});
var map = new Map({
  controls: defaultControls().extend([mousePositionControl]),
  target: "map",
  layers: [raster, vector],
  view: new View({
    center: [2625757.578372622, 5904479.812124118],
    zoom: 13,
    maxZoom: 18,
    minZoom: 1,
  }),
});

map.on("pointermove", pointerMoveHandler);

mousePositionControl.setProjection("EPSG:4326");
mousePositionControl.setCoordinateFormat(createStringXY(7));

var divText = new Vue({
  el: "#app",
  data: {
    coordinatesText: "",
    inputText: "",
    warningText: "",
    flightAltitude:"",
  },
});
var divText2 = new Vue({
  el: "#app2",
  data: {
    flightAltitude:"",
    finalDivText:"",
  },
});
$("#submitGEO").on("click", function() {
  const selectedFile = document.getElementById("geoInput").files[0];
  let pasteJson;
  if (selectedFile) {
    var reader = new FileReader();
    reader.readAsText(selectedFile, "UTF-8");
    reader.onload = function(evt) {
      try {
        pasteJson = jQuery.parseJSON(evt.target.result);
        divText.warningText =
          "The uploaded GeoJSON is visible on the map";
          stateChange(0);
      } catch (error) {
        console.log(error);
        divText.warningText =
          "The GeoJSON you want to paste is not correctly formated!";
        stateChange(0);
      }
      if (
        pasteJson.hasOwnProperty("type") &&
        pasteJson.type == "FeatureCollection"
      ) {
        for (let i = 0; i < pasteJson.features.length; i++) {
          if (pasteJson.features[i].geometry.type == "Polygon")
            drawPolygonOnMap(
              pasteJson.features[i].geometry.coordinates,
              "fromdb"
            );
        }
      }
      if (pasteJson.hasOwnProperty("type") && pasteJson.type == "Polygon") {
        drawPolygonOnMap(pasteJson.coordinates, "fromdb");
      }
    };
    reader.onerror = function(evt) {
      //TODO
    };
  }
});
var reset=0;
var count = 0;
var countTraj=0;
var startTime;
var endTime;
$("#start-time").on("change", function() {
  startTime = document.getElementById("start-time");
  var endDate = new Date(startTime.value);
  endDate.setMinutes(endDate.getMinutes() + 30);
  Number.prototype.AddZero = function(b, c) {
    var l = String(b || 10).length - String(this).length + 1;
    return l > 0 ? new Array(l).join(c || "0") + this : this;
  }; //to add zero to less than 10
  var localDateTime =
    [
      endDate.getFullYear(),
      (endDate.getMonth() + 1).AddZero(),
      endDate.getDate().AddZero(),
    ].join("-") +
    "T" +
    [endDate.getHours().AddZero(), endDate.getMinutes().AddZero()].join(":");
  endTime = document.getElementById("end-time");
  endTime.min = startTime;
  endTime.value = localDateTime;
  sendData(startTime, endTime);
});
var draw;
draw = new Draw({
  source: source1,
  type: "Polygon",
});
$("#drawTrajectory").on("click", function() {
   reset=1
  map.addInteraction(draw);
  createMeasureTooltip();
  countTraj++;
    if (countTraj == 1) {
      addInteraction("displayTrajectory");
    }
});
function sendData() {
  $("form").on("submit", function(e) {
    e.preventDefault();
    map.addInteraction(draw);
    createMeasureTooltip();
    count++;
    if (count == 1) {
      addInteraction("planning");
    }
  });
}

map.removeInteraction();

function createMeasureTooltip() {
  if (measureTooltipElement) {
    measureTooltipElement.parentNode.removeChild(measureTooltipElement);
  }
  measureTooltipElement = document.createElement("div");
  measureTooltipElement.className = "ol-tooltip ol-tooltip-measure";
  measureTooltip = new Overlay({
    element: measureTooltipElement,
    offset: [0, -15],
    positioning: "bottom-center",
  });
  map.addOverlay(measureTooltip);
}
var countDisp=0;
function addInteraction() {
  var clickCoords = 0;
  map.on("click", function(e) {
    clickCoords = e.coordinate;
  });
  map.on("pointermove", function(e) {
    var output;
    var edgesarr = [];
    edgesarr.push(clickCoords);
    edgesarr.push(e.coordinate);
    if (clickCoords != 0) {
      output = formatLength(new LineString(edgesarr));
      var tooltipCoord;
      tooltipCoord = clickCoords;
      measureTooltipElement.innerHTML = output;
      measureTooltip.setPosition(tooltipCoord);
    }
    
  });

  draw.on("drawend", (evt) => {
    map.removeOverlay(measureTooltip);
    measureTooltip.setPosition(0,0)
    const coordinates = evt.feature.getGeometry().getCoordinates();
    const geometry = new Polygon(coordinates);
    const geometry4326 = geometry.transform("EPSG:3857", "EPSG:4326");
    let picked = document.getElementById("toPick");
    drawPolygonOnMap(geometry4326.getCoordinates(), "toDraw");
      var text=  "The polygon that you created has the following coordinates";
    if (picked.value == "schedule") {
      divText.inputText =""
      divText.inputText+=text
    
      var data = {
        geometry: {
          type: "Polygon",
          coordinates: geometry4326.getCoordinates(),
        },
        startTime: startTime.value,
        endTime: endTime.value,
      };
      map.removeInteraction(draw);
      
      fetch("http://localhost:8080/drones", {
        
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          charset: "utf-8",
        },
        body: JSON.stringify(data),
      },)
        .then((response) => response.json())
        .then((data) => {
          if (data[0].name == "intersectie") {
            checkIntersection(data, geometry4326.getCoordinates());
          } else {
            console.log("Success:");
            $("#saveTodB").css("visibility", "visible")
            saved(geometry4326)

          }
        })
        .catch((error) => {
          console.error("Error:", error);
        });

    } else if (picked.value == "visualizeTrajectory") {
      var params = {
        geometry: {
          type: "Polygon",
          coordinates: geometry4326.getCoordinates(),
        },
        spatialResolution:spatialResolution.value,
        digitalResolutionWidth: digitalResolutionWidth.value,
        digitalResolutionHeight: digitalResolutionHeight.value,
        sensorWidth: sensorWidth.value,
        focalLength: focalLength.value,
      };
      map.removeInteraction(draw);

      fetch("http://localhost:8080/drones/getTrajectory",{
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          charset: "utf-8",
        },
        body: JSON.stringify(params),
      })
        .then((response) => response.json())
        .then((data) => {
          setTimeout(function() {
            drawPolygonOnMap(data, "trajectory");
          }, 2000);
          $("#beforeVizButton").css("visibility", "visible")
          $("#visualizeBound").css("visibility", "visible")

          console.log("Success:");
        })
        .catch((error) => {
          console.error("Error:", error);
        });
        if (reset == 1) {
          if(countDisp!=0){
           
          }
          countDisp++;

          getBoundingPath(params);
          
        }

    }
  });
}

var formatLength = function(line) {
  var length = getLength(line);
  var output;
  if (length > 100) {
    output = Math.round((length / 1000) * 100) / 100 + " " + "km";
  } else {
    output = Math.round(length * 100) / 100 + " " + "m";
  }
  return output;
};

var polygonNo = 1;

function drawPolygonOnMap(coordinates, source) {
  if (source == "fromdb") {
    for (let i = 0; i < coordinates[0].length - 1; i++) {
      const polygonFeature2 = new Feature(
        new Point(coordinates[0][i]).transform("EPSG:4326", "EPSG:3857")
      );
      let source2 = new VectorSource({
        features: [polygonFeature2],
      });
      var layer2 = new VectorLayer({
        source: source2,
      });
      map.addLayer(layer2);
    }
    const polygonFeature = new Feature(
      new Polygon(coordinates).transform("EPSG:4326", "EPSG:3857")
    );
    let source = new VectorSource({
      features: [polygonFeature],
    });
    var layer = new VectorLayer({
      source: source,
      style: new Style({
        fill: fillStyle,
        stroke: strokeStyle,
        image: circelStyle,
      }),
    });

    map.addLayer(layer);
  } else if (source == "toDraw") {
    divText.coordinatesText=""
    divText.coordinatesText +=
      "Flight plan number " + polygonNo + " has the following coordinates: ";
    for (let i = 0; i < coordinates[0].length - 1; i++) {
      const points = new Feature(
        new Point(coordinates[0][i]).transform("EPSG:4326", "EPSG:3857")
      );
      divText.coordinatesText +="\n" +"Point " +(i + 1) +": Latitude: " +coordinates[0][i][0] +" Longitude: " +coordinates[0][i][1];
      let source2 = new VectorSource({
        features: [points],
      });
      var layer2 = new VectorLayer({
        source: source2,
        
      });
      map.addLayer(layer2);
    }
    divText.coordinatesText += "\n";
    const fillEdges = new Fill({
      color: [53, 245, 5, 1],
    });
    for (let i = 0; i < coordinates[0].length - 1; i++) {
      var edgesarr = [];
      edgesarr.push(coordinates[0][i]);
      edgesarr.push(coordinates[0][i + 1]);
      const edges = new Feature(
        new LineString(edgesarr).transform("EPSG:4326", "EPSG:3857")
      );
      let sourceEdges = new VectorSource({
        features: [edges],
      });

      var layerEdge = new VectorLayer({
        source: sourceEdges,
        style: new Style({
          fill: fillEdges,
          stroke: strokeStyle,
          text: new Text({
            font: "14px Calibri",
            fill: new Fill({ color: "#000" }),
            stroke: new Stroke({
              color: "#fff",
              width: 2,
            }),
            text: formatLength(
              new LineString(edgesarr).transform("EPSG:4326", "EPSG:3857")
            ),
          }),
        }),
      });
      map.addLayer(layerEdge);
    }
    polygonNo++;
  } else if (source == "trajectory") {
    for (let i = 0; i < coordinates.length - 1; i++) {

        const points = new Feature(
          new Point(coordinates[i]).transform("EPSG:4326", "EPSG:3857")
        );
        if (i == coordinates.length - 1) {
          const points = new Feature(
            new Point(coordinates[i + 1]).transform("EPSG:4326", "EPSG:3857")
          );
        }
        var fillPoint;
        if (i==0) {
            fillPoint = new Fill({
            color: [76, 168, 50,1],
          }); 
        }
        else{
            fillPoint = new Fill({
            color: [245, 49, 5, 1],
          }); 
        }
        var layer2 = new VectorLayer({
          source: new VectorSource({
            features: [points],
          }),
          style: new Style({
            image:new CircleStyle({
              fill: fillPoint,
              radius: 5,
            }),
          }),
                  
        });
        map.addLayer(layer2);

      var edgesarr = [];
      edgesarr.push(coordinates[i]);
      edgesarr.push(coordinates[i + 1]);
      const edges = new Feature(
        new LineString(edgesarr).transform("EPSG:4326", "EPSG:3857")
      );

      var layerEdge = new VectorLayer({
        source: new VectorSource({
          features: [edges],
        }),
        style: new Style({
          fill: {
            color: [53, 245, 5, 1],
          },
          stroke: strokeStyle,
        }),
      });
      map.addLayer(layerEdge);
    

    }
  }
}
$("#button").on("click", function() {
  removeLayers()
  let request = new XMLHttpRequest();
  console.log(startTime.value, endTime.value,"click")
  var url =
    "http://localhost:8080/drones" +"/" +startTime.value +"_" +endTime.value;
  request.open("GET", url);
  request.responseType = "text";
  request.onload = function() {
    var test = JSON.parse(request.response);
    test.forEach((o) => {
      drawPolygonOnMap(o.geometry.coordinates, "fromdb");
    });
  };
  request.send();
});
function saved(geometry){ 
$("#saveTodB").on("click",function(){
  divText.warningText="The plan was saved successfully"
  drawPolygonOnMap(geometry.getCoordinates(), "fromdb");
})
}

function checkIntersection(toDraw, drawnCoords) {
  $("#saveTodB").css("visibility", "hidden")
  const userPolygon = new Feature(
    new Polygon(drawnCoords).transform("EPSG:4326", "EPSG:3857")
  );
  for (let i = 0; i < toDraw.length; i++) {
    const polygonFeature = new Feature(
      new Polygon(toDraw[i].geometry.coordinates).transform(
        "EPSG:4326",
        "EPSG:3857"
      )
    );
    var format = new GeoJSON();
    var intersection = format.readFeature(
      turf.intersect(
        format.writeFeatureObject(userPolygon),
        format.writeFeatureObject(polygonFeature)
      )
    );
    const fillStyle2 = new Fill({
      color: [245, 49, 5, 0.7],
    });
    let source = new VectorSource({
      features: [intersection],
    });
    var layer = new VectorLayer({
      source: source,
      style: new Style({
        fill: fillStyle2,
        stroke: strokeStyle,
        image: circelStyle,
      }),
    });
    divText.warningText="The plan drawn is intersects other plans as you can see on the map.\t\n"
    +"Please consider realoading the page and picking other area or time interval"
    $("#reloadPage").css("visibility","visible")


    map.addLayer(layer);
  }
}

$("#toPick").on("change", function(e) {
  e.preventDefault();
  let picked = document.getElementById("toPick");
  if (picked.value == "schedule") {
    divText.coordinatesText=""
    removeLayers()
    $("#app").css("display", "inline-block");
    $("#app").css("visibility", "visible");
    $("#app2").css("visibility", "hidden");
    $("#reloadPage").css("visibility","hidden");
    $("#saveTodB").css("visibility", "hidden")
    hideSecondDiv()

  } else if (picked.value == "visualizeTrajectory") {
    removeLayers()
    $("#app").css("display", "none");
    $("#app").css("visibility", "hidden");
    $("#app2").css("visibility", "visible");
    
    stateChange(1);

  } else if (picked.value == "empty") {
  removeLayers()
    $("#app").css("visibility", "hidden");
    $("#app2").css("visibility", "hidden");
  }
});

$("#spatialResolution").on("keydown",function(e){
  if(e.keyCode == 13){
    let spatialResolution = document.getElementById("spatialResolution");
    $("#digitalResolutionWidth").css("visibility", "visible");
    $("#digitalResolutionWidthLabel").css("visibility", "visible");
  }
})
$("#digitalResolutionWidth").on("keydown",function(e){
  if(e.keyCode == 13){
    let digitalResolutionWidth = document.getElementById("spatialResolution");
    $("#digitalResolutionHeight").css("visibility", "visible");
    $("#digitalResolutionHeightLabel").css("visibility", "visible");
  }
})
$("#digitalResolutionHeight").on("keydown",function(e){
  if(e.keyCode == 13){
    let digitalResolutionHeight = document.getElementById("spatialResolution");
    $("#sensorWidth").css("visibility", "visible");
    $("#sensorWidthLabel").css("visibility", "visible");
  }
})
$("#sensorWidth").on("keydown",function(e){
  if(e.keyCode == 13){
    let sensorWidth = document.getElementById("spatialResolution");
    $("#focalLength").css("visibility", "visible");
    $("#focalLengthLabel").css("visibility", "visible");
  }
})
$("#focalLength").on("keydown",function(e){
  if(e.keyCode == 13){
    let focalLength = document.getElementById("spatialResolution");
    $("#fligthAltitude").css("visibility","visible")
    $("#drawTrajectory").css("visibility", "visible")
    let altitude= (spatialResolution.value*10*digitalResolutionWidth.value*focalLength.value)/sensorWidth.value;
    divText2.flightAltitude="The altitude required by the parameters provided is: "+(altitude*0.001)+" m"
    
  }
})
var startTime2, endTime2;
$("#start-time2").on("change", function() {
  startTime2 = document.getElementById("start-time2");
  var endDate = new Date(startTime2.value);
  endDate.setMinutes(endDate.getMinutes() + 30);
  Number.prototype.AddZero = function(b, c) {
    var l = String(b || 10).length - String(this).length + 1;
    return l > 0 ? new Array(l).join(c || "0") + this : this;
  }; //to add zero to less than 10
  var localDateTime =
    [
      endDate.getFullYear(),
      (endDate.getMonth() + 1).AddZero(),
      endDate.getDate().AddZero(),
    ].join("-") +
    "T" +
    [endDate.getHours().AddZero(), endDate.getMinutes().AddZero()].join(":");
    endTime2 = document.getElementById("end-time2");
    endTime2.min = startTime2;
    endTime2.value = localDateTime;
 // sendData(startTime2, endTime);
});


function removeLayers(){
  var layerArray, len, layer;
  layerArray = map.getLayers().getArray(),
  len = layerArray.length;
  while (len > 1){
  layer = layerArray[len-1];
  map.removeLayer(layer);
  len = layerArray.length;
}
}
function getBoundingPath(data){ 
$("#visualizeBound").on("click",function(){
  fetch("http://localhost:8080/drones/trajectory/region", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      charset: "utf-8",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((data) => {
      let coords=[]
      let coordonate=[]
      for(let i=0;i<data.length;i++){
        let local=[]
        local.push(data[i].x)
        local.push(data[i].y)
        coordonate.push(local)
      }
      coords.push(coordonate)
      var polygonFeature = new Feature(
        new Polygon(coords).transform("EPSG:4326", "EPSG:3857")
      );
      let source = new VectorSource({
        features: [polygonFeature],
      });
      var layer = new VectorLayer({
        source: source,
        style: new Style({
          fill: fillStyle,
          stroke: strokeStyle,
          image: circelStyle,
        }),
      });
      //map.addLayer(layer)
      displayAndSaveBound(layer,coords);
      countDisp++;
      console.log("Success:");
      $("#boundDate").css("visibility", "visible")

    })
    .catch((error) => {
      console.error("Error:", error);
    });
 
  }
    
)}

function displayAndSaveBound(layer,coords){
  map.addLayer(layer);
  console.log(coords)
    const geometry = new Polygon(coords);
    //const geometry4326 = geometry.transform("EPSG:3857", "EPSG:4326");
  console.log(geometry.getCoordinates())
  
  $("#submitBound").on("click",function(){
    var data = {
      geometry: {
        type: "Polygon",
        coordinates: geometry.getCoordinates(),
      },
      startTime: startTime2.value,
      endTime: endTime2.value,
    };
    map.removeInteraction(draw);
    
    fetch("http://localhost:8080/drones", {
      
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        charset: "utf-8",
      },
      body: JSON.stringify(data),
    },)
      .then((response) => response.json())
      .then((data) => {
        if (data[0].name == "intersectie") {
          checkIntersection(data, geometry.getCoordinates());
          divText2.finalDivText="Unfortunately the bounding region of the path overlaps with other existing plans \n\t"+
          "and it cannot be saved as a valid plan. We recommend to pick another date or hour"
        } else {
          console.log("Success:");
          divText2.finalDivText="The bounding region was successfully saved into the database"
          map.removeLayer(layer)
          setTimeout(function() {
            drawPolygonOnMap(geometry.getCoordinates(), "fromdb");
          }, 2000);  

        }
      })
      .catch((error) => {
        console.error("Error:", error);
      });
      
  })
 


}
