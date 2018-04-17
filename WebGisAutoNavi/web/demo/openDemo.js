var map;
var lnglat = [120.38247,36.26491];
var point = ol.proj.transform(lnglat, 'EPSG:4326', 'EPSG:3857');
var image = "/images/flager.gif";
var marker = null,layer = null;

var listTrack = [];
var xxx = 158.8904;





window.onload=function(){
	 var lnglat = [120.37680781073895, 36.26544625760539];
	 var point = ol.proj.transform(lnglat, 'EPSG:4326', 'EPSG:3857');
     layer = new ol.layer.Tile({
	          source: new ol.source.XYZ({
			   		 attributions: [new ol.Attribution({html: '君图科技'})],
			    	url: "http://webst01.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}"
			  }),
		      min : 4
	    });
	    
	    
    var baseTileLayer = new ol.layer.Tile({
		      source: new ol.source.XYZ({
		        url: "http://123.57.70.174:8280/maptile-service/maptile?x={x}&y={y}&z={z}"
		      })
	    });
	 map = new ol.Map({
	       target  : "iCenter",
		   controls: ol.control.defaults({
		       attributionOptions:({
		       	  collapsible: false
		       })
		  }),
		  layers:[
		        layer,
			    baseTileLayer
    	  ],
	 	  view: new ol.View({
		     center: point,
		     zoom  : 15,
		     rotation: xxx,
		     minZoom : 4,
		     maxZoom : 19
		  })
	 });
	 
//	 map.addEventListener("click",function(e){
//	    var coordinate = e.coordinate;
//	    var lnglat = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
//	    listTrack.push(lnglat);
//	 	 	
//	 });
	 
	 
	 
	 var imageDom = document.createElement("img");
	 imageDom.setAttribute("src","../images/1.gif");
	 imageDom.style.cursor = "pointer";
	 
	imageDom.style.webkitTransform="rotate(-90deg)"
	imageDom.style.MozTransform="rotate(-90deg)"
	imageDom.style.msTransform="rotate(-90deg)"
	imageDom.style.OTransform="rotate(-90deg)"
	imageDom.style.transform="rotate(-90deg)";


	
	var div = document.createElement("div");
	div.className = "AmapLocation";
	div.style.top=5;
	div.style.left=-5;
	div.style.fontSize=10;
	div.style.border="1px solid #999999";
	div.style.backgroundColor="#FFFFFF";
	div.style.position="absolute";
	
	
	
	div.innerText = "88888";
	
	

	 marker = new ol.Overlay({
		  position: point,
		  stopEvent: false,
		  element: imageDom
	 });
	 map.addOverlay(marker);
	 
	 markerLabel = new ol.Overlay({
		  position: point,
		  stopEvent: false,
		  element: div
	 });
	 map.addOverlay(markerLabel);
	 
	 
	 
	 imageDom.addEventListener("click",function(e){
	     	console.log("marker======",marker);
	 	 	var post = marker.getPosition();
	 	 	var popup = new ol.Overlay.Popup();
			map.addOverlay(popup);
		    popup.show(post, {
		       title : "我的标题",
		       content :"我的标题"
		    });
	 });
}

