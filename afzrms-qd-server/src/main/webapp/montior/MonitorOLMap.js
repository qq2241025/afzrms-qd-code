(function(){
	/**
	 * 地图基类
	 * PS : 地图经纬度 * 1E5 = 100000
	 */
	GAS.BaseMap = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    this.config = config || {};
	      	    this.barTitle = config["barTitle"] ;
	      	    this.showDrawBtn = config["drawgon"] || false;
	      	    this.showClearBtn = config["showClearBtn"] || false;
	      	    this.hideBaseMapBtn = config["hideBaseMapBtn"] || false;
	      	    this.hideTools = config["hideTools"] || false;
	      	    this.mapRender = config["mapRender"] ;
	      	    this.mapFlag = "MapView";
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
	      	    this.minZoomleval = 10;
	      	    this.maxZoom = 19;
	      	    this.rotation = 158.8904818103391; //默认旋转角度
	      	    this.defaultZoom = 15;
	      	    this.openHandelr = GAS.config["OpenMousehandler"];
	      	    this.closeHandelr = GAS.config["CloseMouseHandler"];
	      	    this.baseLayerUrl = GAS.TitleURL["googleUrl"];//mapTileUrl【正式地图】 mapTestUrl【测试地图】
	      	    this.addEvents("maprender","resize");
	      	    this.defaultCenter= [120.37680781073895, 36.26544625760539];
				GAS.BaseMap.superclass.constructor.apply(this, arguments);
		   },
		   getBaseLayer:function(){
		      return this.baseTileLayer;
		   },
		   //添加气泡
		   addInfoWindowOnMap:function(){
		        this.infoWindow = new ol.Overlay.Popup();
				this.addOverLayers(this.infoWindow);
		   },
		   //获取气泡的方法【暴露出去的】
		   getMapInfoWindow : function(){
		       return this.infoWindow;
		   },
		   //隐藏气泡
		   hideMapInfoWindow:function(){
		   	  this.infoWindow.hide();
		   },
		   //旋转dom
		   rotateDom:function(htmlDom,rotate){
			    if(htmlDom){
			        htmlDom.style.rotation = rotate + "deg";
			        htmlDom.style.webkitTransform="rotate("+rotate+"deg)"
					htmlDom.style.MozTransform="rotate("+rotate+"deg)"
					htmlDom.style.msTransform="rotate("+rotate+"deg)"
					htmlDom.style.OTransform="rotate("+rotate+"deg)"
					htmlDom.style.transform="rotate("+rotate+"deg)";
			    }else{
			    	GAS.log("不支持旋转属性");
			    }
		   },
		   
		   //获取地图的旋转角度
		   getMapRotation:function(){
		       return this.map.getView().getRotation();
		   },
		   //初始化地图【替换OK】
		   initMap: function() {
				if (!this.map) {
					 var point = this.createPoint(this.defaultCenter[0],this.defaultCenter[1]);
	                 var layer = this.getBaseTileLayer();
	                 var qdTileLayer = this.getQDMaplayers(GAS.config["mapTileUrl"]); //青岛机场的图层
	            	 this.map = new ol.Map({
	            	       target  : this.mapPanelId,
						   controls: ol.control.defaults({
						   	   attribution : false,//不显示地图版权问题
						   	   rotate: false //不显示旋转图标
						   }),
						   layers:[layer,qdTileLayer],
	            	 	   view: new ol.View({
						     center: point,
						     zoom  : this.defaultZoom ,
						     rotation: this.rotation,
						     minZoom: this.minZoomleval,
    						 maxZoom: this.maxZoom
						  })
	            	 });
	            	 this.getMainLoadMask().hide();
	            	 window["BMap"] = this.map;
	            	 this.addInfoWindowOnMap(); //添加气泡在地图上,显示和隐藏更新气泡内容
	            	 this.fireEvent("maprender",this.map);
	            	 if(this.mapRender){
	            	 	this.mapRender.apply(this,[this.map]);
	            	 }
				}
				return this.map;
			},
			MarkerPointer : function(callback,scope){
				  this.map.removeAllListeners();
			      this.pointerMarkerEvent = this.map.addEventListener("click",function(e){
				    var coordinate = e.coordinate;
				    var lnglat = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
				 	if(callback){
				 	   callback.apply(scope || this,[{lng:lnglat[0],lat:lnglat[1],coordinate:coordinate}]);
				 	}
				 });
			},
			//创建图层【替换OK】
			getBaseTileLayer:function(){
				this.baseTileLayer = new ol.layer.Tile({
				      source: new ol.source.XYZ({
				        url: this.baseLayerUrl
				      })
			    });
				return this.baseTileLayer;
			},
			//图层【添加青岛图层】
			getQDMaplayers:function(url){
			   if(!url){
			     throw  new Error("无效的URL地址");
			   }
			   var layer = new ol.layer.Tile({
				      source: new ol.source.XYZ({
				        	url: url
				      })
			    });
			   return layer;
			},
			addMarkerLabel:function(cfg){
			     var point = cfg["point"];
			     var offsetTop = cfg["offsetTop"] || 0;
		   	     var textLabel= cfg["labelText"] || "",fontSize =10;
		   	     var typeColor = cfg["fontColor"] || "#000000";
		   	     var title = cfg["title"] || "";
		   	     var div = document.createElement("div");
				 div.className = "AmapLocation";
				 div.style.fontSize= fontSize;
				 div.style.border="1px solid #999999";
				 div.style.borderRadius = 4;
				 div.title = title;
				 div.style.backgroundColor="#FFFFFF";
				 div.style.position="absolute";
				 div.innerHTML = textLabel ;
		   	     var markerLabel = new ol.Overlay({
					  position: point,
					  stopEvent: false,
					  element: div
				 });
				 this.addOverLayers(markerLabel);
				 var pnode = markerLabel.getElement().parentNode;
		    	 if(pnode){
		    	 	var textCfg= GAS.getTextWidthHeight(textLabel,fontSize);
		    	 	var twidth = textCfg["width"]+2;
		    		div.style.width = twidth;
		    		div.style.top  = offsetTop+ "px";
		    		div.style.color = typeColor;
		    		div.style.left = -(twidth/2) + "px";
		    	 } 
			     return markerLabel;
			},
			//添加点
			addMarker:function(cfg){
			   var point = cfg["lnglat"];
			   var imageDom = document.createElement("img");
			   imageDom.setAttribute("src",cfg["iconUrl"]);
			   imageDom.style.cursor = "pointer";
			   //配置图片的大小
			   if(cfg["iconSize"]){
			   	   var size = cfg["iconSize"];
			       imageDom.style.width  = size[0]+"px";
			       imageDom.style.height = size[1]+"px";
			   }
			   //配置图片的title
			   if(cfg["title"]){
			       imageDom.title  = cfg["title"]
			   }
			   var markPoint = new ol.Overlay({
					  position: point,
					  element: imageDom
			   });
			   //配置锚点
			   if(cfg["iconAnchor"]){
			   	  var anchor = cfg["iconAnchor"];
			   	  markPoint.setOffset([-anchor[0],-anchor[1]]);
			   }
			   
			   this.addOverLayers(markPoint);
			   //配置可以点击
			   if(cfg["clickEnable"]){
			   	     var markerDom = markPoint.getElement();
			   	     var postion = markPoint.getPosition();
		         	 var info = this.getMapInfoWindow();
			   	     markerDom.addEventListener("click",function(e){
						    info.show(postion, {
						        title   : cfg["title"] ||  "信息窗口",
						        width   : cfg["width"] || 150,
						        content : cfg["msg"] || ""
						    });
						    if(cfg["infoTop"]){
						    	var infoTop = cfg["infoTop"];
						    	var pnode = info.getElement().parentNode;
						    	if(pnode){
						    		var top = parseFloat(pnode.style.top);
						    		pnode.style.top = (top-infoTop) + "px";
						    	}
						        
						    }
					 });
			   }
			   if(cfg["isOpened"]){
			   	     var markerDom = markPoint.getElement();
			   	     var postion = markPoint.getPosition();
		         	 var info = this.getMapInfoWindow();
				     info.show(postion, {
				        title   : cfg["title"] ||  "信息窗口",
				        width   : cfg["width"] || 150,
				        content : cfg["msg"] || ""
				    });
			   }
			   return markPoint;
			},
			//更新点的位置信息
			updateMarkPostion:function(marker,newpoint){
			    marker.setPosition(newpoint);
			},
			//更新marker的位置信息【位置和角度】
			updateMarkerIconAndPostion:function(cfg){
					var marker= cfg["marker"],
					iconUrl = cfg["icoUrl"],
					rotate = cfg["rotate"],
					newpoint = cfg["newpoint"];
				    var imageDom = document.createElement("img");
				   	imageDom.setAttribute("src",iconUrl);
				   	imageDom.style.cursor = "pointer";
				    marker.setPosition(newpoint);
	     			marker.setElement(imageDom);
				    this.rotateDom(imageDom,rotate); //旋转图片
			},
			showMapInfo:function(postion,title,content){
			   	   var info = this.getMapInfoWindow();
			       info.show(postion, {
				       title   : title || "信息窗口",
				       width   : 200,
				       content : content || "文本信息"
				   });
				   return info;
			},
			// 更新气泡信息
			setMarkerPop:function(point,content){
			   if(this.popWin){
			   	  this.popWin.setLatLng(point);
			      this.popWin.setContent(content);
			   }
			},
			//居中【替换OK】
			moveToCenter:function(point){
			    this.map.getView().setCenter(point);
			},
			//创建【替换OK】
			createPoint : function(lng,lat){
				var lnglat = [parseFloat(lng),parseFloat(lat)];
				var newpoint = ol.proj.transform(lnglat, 'EPSG:4326', 'EPSG:3857');
				return newpoint;
			},
			//添加覆盖物【替换OK】
			addOverLayers:function(overlayer){
			    this.map.addOverlay(overlayer);
			    return overlayer;
			},
			//添加图层
			addMapLayer:function(layer){
			    this.map.addLayer(layer);
			    return layer;
			},
			//创建折线【替换OK】
			createPolyLine:function(cfg){
				var points = cfg["points"];
				var featureRed = new ol.Feature({
				    geometry: new ol.geom.LineString(points)
				});
				var vectorRedLine = new ol.source.Vector({});
      			vectorRedLine.addFeature(featureRed);
				var polylineLayer = new ol.layer.Vector({
				    source: vectorRedLine,
				    style: new ol.style.Style({
				        stroke: new ol.style.Stroke({
				            color: cfg["strokeColor"] || '#0000E3',
				            width: 2
				        }),
				        fill: new ol.style.Fill({
				            color: cfg["fillColor"] || '#FF0000',
				            weight: 1
				        })
				    })
				});
			    return polylineLayer;
			},
			//创建多边形【替换OK】
			createPolygon:function(cfg){
				var points = [cfg["points"]];
			    var polyFeature = new ol.Feature({
				    geometry: new ol.geom.Polygon(points)
				});
				var vectorLayer = new ol.layer.Vector({
				    source: new ol.source.Vector({
				        features: [polyFeature]
				    })
				});
			    return vectorLayer;
			},
			//添加多边形【替换OK】
			addPolygon:function(cfg,flag){
			   this.polygonOverlayer = this.createPolygon(cfg);
			   this.addMapLayer(this.polygonOverlayer);
			   if(flag && flag == true){
			   	   var gon = this.polygonOverlayer.getSource().getFeatures()[0];
				   var plig = gon.getGeometry();
				   this.map.getView().fitGeometry(plig,this.map.getSize());
			   }
			   return this.polygonOverlayer;
			},
			//添加线【替换OK】
			addPolyLine:function(cfg){
			   var polyline = this.createPolyLine(cfg);
			   this.addMapLayer(polyline);
			   var gon = polyline.getSource().getFeatures()[0];
			   var plig = gon.getGeometry();
			   this.map.getView().fitGeometry(plig,this.map.getSize());
			   return polyline;
			},
			//清除地图所有的地图覆盖物
			clearAllMap:function(){
				
			},
			//移除覆盖物【替换OK】
			removeOverLayer:function(overlay){
				if(overlay){
					try{
                		this.map.removeOverlay(overlay);
                	}catch(e){
                	   console.error("OL3内部异常");
                	}
				}
			},
			//删除图层
			removeLayers :function(layer){
			     if(layer){
					try{
                		this.map.removeLayer(layer);
                	}catch(e){
                	   console.error("OL3内部异常");
                	}
				}
			},
			//【替换OK】
			getMap:function(){
			   return this.map;
			},
			//判断点在bound
			isPointInBounds:function(point){
				var lng = point[0],lat = point[1];
				var size = this.map.getSize();
				var bounts= this.map.getView().calculateExtent(size);
				var fla = ol.extent.containsCoordinate(bounts,point);
				return fla
			},
			getMainLoadMask : function() {
			 	if(!this.loadMask){
			 		  var bodyEl = this.getMapContainer().findParentByType("panel").body;
				      this.loadMask = new Ext.LoadMask(bodyEl, {
							msg : "正在加载地图......."
					  });
			 	}
				return this.loadMask;
			},
			reSizeMap:function(panel,width,height){
			    if(this.map){
			    	var size = [width,height];
			        this.map.setSize(size);
			    }
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
							    this.getMainLoadMask().show();
								this.initMap(); //初始话地图
							},
							resize: this.reSizeMap
						}
					});
				}
				return this.mapcontainer;
			},
			getMapbarTile : function(){
				var title = this.barTitle || "地图工具栏";
			   return "<b style='color:#15428b;'>"+title + "</b>";
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
		                tooltip: "清除多边形",
						iconCls: "icon-clear",
						name:"clear",
						scope: this,
						pressed:false,
						type:"clear",
						handler: this.clearAllpolygon
			       });
			   }
			   return this.clearBtn;
			},
			//清除操作
			clearAllpolygon:function(){
				  this.polygonLayer = [];
			      if(this.polyFeature){
				     this.polyFeature.getFeatures().clear();
				  }
				  //删除多边形
				  if(this.polygonOverlayer){
				     this.map.removeLayer(this.polygonOverlayer);
				  }
			},
			//绘制多边形【绘制多边形之前清除之前的覆盖物】
			drawGonhandler:function(){
				 //清除之前的覆盖物
				 this.clearAllpolygon();
				 this.polyFeature = new ol.FeatureOverlay({
					  style: new ol.style.Style({
					    	fill: new ol.style.Fill({ color: 'rgba(55, 155, 55, 0.3)'  }),
					    	stroke: new ol.style.Stroke({ color: '#B15BFF', width: 2 }),
					    	image: new ol.style.Circle({ radius: 7, fill: new ol.style.Fill({color: '#ffcc33' })
					    })
					  })
				  });
				  this.polyFeature.setMap(this.map);
				  var draw = new ol.interaction.Draw({
				    	features: this.polyFeature.getFeatures(),
				    	type: "Polygon"
				  });
				  draw.on("drawend",function(evt){
				        this.polygonLayer = evt.feature.getGeometry().getCoordinates()[0]; //坐标点集合
				  		this.map.removeInteraction(draw); //移除绘制事件
				  },this);
				  this.map.addInteraction(draw); 
			},
			//获取当前绘制的多边形覆盖物对象
			getDrawPolygonLayers:function(){
			    return this.polygonLayer;
			},
			//屏幕坐标转经纬度坐标
			referCoorToLnglat:function(coord){
			    return  ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
			},
			//获取多边形的经纬度
			getDrawPolugonLatLngList:function(){
			    var list  = this.getDrawPolygonLayers();
			    var newList = [];
			    if(list && list.length > 0){
			    	var len = list.length;
			        for (var i = 0; i < len; i++) {
			        	var lnglat = this.referCoorToLnglat(list[i]);
			        	newList.push(lnglat[0]+","+lnglat[1]);
			        }
			    }
			    return newList;
			},
			//显示多边形["PS"] 首尾坐标相连，设计如此
			showDrawPolugon:function(listArr){
			   var array = listArr?listArr.split("@"):[];
			   this.polygonLayer = [];
			   if(array.length > 0){
			   	  var newPoints = [];
			      for (var i = 0; i < array.length; i++) {
			      	  var lnglatStr= array[i],
			      	  lnglatArr= lnglatStr.split(",");
			      	  var coorlnglat = this.createPoint(lnglatArr[0],lnglatArr[1]); 
			      	  this.polygonLayer.push(coorlnglat);
			      	  //去掉最后一个坐标【去掉】
			      	  if(i!= array.length -1){
			      	     newPoints.push(coorlnglat);
			      	  }
			      }
		          this.addPolygon({ points : newPoints },true); //绘制多边形
			   }
			},
			polylineFitInView:function(polyline){
				if(polyline){
					var gon = polyline.getSource().getFeatures()[0];
   					var plig = gon.getGeometry();
				    this.map.getView().fitGeometry(plig,this.map.getSize());
				}
			},
			setView:function(point,zoom){
			   zoom = zoom ? zoom : this.map.getView().getZoom();
			   this.map.getView().setCenter(point);
			   this.map.getView().setZoom(zoom);
			},
			//放大【ok】
			MapZoomIn: function(){
			   var zoom = this.map.getView().getZoom();
			   zoom ++; 
			   this.map.getView().setZoom(zoom);
			},
			//缩小【ok】
			MapZoomOut: function(){
			   var zoom = this.map.getView().getZoom();
			   zoom --; 
			   this.map.getView().setZoom(zoom);
			},
			//返回初始点【ok】
			resetMapCenter: function(){
			   var lnglat = this.defaultCenter;
			   var point = this.createPoint(lnglat[0],lnglat[1]);
			   var zoom =  this.defaultZoom;
			   this.setView(point,zoom);
			},
			//鼠标平移
			mouseTranslation:function(){
			    var container = this.map.getViewport();
			    container.style.cursor = "url('"+this.openHandelr+"'),pointer";
			},
			//鼠标指针样式
			mousePointer:function(){
			    var container = this.map.getViewport();
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
			//显示图层
			showBaseMapLayer:function(){
			    var  layer = this.getBaseLayer();
			    layer.setVisible(true);
			},
			//隐藏图层
			hideBaseMapLayer:function(){
			    var  layer = this.getBaseLayer();
			    layer.setVisible(false);
			},
			getShowButtomMap:function(){
			     var btn = new Ext.form.Checkbox({
		            boxLabel : "隐藏底图",
		            scope : this,
		            listeners:{
		            	scope :this,
		            	check:function(check,flag){
				      	   if(flag){ //true显示地图
				      	      this.hideBaseMapLayer();
				      	   }else{ //false隐藏地图
				      	   	  this.showBaseMapLayer();
				      	   }
			            }
		            }
		          });
		          return btn;
			},
			getMapToolBar:function(){
				 if(!this.baritems){
				 	  this.baritems = [this.getMapbarTile(),{
							xtype: 'tbfill'
						 },
						 this.showDrawBtn?this.getMapBtn():"", //显示画多边形,
						 this.showClearBtn?this.getClearBtn():"", //显示画多边形
						 !this.hideBaseMapBtn?this.getShowButtomMap():"" //是否显示显示地图的复选框
						 ].concat(this.getCommonMapOper());
				 }
				 return  this.baritems;
			},
			getMainMap:function(){
			   if (!this.mainMap) {
			   	     var hideTools = this.hideTools;
			   	     var cfg = {
			   	        border : false,
						frame  : false,
						layout : "fit",
						items  : this.getMapContainer()
			   	     };
			   	     if(!hideTools){
			   	         cfg["tbar"] = this.getMapToolBar();
			   	     }
					 this.mainMap = new Ext.Panel(cfg);
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





(function(){
	/**
	 * 地图基类扩展
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.MonitorMap = Ext.extend(GAS.BaseMap,{
		  constructor : function(config) {
	      	    config = config || {};
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
	      	    this.listMarkerPoints = [] ; //所有的点覆盖物的集合
	      	    this.listMarkerTip = {};
	      	    this.ListMarkerLabel={};
	      	    this.GPSLabelText = {};
	      	    this.targetRuleName = null;
				this.targetMaxSpeed = null;
	      	    this.terminalInfoUrl =  "./montior/data.json?_dc="+new Date().getTime();//查询终端详情    
				GAS.MonitorMap.superclass.constructor.apply(this, arguments);
				this.allInView  =false ;  //所有的覆盖物显示在当前视野 
				this.showBusLabelText = true;
		   },
		   //清除所有的车辆覆盖物
		   clearAllMap:function(){
		   	  this.hideMapInfoWindow();  //隐藏气泡
		      var list = this.listMarkerPoints;
		      if(list && list.length>0){
		          for (var i = 0; i < list.length; i++) {
		          	  var marker = list[i];
		          	  this.removeOverLayer(marker); //调用地图删除覆盖物的方法
		          }
		          this.listMarkerPoints = [];  //清空所有的对象
			  	  this.listMarkerTip = {};
		      }
		      this.clearAllMarkerLabel();
		   },
		   //删除车牌号
		   removeAllMarkerLabel:function(){
		          for (var la in this.ListMarkerLabel) {
		          	  var markerlabel = this.ListMarkerLabel[la];
		          	  this.removeOverLayer(markerlabel); //调用地图删除覆盖物的方法
		          }
		          this.ListMarkerLabel = {};
		   },
		   //删除车牌号
		   removeGPSMarkerLabel:function(targetId){
		   	      var marker = this.ListMarkerLabel[targetId];
		          if(marker){
		          	  this.removeOverLayer(marker); //调用地图删除覆盖物的方法
		          }
		   },
		   //清除所有的文本
		   clearAllMarkerLabel:function(){
		      this.removeAllMarkerLabel();
		      this.GPSLabelText = {};
		   },
		   getMapbarTile : function(){
			   return  "车辆监控地图工具栏";
		   },
		   //根据目标ID居中显示
		   moveCarToCenter:function(marker){
		   	    var cpoint = marker.getPosition();
		        this.setView(cpoint);
		   },
		   getGPSLabelTextList:function(){
		       var list =[];
		       for (var label in this.GPSLabelText) {
	          	  var labelrecord = this.GPSLabelText[label];
	          	  list.push(labelrecord);
	          }
	          return list;
		   },
		    //根据目标ID居中显示
		   moveCarToCenterAndTip:function(marker,title,content,targetId){
		   	    var postion = marker.getPosition();
		        this.setView(postion);
		        this.onMarkerClickHandler(targetId,postion,title,content); //
		        var info = this.getMapInfoWindow();
		   	    info.setTargetId(targetId);
		   },
		   //开始定位信息
		   startGPSLocation : function(targetID,cfg){
	           var point = this.createPoint(cfg["lng"],cfg["lat"]);
	   	       var marker = this.addMarker({
	            	 lnglat   : point,
	            	 iconSize : [17,17],
	            	 iconAnchor:[9,9],
	            	 title    : cfg["vehicleNum"],
	            	 iconUrl  : cfg["iconUrl"],
	            	 contentMsg  : cfg["msg"] 
            	});
            	var labelcfg = {
			     	point:point,
			     	targetId :targetID,
			     	fontColor: cfg["typeColor"],
			     	title    : cfg["vehicleNum"],
			     	labelText: cfg["vehicleNum"],
			     	offsetTop: 10
			 　　　}
			    this.GPSLabelText[targetID] = labelcfg;
            	//显示车牌号
			    if(this.showBusLabelText){
				 　　　var markerLabel = this.addMarkerLabel(labelcfg);
				 　　　this.ListMarkerLabel[targetID] = markerLabel;
			    }			 
			 　　　
			 
	           　		this.listMarkerPoints.push(marker); //多个经纬度
	           　		this.openMarkerInfoWindow(marker,"",cfg["msg"],targetID);//开始点注册气泡【不然点击的时候没有事件】
	           　　　　　　return marker;
		   },
		   //更新定位信息
		   updateGPSLocation:function(marker,cfg){
		   	    var oldCoornate = marker.getPosition() ||[0,0];
	   	        var targetId = cfg["deviceId"],
	   	        curpoint = this.createPoint(cfg["lng"],cfg["lat"]);
	   	        var imageUrl = cfg["iconUrl"];
	   	        var route = GAS.angRoute({x:oldCoornate[0],y:oldCoornate[1]},{x:curpoint[0],y:curpoint[1]});
	   	        var newRoute = 0,mapRotat = this.getMapRotation(); //地图的旋转角度
	            if(mapRotat == 0){ //不旋转
	            	 newRoute = Math.round(route + mapRotat * Math.PI/180);
	            }else{//旋转
	            	 newRoute = 90 + Math.round(route + mapRotat * Math.PI/180);
	            }
        	    this.updateMarkerIconAndPostion({
        	            marker: marker, //当前更新的覆盖物点
					    icoUrl: imageUrl , //图标
					    rotate: newRoute, //旋转角度
					    newpoint:curpoint //更新的位置
        	    }); //update 车辆信息和位置
        	    
        	    var labelcfg = {
			     	point:curpoint,
			     	fontColor: cfg["typeColor"],
			     	targetId :targetId,
			     	title    : cfg["vehicleNum"],
			     	labelText: cfg["vehicleNum"],
			     	offsetTop: 10
			 　　　}
			    this.GPSLabelText[targetId] = labelcfg;
        	    
        	    //如果默认配置显示车牌号则执行
        	    if(this.showBusLabelText){
    	            //更新label的位置
	        	    var markerLabel = this.ListMarkerLabel[targetId];
	        	    if(markerLabel){
	        	        markerLabel.setPosition(curpoint); //更新marker的位置信息
	        	    }
        	    }
        	    
        	    
        	    //ps:更新的时候也要更新信息
				var content = cfg["msg"];
        	    if(this.targetRuleName!=null && this.targetMaxSpeed!=null){
				    content = content.replace("{maxSpeed}", this.targetMaxSpeed).replace("{ruleName}",this.targetRuleName);
        	    }
        	    this.openMarkerInfoWindow(marker,"",content,targetId);
		   },
		   //marker打开信息对话气泡信息
		   openMarkerInfoWindow:function(markPoint,title,content,targetId){
				 if(!markPoint){
				 	 GAS.error("markPoint 不能为空");
				     return ;
				 }
		         var markerDom = markPoint.getElement(),me = this;
		         var postion = markPoint.getPosition();
		         var info = this.getMapInfoWindow();
		         //判断某个终端点击了弹出气泡 更新位置【否则出现多个覆盖物，气泡错乱更新位置bug】
		         if(info.getTargetId() == targetId){
		            info.updatePostion(postion); //更新marker的位置信息
		            info.setContent(content); //更新文本内容
		         }
		   	     markerDom.addEventListener("click",function(e){
		   	     	 info.setTargetId(targetId);
		   	     	 me.onMarkerClickHandler(targetId,postion,title,content);
				 },this);
			},
			getAjaxLoadMask : function() {
			 	if(!this.ajaxLoadMask){
			 		  var bodyEl = this.container;
				      this.ajaxLoadMask = new Ext.LoadMask(bodyEl, {
							msg : "正在加载车辆基本信息......."
					  });
			 	}
				return this.ajaxLoadMask;
			},
			//点击的时候请求车辆的信息
			onMarkerClickHandler:function(targetId,postion,title,content){
				var me = this;
				var callback= function(res){
					//me.getAjaxLoadMask().hide();
					var result = Ext.decode(res.responseText);
					if(result["result"]){
						 result = result["result"];
						 var ruleName = result["ruleName"] || "暂无数据",maxSpeed = result["speedset"] || "暂无数据";
						 this.targetRuleName = ruleName;
						 this.targetMaxSpeed = maxSpeed;
						 content = content.replace("{maxSpeed}", this.targetMaxSpeed).replace("{ruleName}",this.targetRuleName);
					     this.showMapInfo(postion,title,content);
					}
				}
				//this.getAjaxLoadMask().show();
				Ext.Ajax.request({
				   url :  this.terminalInfoUrl,
				   method : "post",
				   params : {
				   	 deviceId : targetId
				   },
				   scope : this,
				   success : callback,
				   failure : callback
				});
			    
			},
		    //多个marke居中
		   onMutilMarkerInView:function(){
		      var list = this.listMarkerPoints;
		      //只有一个点的情况
		      if(list.length == 1){
		          var markerlnglat = list[0].getLatLng();
		          this.setView(markerlnglat);
		      }else{
		      	  var listPoints = [];
		      	  for (var i = 0; i < list.length; i++) {
		      	  	 var markerlnglat = list[i].getLatLng();
		      	  	 listPoints.push(markerlnglat);
		      	  }
		      	  var polyline = this.createPolyLine({points:listPoints});
		      	  //配置多个覆盖物是否显示当前视野范围内
		      	  if(this.allInView){
		      	      this.polylineFitInView(polyline);
		      	  }
		      }
		   },
		   getCheckOnMap:function(){
		      if(!this.checkBox){
		          this.checkBox = new Ext.form.Checkbox({
		            boxLabel : "固定当前视野",
		            tooltip: "固定当前视野",
		            listeners:{
		            	scope :this,
		            	check:function(check,flag){
			               this.allInView = flag;
			            }
		            }
		          });
		      }
		      return this.checkBox;
		   },
		   //显示车牌号
		   showBusLabelTextHander:function(fa){
		   	  this.showBusLabelText = fa;
		   	  var list = this.getGPSLabelTextList();
		      if(fa){
		      	  this.removeAllMarkerLabel();
		      	  
		          //全部显示车牌号
		      	  for (var  i= 0;  i< list.length; i++) {
		      	  	   var labelcfg = list[i];
		      	  	   var targetId =  labelcfg["targetId"];
	      	  	       var markerLabel = this.addMarkerLabel(labelcfg);
			 　　　	   this.ListMarkerLabel[targetId] = markerLabel;
		      	  }
		      }else{
		      	 //取消显示车牌号
		         this.removeAllMarkerLabel();
		      }
		   },
		   getIsInView:function(){
		       return this.allInView;
		   },
		    //测距
		   ruleTools :function(){
		   	    var  map= this.getMap();
		      	GAS.RuleTool(map);
		   },
		   //取消测距
		   unruleTools :function(){
		      	GAS.unRuleTool();
		   },
		   //覆盖工具栏内容
		   getMapToolBar:function(){
				 if(!this.baritems){
				 	  this.baritems = ["<b style='color:#15428b;'>"+this.getMapbarTile()+"</b>",{
							xtype: 'tbfill'
						 },
						 
						 this.showDrawBtn?this.getMapBtn():"", //显示画多边形,
						 this.showClearBtn?this.getClearBtn():"", //显示画多边形
						{
							tooltip: "测距",
							iconCls: "icon-ruler",
							scope: this,
						    pressed:false,    
							type:"20",
							handler: this.ruleTools
						},
						{xtype: "tbseparator"},
						{
							tooltip: "取消测距",
							iconCls: "icon-unruler",
							scope: this,
						    pressed:false,    
							type:"20",
							handler: this.unruleTools
						}
						].concat(this.getCommonMapOper());
				 }
				 return  this.baritems;
			},
		   //覆盖主面板
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



