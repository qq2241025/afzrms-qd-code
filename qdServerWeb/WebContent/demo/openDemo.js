var map;
var lnglat = [120.38247, 36.26491];
var point = ol.proj.transform(lnglat, 'EPSG:4326', 'EPSG:3857');
var image = "/images/flager.gif";
var marker = null, layer = null;
var attribution = new ol.Attribution({
	html : 'Tiles &copy; <a href="http://maps.nls.uk/townplans/glasgow_1.html">'
			+ 'National Library of Scotland</a>'
});

window.onload = function() {
	var lnglat = [120.38262525, 36.26008076];
	// var lnglat = [120.38247,36.26491];
	var point = ol.proj.transform(lnglat, 'EPSG:4326', 'EPSG:3857');
	console.log(point);
	layer = new ol.layer.Tile({
		source : new ol.source.XYZ({
			attributions : [new ol.Attribution({
						html : '君图科技'
					})],
			url : "http://t4.tianditu.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILECOL={x}&TILEROW={y}&TILEMATRIX={z}"
		}),
		min : 4
	});

	map = new ol.Map({
		target : "iCenter",
		controls : ol.control.defaults({
					attributionOptions : ({
						collapsible : false
					})
				}),
		layers : [layer, new ol.layer.Tile({
			source : new ol.source.XYZ({
						attributions : [attribution],
						url : 'http://127.0.0.1:8080/map/mapServices?x={x}&y={y}&z={z}'
					})
		})],
		view : new ol.View({
					center : point,
					zoom : 15,
					minZoom : 4,
					maxZoom : 19
				})
	});

	console.info("map", map.getView(), map, "color:blue");
	var imageDom = document.createElement("img");
	imageDom.setAttribute("src", "../images/1.gif");
	imageDom.style.cursor = "pointer";
	marker = new ol.Overlay({
				position : point,
				stopEvent : false,
				element : imageDom
			});
	console.info(marker, marker.getPosition());
	console.warn("layer======", layer.getExtent());
	map.addOverlay(marker);

	imageDom.addEventListener("click", function(e) {
				console.log("marker======", marker);
				var post = marker.getPosition();
				var popup = new ol.Overlay.Popup();
				map.addOverlay(popup);
				popup.show(post, {
							title : "我的标题",
							content : "我的标题"
						});
			});
}
// 添加点
var vectorLayer = null, List = [];
var addmarkerHandlet = function() {
	var lng = 120.38247 + Math.random() * 003;
	var lat = 36.26491 + Math.random() * 004;
	var newpoint = ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857');
	var imageDom = document.createElement("img");
	imageDom.setAttribute("src", "../images/1.gif");
	var markerA = new ol.Overlay({
				position : newpoint,
				element : imageDom
			});
	map.addOverlay(markerA);
	List.push(markerA);
}

hideMap = function() {
	layer.setVisible(false);
}
showMap = function() {
	layer.setVisible(true);
}
window.resize = function() {
	console.log("resize", layer);
	layer.redraw(true);
}
clearMap = function() {
	console.log(layer.getSource());
}

// 判断点在bound
isPointInBounds = function() {
	var size = map.getSize();
	var bounts = map.getView().calculateExtent(size);
	var fla = ol.extent.containsCoordinate(bounts, point);
	console.log(fla);
	return fla;
};

setMarker = function() {
	var lng = 120.38247 + Math.random() * 003;
	var lat = 36.26491 + Math.random() * 004;
	var newpoint = ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857');
	var imageDom = document.createElement("img");
	imageDom.setAttribute("src", "../images/2.gif");
	marker.setPosition(newpoint);
	marker.setElement(imageDom);
	console.log(marker);
}
var sss = false, MarkVectorLayer = null;
var addMarkerText = function() {
	var lng = 120.38247 + Math.random() * 003;
	var lat = 36.26491 + Math.random() * 004;
	var newpoint = ol.proj.transform([lng, lat], 'EPSG:4326', 'EPSG:3857');

	var iconFeature = new ol.Feature({
				geometry : new ol.geom.Point(newpoint),
				name : 'Null Island'
			});
	var iconStyle = new ol.style.Style({
				image : new ol.style.Icon({
							anchor : [0.5, 46],
							anchorXUnits : 'fraction',
							anchorYUnits : 'pixels',
							opacity : 0.75,
							src : '../images/3.gif'
						})
			});
	// iconFeature.setStyle();
	var vectorSource = new ol.source.Vector({
				features : [iconFeature]
			});
	if (!sss) {
		MarkVectorLayer = new ol.layer.Vector({
					source : vectorSource,
					style : iconStyle
				});
		sss = true;
		map.addLayer(MarkVectorLayer);
	} else {
		vectorSource.addFeatures(iconFeature);
	}
}