//ol.coordinate.rotates = function(coordinate, angle) {
//	  var cosAngle = Math.cos(angle);
//	  var sinAngle = Math.sin(angle);
//	  var x = coordinate[0] * cosAngle - coordinate[1] * sinAngle;
//	  var y = coordinate[1] * cosAngle + coordinate[0] * sinAngle;
//	  return [x,y];
//};
//
//
// rotateDom=function(htmlDom,rotate){
//			    if(htmlDom){
//			        htmlDom.style.rotation = rotate + "deg";
//			        htmlDom.style.webkitTransform="rotate("+rotate+"deg)"
//					htmlDom.style.MozTransform="rotate("+rotate+"deg)"
//					htmlDom.style.msTransform="rotate("+rotate+"deg)"
//					htmlDom.style.OTransform="rotate("+rotate+"deg)"
//					htmlDom.style.transform="rotate("+rotate+"deg)";
//			    }else{
//			    	console.log("不支持旋转属性");
//			    }
//		   };
//var pointTrack = null;
//playTrack = function(){
//    if(pointTrack){
//	    map.removeOverlay(pointTrack);
//	}
//	var index = 0;
//	var total = listTrack.length ;
//    var timer = setInterval(function(){
//    	var xxx = map.getView().getRotation();
//        var curlnglat = listTrack[index];
//        var curpoint = ol.proj.transform(curlnglat, 'EPSG:4326', 'EPSG:3857');
//        if(index == 0 ){
//        	 var imageDom = document.createElement("img");
//        	 imageDom.setAttribute("src","up.png");
//			 imageDom.style.width = "17px";
//			 imageDom.style.height = "17px";
//			 imageDom.style.cursor = "pointer";
//             pointTrack = new ol.Overlay({
//				  position: curpoint,
//				  stopEvent: false,
//				  element: imageDom
//			 });
//			 console.log(pointTrack);
//			 map.addOverlay(pointTrack);
//			 pointTrack.setOffset([-9,-9]);
//        }else if(index == total-1){
//           clearInterval(timer);
//           index = 0;
//        }else{
//        	
//            var uplnlat= listTrack[index-1];
//            /****
//            var x1 = ol.coordinate.rotates(curlnglat, xxx); //旋转后的坐标
//            var x2 = ol.coordinate.rotates(uplnlat, xxx); //旋转后的坐标
//            var xpoint1 = ol.proj.transform(x1, 'EPSG:4326', 'EPSG:3857');
//            var xpoint2 = ol.proj.transform(x2, 'EPSG:4326', 'EPSG:3857');
//            var nlagxxw =  GAS.angRoute({x:xpoint1[0],y:xpoint1[1]},{x:xpoint2[0],y:xpoint2[1]});
//            console.log("nlagxxw===="+nlagxxw);
//            **********/
//            
//            
//            /***********************/
//            var uppoint = ol.proj.transform(uplnlat, 'EPSG:4326', 'EPSG:3857');
//            console.info("当前点的坐标",curpoint);
//            
//            var nx22 =  GAS.angRoute({x:uppoint[0],y:uppoint[1]},{x:curpoint[0],y:curpoint[1]});
//            if(xxx == 0){ //不旋转
//            	 nx22 = Math.round(nx22 + xxx * Math.PI/180);
//            }else{//旋转
//            	 nx22 = 90 + Math.round(nx22 + xxx * Math.PI/180);
//            }
//            console.warn("旋转角度",nx22);
//            /******************************************/
//            
//            var image = pointTrack.getElement();
//            pointTrack.setPosition(curpoint);
//            rotateDom(image,nx22);
//        }
//        index ++;
//    },1000);
//}
//function resetMapRaroute(){
//   map.getView().setRotation(xxx);   
//}
//function NoresetMapRaroute(){
//   map.getView().setRotation(0);   
//}
//
//addTrack = function(){
//       var points =dataList;
//       var pointList = [];
//       var listObj = [];
//       for (var index = 0; index < points.length; index++) {
//       	   var lnglat = points[index];
//       	   var point = ol.proj.transform(lnglat, 'EPSG:4326', 'EPSG:3857');
//       	   pointList.push(point);
//       	   var obj = {
//				distance : 12.0,
//				height : 9.943,
//				gpsTime : '2015-08-27 12:30:14',
//				speed : 16.3717,
//				direction : 157.63,
//				deviceStatus : '',
//				deviceId : '354525045369434',
//				y : lnglat[1],
//				x : lnglat[0]
//       	   };
//       	   listObj.push(obj);
//       }
//       //console.log(GAS.toString(listObj));
//       var featureRed = new ol.Feature({
//	      geometry: new ol.geom.LineString(pointList)
//	   });
//	   vectorRedLine = new ol.source.Vector({});
//       vectorRedLine.addFeature(featureRed);
//	   vectorRedLayer = new ol.layer.Vector({
//		    source: vectorRedLine,
//		    style: new ol.style.Style({
//		        stroke: new ol.style.Stroke({
//		            color: '#FF0000',
//		            width: 4
//		        }),
//		        fill: new ol.style.Fill({
//		            color: '#FF0000',
//		            weight: 1
//		        })
//		    })
//		});
//      map.addLayer(vectorRedLayer);
//}
//
////添加点
//var vectorLayer = null,List=[];
//var addmarkerHandlet = function(){
//	  var lng= 120.38247 + Math.random() * 003;
//	  var lat= 36.26491 + Math.random() * 004;
//	  var newpoint = ol.proj.transform([lng,lat], 'EPSG:4326', 'EPSG:3857');
//	  var imageDom = document.createElement("img");
//	  imageDom.setAttribute("src","../images/1.gif");
//	  var markerA = new ol.Overlay({
//		  position: newpoint,
//		  element: imageDom
//	 });
//	 map.addOverlay(markerA);
//	 List.push(markerA);
//}
//
//hideMap = function(){
//   layer.setVisible(false);
//}
//showMap = function(){
//   layer.setVisible(true);
//}
//window.resize =function(){
//   layer.redraw(true);
//}
//clearMap = function(){
//	console.log(layer.getSource());
//}
//
////判断点在bound
//isPointInBounds=function(){
//	var size = map.getSize();
//	var bounts= map.getView().calculateExtent(size);
//	var fla = ol.extent.containsCoordinate(bounts,point);
//	console.log(fla);
//	return fla;
//};
//
//setMarker= function(){
//	 var lng= 120.38247 + Math.random() * 003;
//	 var lat= 36.26491 + Math.random() * 004;
//	 var newpoint = ol.proj.transform([lng,lat], 'EPSG:4326', 'EPSG:3857');
//	 var imageDom = document.createElement("img");
//	 imageDom.setAttribute("src","../images/2.gif");
//     marker.setPosition(newpoint);
//     marker.setElement(imageDom);
//     console.log(marker);
//}
//var sss = false,MarkVectorLayer=null;
//var addMarkerText= function(){
//  var lng= 120.38247 + Math.random() * 003;
//  var lat= 36.26491 + Math.random() * 004;
//  var newpoint = ol.proj.transform([lng,lat], 'EPSG:4326', 'EPSG:3857');
//
//  var iconFeature = new ol.Feature({
//	  geometry: new ol.geom.Point(newpoint),
//	  name: 'Null Island'
//	});
//	var iconStyle = new ol.style.Style({
//	  image: new ol.style.Icon({
//	    anchor: [0.5, 46],
//	    anchorXUnits: 'fraction',
//	    anchorYUnits: 'pixels',
//	    opacity: 0.75,
//	    src: '../images/3.gif'
//	  })
//	});
//	//iconFeature.setStyle();
//	var vectorSource = new ol.source.Vector({
//	  features: [iconFeature]
//	});
//	if(!sss){
//		MarkVectorLayer = new ol.layer.Vector({
//	    	source: vectorSource,
//	    	style : iconStyle
//		});
//		sss = true;
//	    map.addLayer(MarkVectorLayer);
//	}else{
//	    vectorSource.addFeatures(iconFeature);
//	}
//}
//
////添加折线的方法
//var vectorRedLayer = null,vectorRedLine = null;
//var addPolyline = function(){
//       var points = [[ 116.127122,31.120342],[114.141822,36.415312],[113.117122,31.715332]];
//       var pointB = ol.proj.transform(points[0], 'EPSG:4326', 'EPSG:3857');
//       var pointC = ol.proj.transform(points[1], 'EPSG:4326', 'EPSG:3857');
//       var pointD = ol.proj.transform(points[2], 'EPSG:4326', 'EPSG:3857');
//       var pointList =[pointB,pointC,pointD];
//       console.log(pointList);
//      var featureRed = new ol.Feature({
//	      geometry: new ol.geom.LineString(pointList)
//	  });
//	  vectorRedLine = new ol.source.Vector({});
//      vectorRedLine.addFeature(featureRed);
//	  vectorRedLayer = new ol.layer.Vector({
//		    source: vectorRedLine,
//		    style: new ol.style.Style({
//		        stroke: new ol.style.Stroke({
//		            color: '#FF0000',
//		            width: 4
//		        }),
//		        fill: new ol.style.Fill({
//		            color: '#FF0000',
//		            weight: 1
//		        })
//		    })
//		});
//     map.addLayer(vectorRedLayer);
//}
////删除线
//var removePolyline = function(){
//     console.log(vectorRedLayer,vectorRedLine);
//     map.removeLayer(vectorRedLayer);
//}
//
////删除点
//var removeAllPoint = function(){
//	for (var i = 0; i < List.length; i++) {
//		 var overlayers = List[i];
//		 console.log(overlayers);
//		 map.removeOverlay(overlayers);
//	}
//}
//
////删除点
//var removePoint = function(){
//	 console.log(map);
//     map.removeOverlay(marker);
//}
//
//
//var myInfoWindow=function(cfg){
//	var title =cfg["title"],content =cfg["content"] ,width = cfg["width"],width = cfg["scope"];
//	//自定义信息窗口
//	var info = document.createElement("div");
//	info.className = "infoWindow";
//	info.style.width = width || 180;
//	// 定义顶部标题
//	  var top = document.createElement("div");
//	  top.className = "infoWindow-top";
//	  var titleD = document.createElement("div");
//	  titleD.innerHTML = title || "标题";
//	  var closeX = document.createElement("img");
//	  closeX.style.cursor="pointer";
//	  closeX.src = "images/infowindow/close2.gif";
//	  closeX.onclick = function(){
//	      console.log(this);
//	  };
//	top.appendChild(titleD);
//	top.appendChild(closeX);
//	info.appendChild(top);
//	// 定义中部内容
//	var middle = document.createElement("div");
//	middle.className = "infoWindow-middle";
//	middle.style.backgroundColor='white';
//	middle.innerHTML = content || "";
//	info.appendChild(middle);
//	// 定义底部内容
//	var bottom = document.createElement("div");
//	bottom.className = "infoWindow-bottom";
//	bottom.style.position = 'relative';
//	bottom.style.top = '0px';
//	bottom.style.margin = '0 auto';
//	var sharp = document.createElement("img");
//	sharp.src = "images/infowindow/sharp.png";
//	bottom.appendChild(sharp);	
//	info.appendChild(bottom);
//	return info;
//}
//var openInfow = function(marker){
//    var popup = new ol.Overlay.Popup();
//	map.addOverlay(popup);
//	map.on('singleclick', function(evt) {
//	    popup.show(evt.coordinate, {
//	       title : "我的标题",
//	       content :"我的标题"
//	    });
//	});
//}
//
//
////添加多边形
var vectorLayerPolygon= null;
var addPolygon = function(){
	   var polyFeature = new ol.Feature({
		    geometry: new ol.geom.Polygon([
		        [
		            [116.727122,31.120342],
		      		[114.109822,36.115312],
		       		[113.117022,31.115332],
		       		[116.227122,30.115332]
		        ]
		    ])
		});
		polyFeature.getGeometry().transform('EPSG:4326', 'EPSG:3857'); 
		vectorLayerPolygon = new ol.layer.Vector({
		    source: new ol.source.Vector({
		        features: [polyFeature]
		    })
		});
        map.addLayer(vectorLayerPolygon);
}

