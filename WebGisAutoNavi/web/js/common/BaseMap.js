(function(){
	/**
	 * 地图基类
	 */
	GAS.BaseMap = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.barTitle = config["barTitle"] ;
	      	    this.showDrawBtn = config["drawgon"] || false;
	      	    this.showClearBtn = config["showClearBtn"] || false;
	      	    this.mapFlag = "MapView";
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
	      	    this.minZoom = 4;
	      	    this.maxZoom = 19;
	      	    this.defaultZoom = 16;
	      	    this.baseLayerUrl = GAS.TitleURL["tiandituUrl"];//mapTileUrl【正式地图】 mapTestUrl【测试地图】
	      	    this.addEvents("maprender","resize");
	      	    this.defaultCenter= [36.26491614849219,120.38247585296631];
	      	    this.layerGroup = null;
				GAS.BaseMap.superclass.constructor.apply(this, arguments);
		   },
		   getBaseLayer:function(){
		      return this.baseTileLayer;
		   },
		   //初始化地图
		   initMap: function() {
				if (!this.map) {
					 var mapId = this.mapPanelId;
	                 var layer = this.getBaseTileLayer();
	            	 this.map= L.map(mapId, {layers: [layer],contextmenu :false}).setView(this.defaultCenter, this.defaultZoom);
	            	 window["TestMap"] = this.map;
	            	 this.layerGroup = L.layerGroup();
	            	 var qdlayers =  GAS.config["mapTileUrl"]; //青岛栅格地址
	            	 GAS.MapGooGle =  this.map;
	            	 this.addMaplayers(qdlayers);
	            	 this.fireEvent("maprender",this.map);
				}
				return this.map;
			},
			//
			getBaseTileLayer:function(){
			    this.baseTileLayer= L.tileLayer(this.baseLayerUrl,{
                     minZoom: this.minZoom,
                     maxZoom: this.maxZoom
                });
				return this.baseTileLayer;
			},
			//创建图标
			createIcon : function(cfg){
			      var myIcon = L.icon({
				    iconUrl: cfg["iconUrl"],
				    iconSize:  cfg["iconSize"] || [17, 17],
				    iconAnchor:  cfg["iconAnchor"] || [9, 9],
				    popupAnchor: cfg["popupAnchor"] || [-3, -75]
			    });
				return myIcon;
			},
			//添加点
			addMaplayers:function(url){
			   var layer = L.tileLayer(url,{
                 minZoom: 15,
                 maxZoom: 19
               }).addTo(this.map);
			   return layer;
			},
			addMarker:function(cfg){
			   var point = cfg["lnglat"];
			   cfg["draggable"] = cfg["draggable"] || false;
			   var myIcon = this.createIcon(cfg);
			   cfg["icon"] = myIcon;
			   var marker =  L.marker(point,cfg);
			   this.addOverLayers(marker);
			   if(cfg["clickEnable"]){
			   	   var text = cfg["contentMsg"],me = this;
			   	   marker.on("click",function(e){
			   	   	    var opp = L.popup().setLatLng(point).setContent(text).openOn(me.map);
			   	   	    me.popWin = opp;
			   	   });
			   }
			   return marker;
			},
			// 更新气泡信息
			setMarkerPop:function(point,content){
			   if(this.popWin){
			   	  this.popWin.setLatLng(point);
			      this.popWin.setContent(content);
			   }
			},
			//居中
			moveToCenter:function(point){
			    this.map.panTo(point);
			},
			//创建点
			createPoint : function(lng,lat){
				return L.latLng(lng,lat);
			},
			//添加覆盖物
			addOverLayers:function(overlayer){
			    this.layerGroup.addLayer(overlayer).addTo(this.map);
			    return overlayer;
			},
			//创建折线
			/**
			 * cfg{
			 *    point  :  [] ,
			 *    lineOption : {
			 *         color: 'blue',
			 *         weight: 2,
			 *         ......
			 *         ......
			 *    }
			 * }
			 */
			createPolyLine:function(cfg){
				var points = cfg["points"];
				var option = cfg["lineOption"] || {};
				Ext.apply({
					color: 'blue', 
					weight: 2
				},option);
			    var polyline = L.polyline(points, option);
			    return polyline;
			},
			//创建多边形
			/**
			 * cfg{
			 *    point  :  [] ,
			 *    lineOption : {
			 *         color: 'blue',
			 *         weight: 2,
			 *         ......
			 *         ......
			 *    }
			 * }
			 */
			createPolygon:function(cfg){
				var points = cfg["points"];
				var option = cfg["polyOption"] || {};
				Ext.apply({
					color: 'blue', 
					weight: 2
				},option);
			    var polygon = new L.Polygon(points, option);
			    return polygon;
			},
			//添加多边形
			addPolygon:function(cfg,flag){
			   var polygon = this.createPolygon(cfg);
			   this.addOverLayers(polygon);
			   if(flag && flag == true){
			   	  this.map.fitBounds(polygon.getBounds());
			   }
			   return polygon;
			},
			//添加线
			addPolyLine:function(cfg){
			   var polyline = this.createPolyLine(cfg);
			   this.addOverLayers(polyline);
			   this.map.fitBounds(polyline.getBounds());
			   return polyline;
			},
			//清除地图所有的地图覆盖物
			clearAllMap:function(){
				this.layerGroup.clearLayers();
			},
			//移除覆盖物
			removeOverLayer:function(overlay){
				if(overlay){
					this.layerGroup.removeLayer(overlay);
				}
			},
			//添加图层
			addMapLayer:function(layer){
				this.map.addLayer(layer);
			},
			//删除图层
			removeMapLayer:function(layer){
			   if(layer){
					this.map.removeLayer(layer);
			   }
			},
			getMap:function(){
			   return this.map;
			},
			//判断点在bound
			isPointInBounds:function(point){
		        var currentBound = this.map.getBounds();
		        return currentBound.contains(point);
			},
			getMapContainer : function(){
				if (!this.mapcontainer) {
					 this.mapcontainer = new Ext.Container({
						frame  :  true,
						hideBorders:false,
						listeners: {
							scope: this,
							afterRender: function(panel) {
							    this.mapPanelId = panel.id;
								var filelist = [
									GAS.config.rootPath + "lib/leaflet/leaflet.js"
								];
								GAS.LoadScript.load(filelist, this.initMap, this, false);
							}
						}
					});
				}
				return this.mapcontainer;
			},
			getMapbarTile : function(){
			   return this.barTitle || "地图工具栏";
			},
			getMapBtn:function(){
			   if(!this.drawGon){
			       this.drawGon = new Ext.Button({
			                tooltip: "绘制区域",
							iconCls: "icon-loyon",
							name:"pointer",
							scope: this,
							pressed:false,
							type:"6",
							handler: this.drawGonhandler
			       });
			   }
			   return this.drawGon;
			},
			getClearBtn:function(){
			   if(!this.clearBtn){
			       this.clearBtn = new Ext.Button({
			                tooltip: "清除",
							iconCls: "icon-clear",
							name:"clear",
							scope: this,
							pressed:false,
							type:"clear",
							handler: this.clearAll
			       });
			   }
			   return this.clearBtn;
			},
			//清除操作
			clearAll:function(){
			   this.clearAllMap();
			},
			//绘制多边形
			drawGonhandler:function(){
				 this.clearAllMap(); //清除所有的覆盖物对象
				 var filelist = [
					GAS.config.rootPath + "lib/leafletDraw/leaflet.draw.js",
					GAS.config.rootPath + "lib/fix/Draw.ToolTip.js"
				 ],me= this,map = this.getMap();
				 //回调函数
				 var loadScriptHandler = function(){
				 	  var polygon =  new L.Draw.Polygon(map, {
					        showArea: false,
							shapeOptions: {
								color: 'red'
							}
					 });
					 polygon.enable();
					 map.on('draw:created', function (e) {
						var layer = e.layer;
						me.polygonLayer = layer;
						me.addOverLayers(layer);
					});
				 };
				 GAS.LoadScript.load(filelist, loadScriptHandler, this, false);
			},
			//获取当前绘制的多边形覆盖物对象
			getDrawPolygonLayers:function(){
			    return this.polygonLayer;
			},
			//获取多边形的经纬度
			getDrawPolugonLatLngs:function(){
			    var layers  = this.getDrawPolygonLayers();
			    return layers["_latlngs"];
			},
			//获取多边形的经纬度
			getDrawPolugonLatLngList:function(){
			    var list  = this.getDrawPolugonLatLngs();
			    var newList = [];
			    if(list && list.length > 0){
			    	var len = list.length;
			        for (var i = 0; i < len; i++) {
			        	var lnglat = list[i];
			        	newList.push(lnglat["lng"]+","+lnglat["lat"]);
			        }
			        var lastlnglat = list[0];
		        	newList.push(lastlnglat["lng"]+","+lastlnglat["lat"]);
			    }
			    return newList;
			},
			//显示多边形["PS"] 首尾坐标相连，设计如此
			showDrawPolugon:function(listArr){
			   var array = listArr?listArr.split("@"):[];
			   if(array.length > 0){
			   	  var newPoints = [];
			      for (var i = 0; i < array.length; i++) {
			      	  var lnglatStr= array[i],
			      	  lnglatArr= lnglatStr.split(",");
			      	  var lnglat = this.createPoint(lnglatArr[1],lnglatArr[0]); 
			      	  //去掉最后一个坐标【去掉】
			      	  if(i!= array.length -1){
			      	     newPoints.push(lnglat);
			      	  }
			      }
			      this.polygonLayer=this.addPolygon({
			      	 points : newPoints
			      },false);
			   }
			},
			polylineFitInView:function(polyline){
				if(polyline){
				   this.map.fitBounds(polyline.getBounds());
				}
			},
			setView:function(lnglat,zoom){
			   zoom = zoom ? zoom : this.map.getZoom();
			   this.map.setView(lnglat,zoom);
			},
			//放大
			MapZoomIn: function(){
			   this.map.zoomIn();
			},
			//缩小
			MapZoomOut: function(){
			   this.map.zoomOut();
			},
			//返回初始点
			resetMapCenter: function(){
			   var lnglat = this.defaultCenter;
			   var zoom =  this.defaultZoom;
			   this.setView(lnglat,zoom);
			},
			//鼠标平移
			mouseTranslation:function(){
			    var container = this.map.getContainer();
			    container.style.cursor = '';
			},
			mousePointer:function(){
			    var container = this.map.getContainer();
			    container.style.cursor = 'default';
			},
			//鼠标工具类
			maptoolbarHander:function(btn){
			     var btnType  = btn["type"];
			     if(btnType == "6"){
			         this.mousePointer();
			     }else if(btnType == "7"){ //放大
			         this.MapZoomIn();
			     }else if(btnType == "8"){ //缩小
			     	 this.MapZoomOut();
			     }else if(btnType == "9"){ //平移
			     	 this.mouseTranslation();
			     }else if(btnType == "10"){ //返回初始点
			     	 this.resetMapCenter();
			     }
			},
			getCommonMapOper:function(){
			   return [{
							tooltip: "指针",
							iconCls: "icon-pointer",
							name:"pointer",
							scope: this,
							pressed:false,
							type:"6",
							handler: this.maptoolbarHander
						},
						{xtype: "tbseparator"},
						{
							style: 'height:20px;width:20px;',
							tooltip: '放大',
							iconCls: "icon-zoom-out",
							scope: this,
							pressed:false,
							type:"7",
							handler: this.maptoolbarHander
						},
						{xtype: "tbseparator"},
						{
							tooltip: "缩小",
							iconCls: "icon-zoom-in",
							scope: this,
							type:"8",
							pressed:false,
							handler: this.maptoolbarHander
						}, 
						{xtype: "tbseparator"},
						{
							tooltip: "平移",
							iconCls: "icon-hand",
							scope: this,
						    pressed:false,    
							type:"9",
							handler: this.maptoolbarHander
						}, 
						{xtype: "tbseparator"},
						{
							tooltip: "返回初始点",
							scope: this,
							pressed:false,
							type:"10",
							iconCls: "icon-show-full",
							handler: this.maptoolbarHander
						}];
				
			},
			getShowButtomMap:function(){
			     var btn = new Ext.form.Checkbox({
		            boxLabel : "隐藏底图",
		            scope : this,
		            listeners:{
		            	scope :this,
		            	check:function(check,flag){
				      	   var showMap = flag,layer = this.getBaseLayer().getContainer();;
				      	   if(flag){
				      	      layer.style.display ="none";
				      	      GAS.Cookie.setCookie(this.mapFlag,flag);
				      	   }else{
				      	   	  layer.style.display ="";
				      	   	  GAS.Cookie.setCookie(this.mapFlag,flag);
				      	   }
			            }
		            }
		          });
		          return btn;
			},
			getMapToolBar:function(){
				 if(!this.baritems){
				 	  this.baritems = ["<b style='color:#15428b;'>"+this.getMapbarTile()+"</b>",{
							xtype: 'tbfill'
						 },
						 this.showDrawBtn?this.getMapBtn():"", //显示画多边形,
						 this.showClearBtn?this.getClearBtn():"", //显示画多边形
						 this.getShowButtomMap()].concat(this.getCommonMapOper());
				 }
				 return  this.baritems;
			},
			getMainMap:function(){
			   if (!this.mainMap) {
					 this.mainMap = new Ext.Panel({
						border : false,
						frame  : false,
						layout : "fit",
						tbar   : this.getMapToolBar(),
						items  : this.getMapContainer(),
						listeners :{
						   scope : this,
						   resize :function(a){
						   	      var layers = this.getBaseLayer();
						   	      if(layers){
						   	          layers.redraw();
						   	      }
						   }
						}
					});
				}
				return this.mainMap;
			},
			getMainPanel: function() {
				if (!this.mainpanel) {
					 this.mainpanel = new Ext.Panel({
						border : false,
						frame  : false,
						layout : "fit",
						items  : this.getMainMap()
					});
				}
				return this.mainpanel;
			}
	});
	
})();