// 添加折线的方法
var vectorRedLayer = null, vectorRedLine = null;
var addPolyline = function() {
	var points = [[116.127122, 31.120342], [114.141822, 36.415312],
			[113.117122, 31.715332]];
	var pointB = ol.proj.transform(points[0], 'EPSG:4326', 'EPSG:3857');
	var pointC = ol.proj.transform(points[1], 'EPSG:4326', 'EPSG:3857');
	var pointD = ol.proj.transform(points[2], 'EPSG:4326', 'EPSG:3857');
	var pointList = [pointB, pointC, pointD];
	console.log(pointList);
	var featureRed = new ol.Feature({
				geometry : new ol.geom.LineString(pointList)
			});
	vectorRedLine = new ol.source.Vector({});
	vectorRedLine.addFeature(featureRed);
	vectorRedLayer = new ol.layer.Vector({
				source : vectorRedLine,
				style : new ol.style.Style({
							stroke : new ol.style.Stroke({
										color : '#FF0000',
										width : 4
									}),
							fill : new ol.style.Fill({
										color : '#FF0000',
										weight : 1
									})
						})
			});
	map.addLayer(vectorRedLayer);
}
// 删除线
var removePolyline = function() {
	console.log(vectorRedLayer, vectorRedLine);
	map.removeLayer(vectorRedLayer);
}

// 删除点
var removeAllPoint = function() {
	for (var i = 0; i < List.length; i++) {
		var overlayers = List[i];
		console.log(overlayers);
		map.removeOverlay(overlayers);
	}
}

// 删除点
var removePoint = function() {
	console.log(map);
	map.removeOverlay(marker);
}

var myInfoWindow = function(cfg) {
	var title = cfg["title"], content = cfg["content"], width = cfg["width"], width = cfg["scope"];
	// 自定义信息窗口
	var info = document.createElement("div");
	info.className = "infoWindow";
	info.style.width = width || 180;
	// 定义顶部标题
	var top = document.createElement("div");
	top.className = "infoWindow-top";
	var titleD = document.createElement("div");
	titleD.innerHTML = title || "标题";
	var closeX = document.createElement("img");
	closeX.style.cursor = "pointer";
	closeX.src = "images/infowindow/close2.gif";
	closeX.onclick = function() {
		console.log(this);
	};
	top.appendChild(titleD);
	top.appendChild(closeX);
	info.appendChild(top);
	// 定义中部内容
	var middle = document.createElement("div");
	middle.className = "infoWindow-middle";
	middle.style.backgroundColor = 'white';
	middle.innerHTML = content || "";
	info.appendChild(middle);
	// 定义底部内容
	var bottom = document.createElement("div");
	bottom.className = "infoWindow-bottom";
	bottom.style.position = 'relative';
	bottom.style.top = '0px';
	bottom.style.margin = '0 auto';
	var sharp = document.createElement("img");
	sharp.src = "images/infowindow/sharp.png";
	bottom.appendChild(sharp);
	info.appendChild(bottom);
	return info;
}
var openInfow = function(marker) {
	var popup = new ol.Overlay.Popup();
	map.addOverlay(popup);
	map.on('singleclick', function(evt) {
				popup.show(evt.coordinate, {
							title : "我的标题",
							content : "我的标题"
						});
			});
}

// 添加多边形
var vectorLayerPolygon = null;
var addPolygon = function() {
	var polyFeature = new ol.Feature({
				geometry : new ol.geom.Polygon([[[116.727122, 31.120342],
						[114.109822, 36.115312], [113.117022, 31.115332],
						[116.227122, 30.115332]]])
			});
	polyFeature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
	vectorLayerPolygon = new ol.layer.Vector({
				source : new ol.source.Vector({
							features : [polyFeature]
						})
			});
	map.addLayer(vectorLayerPolygon);
}
var pantTo = function() {
	map.getView().setCenter(point);
}

var zoomIn = function() {
	var zoom = map.getView().getZoom();
	zoom++;
	map.getView().setZoom(zoom);
}
var zoomOut = function() {
	var zoom = map.getView().getZoom();
	zoom--;
	console.log(zoom);
	map.getView().setZoom(zoom);
}

var draw, ploginList = null;
function DrawPolygon() {
	var featureOverlay = new ol.FeatureOverlay({
				style : new ol.style.Style({
							fill : new ol.style.Fill({
										color : 'rgba(55, 155, 55, 0.3)'
									}),
							stroke : new ol.style.Stroke({
										color : '#B15BFF',
										width : 2
									}),
							image : new ol.style.Circle({
										radius : 7,
										fill : new ol.style.Fill({
													color : '#ffcc33'
												})
									})
						})
			});
	featureOverlay.setMap(map);
	draw = new ol.interaction.Draw({
				features : featureOverlay.getFeatures(),
				type : "Polygon"
			});
	if (ploginList) {
		ploginList.clear();
	}
	draw.on("drawend", function(evt) {
				ploginList = featureOverlay.getFeatures();
				var coord = evt.feature.getGeometry().getCoordinates()[0][0];
				console.log(coord);
				var lonlat = ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
				map.removeInteraction(draw);
			}, this);
	map.addInteraction(draw);
}
// 多边形在视野方位内显示
var DrawPolygonFitView = function() {
	var gon = vectorLayerPolygon.getSource().getFeatures()[0];
	var plig = gon.getGeometry();
	console.log(plig);
	map.getView().fitGeometry(plig, map.getSize());
}

// 多边形在视野方位内显示
var DrawPolylineFitView = function() {
	var gon = vectorRedLayer.getSource().getFeatures()[0];
	var plig = gon.getGeometry();
	console.log(plig);
	map.getView().fitGeometry(plig, map.getSize());
}
// 多边形在视野方位内显示
var DrawPolylineFitView = function() {
	var gon = vectorRedLayer.getSource().getFeatures()[0];
	var plig = gon.getGeometry();
	console.log(plig);
	map.getView().fitGeometry(plig, map.getSize());
}