var removePolygon = function(){
    console.log(vectorLayerPolygon);
	map.removeLayer(vectorLayerPolygon);
}


//var pantTo=function(){
//   map.getView().setCenter(point);
//}
//
//var zoomIn =function(){
//	var zoom = map.getView().getZoom();
//	zoom ++; 
//	map.getView().setZoom(zoom);
//}
//var zoomOut =function(){
//	var  zoom= map.getView().getZoom();
//	zoom --; 
//	console.log(zoom);
//	map.getView().setZoom(zoom);
//}
//
//var draw;
//function DrawPolygon() {
//	var sources = new ol.source.Vector();
//	  draw = new ol.interaction.Draw({
//	    	source: sources,
//	    	type: "Polygon"
//	  });
//	  draw.on("drawend",function(evt){
//	        var coord = evt.feature.getGeometry().getCoordinates()[0][0];
//	        var lonlat = ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
//	  		map.removeInteraction(draw);
//	  },this);
//	  map.addInteraction(draw);
//}
////多边形在视野方位内显示
//var DrawPolygonFitView=function(){
//	var gon = vectorLayerPolygon.getSource().getFeatures()[0];
//   var plig = gon.getGeometry();
//   console.log(plig);
//   map.getView().fitGeometry(plig,map.getSize());
//}
//
////多边形在视野方位内显示
//var DrawPolylineFitView=function(){
//	var gon = vectorRedLayer.getSource().getFeatures()[0];
//   var plig = gon.getGeometry();
//   console.log(plig);
//   map.getView().fitGeometry(plig,map.getSize());
//}
////多边形在视野方位内显示
//var DrawPolylineFitView=function(){
//	var gon = vectorRedLayer.getSource().getFeatures()[0];
//    var plig = gon.getGeometry();
//    console.log(plig);
//    map.getView().fitGeometry(plig,map.getSize());
//}
/***
 * 测距tools
 * @type String
 */
ol.DrawEventType.DRAWING = "drawing";

ol.interaction.Draw.prototype.addToDrawing_ = function(event) {
  var coordinate = event.coordinate;
  var geometry = this.sketchFeature_.getGeometry();
  var coordinates;
  if (this.mode_ === ol.interaction.DrawMode.LINE_STRING) {
    this.finishCoordinate_ = coordinate.slice();
    goog.asserts.assertInstanceof(geometry, ol.geom.LineString);
    coordinates = geometry.getCoordinates();
    coordinates.push(coordinate.slice());
    geometry.setCoordinates(coordinates);
  } else if (this.mode_ === ol.interaction.DrawMode.POLYGON) {
    this.sketchPolygonCoords_[0].push(coordinate.slice());
    goog.asserts.assertInstanceof(geometry, ol.geom.Polygon);
    geometry.setCoordinates(this.sketchPolygonCoords_);
  }
  this.updateSketchFeatures_();
  this.dispatchEvent(new ol.DrawEvent(ol.DrawEventType.DRAWING,this.sketchFeature_));
};



var sketch;
var measureTooltip;
var pointerMoveHandler = function(evt) {
  if (evt.dragging) {
    return;
  }
  var tooltipCoord = evt.coordinate;
  if (sketch) {
	    var geom =  sketch.getGeometry();
	    if (geom instanceof ol.geom.LineString) {
	      var output = formatLength(geom.getLength());
	      tooltipCoord = geom.getLastCoordinate();
	      measureTooltip.getElement().innerHTML = output;
	      measureTooltip.setPosition(tooltipCoord);
	      console.log("drawmoving");
	    }
  }
};


var draw,drawOverlayer = null,vector=null;
function rectTools() {
  clearTools();
  vector = new ol.layer.Vector({
 	 	source: new ol.source.Vector()
  });
  map.addLayer(vector);
  draw = new ol.interaction.Draw({
    source: vector.getSource(),
    type:"LineString"
  });
  
  map.addInteraction(draw);
 
  createMeasureTooltip();

  draw.on('drawstart', function(evt) {
	  sketch = evt.feature; 
	  console.log("drawstart");
  });
  
  draw.on('drawing', function(evt) {
	  pointerMoveHandler(evt);
  });

  draw.on('drawend', function(evt) {
  	 measureTooltip.getElement().className = 'tooltip tooltip-static';
     map.removeInteraction(draw);
 });
}

var formatLength = function(length) {
  if (length > 100) {
    	output = (Math.round(length / 1000 * 100) / 100) + 'km';
  } else {
    	output = (Math.round(length * 100) / 100)+ 'm';
  }
  return output;
};
var clearTools = function(){
	 if(measureTooltip){
  	   	map.removeOverlay(measureTooltip);
     }
     if(vector){
     	console.log("clearTools");
        map.removeLayer(vector);
        vector
     }
}

function createMeasureTooltip() {
	 var helpTooltipElement = document.getElementById("openlayer_tools_mouse");
	 if(helpTooltipElement==null){
		  helpTooltipElement = document.createElement('div');
		  helpTooltipElement.className = 'tooltip';
		  helpTooltipElement.id = "openlayer_tools_mouse";
	  }
	  helpTooltipElement.className = 'tooltip tooltip-measure';
	  
	  measureTooltip = new ol.Overlay({
	    element: helpTooltipElement,
	    offset: [0, -8],
	    positioning: 'bottom-center'
	  });
	  map.addOverlay(measureTooltip);
}


