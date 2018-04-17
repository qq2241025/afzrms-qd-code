(function(){
	/**
	 * 轨迹回放
	 */
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.TrackQueryMap = Ext.extend(GAS.BaseMap,{
		   constructor : function(config) {
	      	    config = config || {};
	      	    this.startIcon = GAS.iconPath + "qidian.png"; //起点图标
	      	    this.endIcon = GAS.iconPath + "zhongdian.png";//终点图标
	      	    this.trackIcon = GAS.iconPath + "trackPoint.png";//终点图标
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
	      	    this.setIntervalTime = 500; //时钟0.5S
            	this.setIntervalCount= 50; //一次显示多少的Marker
	      	    this.pause = false; // 暂停播放
				this.currIndex = 0; // 当前索引的对象
				this.resetIndex  = false; //重置当前播放的索引
	      	    this.addEvents("trackQuery");
	      	    this.maxLimit = 60000;
	      	    //轨迹列表Url
	      	    
	      	    this.playMode = "1";
	      	    this.limitdis = 100;
	      	    
	      	    this.trackDataUrl = "track.json?_dc="+new Date().getTime(); 
//				this.trackDataUrl = GAS.config["globalProxy"]+"/locrecord/list?_dc="+new Date().getTime(); 
				GAS.TrackQueryMap.superclass.constructor.apply(this, arguments);
				this.initDoHandler();
		   },
		   //初始化执行
		   initDoHandler:function(){
		   	   this.on({
		   	      scope : this,
		   	      maprender : function(){
		   	          	var query = this.getQueryTrackPanel();
					    this.getMainMap().add(query);
					    this.getMainMap().doLayout();
		   	      	
		   	      }
		   	   });
		   },
		   //覆盖显示工具栏
		   getMapbarTile : function(){
			   return  "轨迹回放工具栏";
		   },
		   //请求参数
		   getQueryParams : function(){
		      var params   ={};
		      var  startTime = this.getStartTime.getValue().format("Y-m-d H:i:s");
		   	  var  endTime = this.getendTime.getValue().format("Y-m-d H:i:s");
		      params["beginTime"] = startTime; 
		      params["endTime"] = endTime; 
		      params["limit"] = this.maxLimit; 
		      params["start"] = 0 ; 
		      return params;
		   },
		   //显示查询进度条
		   showLoadMask : function() {
				 this.Trackmask = Ext.Msg.show({
	             	title: '请稍等',   
					msg: '正在查询轨迹数据，请稍后.....',
					progressText: '查询中...',
					width:300,
					wait:true
				});
		   },
		   //隐藏查询进度条
		   hideLoadMask : function() {
			   Ext.Msg.hide();
		   },
		   //获取终端的对象
		   getDeviceObj:function(){
		       return this.devideObj;
		   },
		   //ajax轨迹查询  暴露调用函数 ["终端Id",终端的基本信息]
		   doTrackQueryWithAjax:function(deviceId,deviceObj){
		   	  var  params = this.getQueryParams();
		   	  this.devideObj = deviceObj;
		   	  params["deviceId"]  = deviceId;
		   	  //回调函数
		   	  var callback = function(res,req){
		   	  	     this.hideLoadMask(); //隐藏查询进度条
		    	     var result = Ext.decode(res.responseText);
				 	 if(result && result["data"] && result["data"].length == 0){
				 		Ext.Msg.alert("信息提示", "没有查询到轨迹数据数据");
				     }else{
				     	var data = result["data"] || [];
				        this.reSetAllPlaySetting();
				        //新的展示方式
				        if(this.playMode == "1"){
				             this.showTrackDataOnPartMap(data);
				        }else{
				             this.showTrackDataOnMap(data);
				        }
				     }
			    };
			    this.showLoadMask(); //显示查询进度条
			    GAS.AOP.send({
			       url   : this.trackDataUrl,
			       params: params,
			       timeout : 2 * 60 * 1000,
			       scope : this,
			       callbackFn:callback
			   });
		   },
		   //轨迹播放
		   playTrack:function(){
		   	    var dataArray = this.getTrackData(), playindex = 0; // 轨迹数据
		   	　　this.setPlayStatic(true, false); // 设置暂停开始按钮状态
				if (this.currIndex == 0) {
					this.pause = false;
					this.stop = false;
				} else {
					playindex = this.currIndex;
				}
				//重置播放的索引
				if(this.resetIndex){
				   this.currIndex  = 0;
				}
		   　　　　GAS.asyncEach(dataArray, function(data, dataindex) {
					this.setPalyIndex(dataindex);
					if (this.pause) { // 播放停止之后
						this.pause = false;
						this.setStartPlayStatic("继续播放");
						this.setPlayStatic(false, true); // 设置播放按钮的显示样式
						if (this.stop) { //
							this.currIndex = 0;
							this.stop = false;
						}
						return false;
					}
					this.proccessTrackData(dataArray, data, dataindex);
				}, 1, this, this.commboSpeed.getValue(), this.getPalyIndex());
		   },
		   clearPlayPoint:function(){
		        if (this.pointTrackIcon) {
					this.removeOverLayer(this.pointTrackIcon); // 移除覆盖物
				}
		   },
			//轨迹数据中间处理逻辑
			proccessTrackData:function(list, data, index){
				this.sliderBar.setValue(index); // 设置轨迹进度条
			　　var playType = this.playType.getValue(), // 播放模式
				currentPoint = data["point"]; // 取当前经纬度
				var dirIcon = GAS.config["gpsimagePath"]+ "normal_gps.png";; //方向图标
				if (index == 0) {
					var cfg ={
					    lnglat   : currentPoint,
	            	 	iconSize : [17,17],
	            	 	iconAnchor:[9,9],
	            	 	zIndex   : 33333,
	            	 	iconUrl  : dirIcon
					};
					if (this.pointTrackIcon) {
						this.removeOverLayer(this.pointTrackIcon); // 移除覆盖物
					}
					this.pointTrackIcon = this.addMarker(cfg); // 添加线覆盖物对象；
				} else if(index == list.length - 1 ){ //最后一条轨迹 重置播放设置
				    this.TrackPlayEndStaic(); // 恢复播放前状态
				}else{
					var upPoint = list[index-1]["point"] || [0,0];
					var route = GAS.angRoute({x:upPoint[0],y:upPoint[1]},{x:currentPoint[0],y:currentPoint[1]}); //角度
					var newRoute = 0,mapRotat = this.getMapRotation(); //地图的旋转角度
		            if(mapRotat == 0){ //不旋转
		            	 newRoute = Math.round(route + mapRotat * Math.PI/180);
		            }else{//旋转
		            	 newRoute = 90 + Math.round(route + mapRotat * Math.PI/180);
		            }
		            
					this.updateMarkerIconAndPostion({
					    marker: this.pointTrackIcon, //当前更新的覆盖物点
					    icoUrl: dirIcon , //图标
					    rotate: newRoute, //旋转角度
					    newpoint:currentPoint //更新的位置
					}); //更新点的位置信息
				}
				if (playType == "1") { // 追踪模式
					var flg = this.isPointInBounds(currentPoint); // 判断当前的地图点是否在地图当前视图范围内
					if (!flg) { // 如果不在范围内则判断居中
						this.moveToCenter(currentPoint);
					}
				} else if (playType == "2") {
					this.moveToCenter(currentPoint);
				}
			},
		   　  getPalyIndex : function() {
				return this.currIndex;
		　　},
		　　setPalyIndex : function(index) {
				this.currIndex = index;
		　　},
			// 暂停播放的代码
			playstop : function() { 
				this.resetIndex = false;
				this.pause = true;
			},
			// 设置开始播放按钮状态，文字提示信息
			setStartPlayStatic : function(title) {
				this.startButton.setTooltip(title);
			},
			// 设置开始、暂停按钮禁用启用
			setPlayStatic : function(start, parse) {
				this.startButton.setDisabled(start); // 设置开始按钮禁用启用
				this.parseButton.setDisabled(parse); // 设置暂停按钮禁用启用
			},
			//恢复开始播放状态
			recoveryStartStatic:function(){
				this.setStartPlayStatic("重新播放");
				this.setPlayStatic(false, true); // 设置播放按钮的显示样式
				this.sliderBar.setValue(0); // 设置播放按钮的显示样式
				this.resetIndex= true; //重置索引
			},
			//重置所有的播放设置
			reSetAllPlaySetting:function(){
			    this.setStartPlayStatic("重新播放");
				this.setPlayStatic(false, true); // 设置播放按钮的显示样式
				this.sliderBar.setValue(0); // 设置播放按钮的显示样式
				this.currIndex = 0;
				this.playstop(); //停止播放
			},
			// 轨迹回放完毕
			TrackPlayEndStaic : function() {
				this.reSetAllPlaySetting();//重置所有的播放设置
			},
		    playTrackBar:function(){
	           this.startButton = new Ext.Button({
					tooltip : '开始播放',
					iconCls : "track_begin",
					scope : this,
					type : "start",
					handler : this.playTrack
				});
				this.parseButton = new Ext.Button({
					tooltip : "暂停播放",
					iconCls : "track_pause",
					scope : this,
					disabled : true,
					type : "stop",
					handler : this.playstop
				});
				this.playType = new Ext.form.ComboBox({
					width : 70,
					store : new Ext.data.JsonStore({
						fields : ['id', 'name'],
						data : [{id : 1,name : "锁定模式"}, // 点在地图bound范围内
								{id : 2,name : "追踪模式"}, // 每个点必须居中
								{id : 3,name : "自由模式"}]
					}),
					valueField : 'id',
					displayField : 'name',
					typeAhead : true,
					mode : 'local',
					selectOnFocus : true,
					forceSelection : true,
					triggerAction : 'all',
					editable : false,
					emptyText : '请选择',
					value : "2"
				});
				this.commboSpeed = new Ext.form.ComboBox({
					width : 60,
					store : new Ext.data.ArrayStore({
						fields : ['id', 'name'],
						data : [["800", "2X"], ["400", "4X"], ["200", "8X"]]
					}),
					valueField : 'id',
					displayField : 'name',
					typeAhead : true,
					editable : false,
					mode : 'local',
					forceSelection : true,
					triggerAction : 'all',
					emptyText : '请选择',
					selectOnFocus : true,
					value : "400"
				});
				this.showPointsCheckbox = new Ext.form.Checkbox({
		            boxLabel : "显示轨迹点",
		            scope : this,
		            listeners:{
		            	scope :this,
		            	check:function(check,flag){
				      	   var showPoints = flag;
				      	   if(flag){
				      		 	this.showMidTrackMarker();
				      	   }else{
				      		 	this.clearMidTrackMarker();//清除轨迹点
				      	   }
			            }
		            }
		          });
				this.bottombar = new Ext.Toolbar({
					disabled : true,
					items : [this.startButton, "-", this.parseButton, "-","播放模式", this.playType, "播放速度",this.commboSpeed, "		", this.showPointsCheckbox]
				});
				return this.bottombar;
		   },
		   //暴露出去的函数
		    setTrackData:function(data){
		      this.trackdata = data;
		    },
		    getPossBackData:function(){
		       return this.trackdata;
		    },
		    getTrackData:function(){
		       return this.TrackPlayData;
		    },
		    clearAllMap:function(){
		    	//清除线
		    	this.hideMapInfoWindow();  //隐藏气泡
		    	this.clearPlayPoint(); //清除播放中的播放点
				if(this.TrackLine){
				    this.removeLayers(this.TrackLine);
				}
				//清除起始点
				var listSE = this.StartAndEndPoint;
				if( listSE &&listSE.length > 0){
					 for (var i = 0; i < listSE.length; i++) {
					 	  var overlayer = listSE[i];
					 	  this.removeOverLayer(overlayer);
					 }
				}
				//清除轨迹点
				this.clearTrackData();
			},
		   //显示轨迹数据
		   showTrackDataOnMap:function(trackData){
		        //工具栏启用
				this.TrackPlayData = []; // 轨迹数据点
		        var tdistance = 0; // 设置距离
		        if(trackData && trackData.length>0){
		        	var total = trackData.length - 1;
		        	this.getProccessingBar().setDisabled(false);
		            this.bottombar.setDisabled(false);
		            
		            this.sliderBar.setMaxValue(total - 1); //设置总进度条
		        	
		        	var listTrackdata = [],deviceObj = this.getDeviceObj(); //终端的基本信息
		        	var firstPointDistance = null;
		            for (var i = 0; i < trackData.length; i++) {
		            	  var data = trackData[i];
		            	  var lng = data["x"],lat = data["y"];
		            	  var termName  =  deviceObj["text"];//车牌号
		            	  var simcard   =  deviceObj["simcard"];
		            	  var deptName   =  deviceObj["deptName"] || "";
		            	  var vehicleType   =  deviceObj["vehicleTypeName"] || "";
		            	  var vehicleBrand   =  deviceObj["vehicleBrandName"] || "";
		            	  var xdistance = Number(data["distance"] || "0.0");
		            	  if(i==0){
		            		  firstPointDistance = xdistance;
		            	  }
		            	  var point = this.createPoint(parseFloat(lng),parseFloat(lat));
		            	  var contentText = [
							'<b>车牌号码: </b> ' + termName,
//							'<br><b>手机号码: </b> ' + simcard,
							'<br><b>所属部门: </b> ' + deptName,
							'<br><b>车辆类型: </b> ' + vehicleType,
							'<br><b>车辆品牌: </b> ' + vehicleBrand,
							'<br><b>行驶车速: </b> ' + data["speed"]+"km/h",
							'<br><b>行驶时间: </b> ' + data["gpsTime"]];
		            	    Ext.apply(data, {
								point : point,
								Target : termName,
								contentMsg : contentText.join("")
						   });
						   this.TrackPlayData.push(data);
		            	   listTrackdata.push(point);
		            }
		            var trackLen = listTrackdata.length;
		            if(trackLen>0){
		            	  this.clearAllMap();//清除地图覆盖物
		            	  //添加轨迹路线
		                  this.TrackLine= this.addPolyLine({
			                  	points:listTrackdata
		                  });
		            }
		             this.showTrackPointIcon(); //显示轨迹数据点
		        }else{
		            console.log("没有轨迹数据");
		        }
		   },
		   //显示轨迹数据点
		   showTrackPointIcon :function (){
		   	  this.StartAndEndPoint = [];
		      var trackData   = this.TrackPlayData,
			      startRecord = trackData[0],
			      endRecord   = trackData[trackData.length - 1];
		      //开始点
		      var firstCfg = {
		             lnglat      : startRecord["point"],
	            	 iconSize    : [31,35],
	            	 iconAnchor  : [15, 34],
	            	 iconUrl     : this.startIcon,
	            	 clickEnable : true,
	            	 width       : 230,
	            	 msg  : startRecord["contentMsg"] 
		      };
		      //结束点
		      var endCfg = {
		             lnglat      : endRecord["point"],
	            	 iconSize    : [31,35],
	            	 iconAnchor  : [15, 34],
	            	 iconUrl     : this.endIcon,
	            	 clickEnable : true,
	            	 width       : 230,
	            	 msg  : endRecord["contentMsg"] 
		      };
		      var start = this.showTrackPoint(firstCfg); //显示开始轨迹点
		      var end = this.showTrackPoint(endCfg);  //显示结束轨迹点
		      
		      this.StartAndEndPoint.push(start);
		      this.StartAndEndPoint.push(end);
		      // 先不描点
//		      this.showMidTrackMarker(); //显示终端的数据轨迹点
		   },
		   // 显示轨迹点
		   showTrackPoint : function(cfg) {
		   	     var marker = this.addMarker(cfg)
	   	         return marker;
		   },
		   //显示
		   showMidTrackMarker:function(){
			    this.TrackPlayData_marker = []; // 轨迹数据点marker
			    var count = this.setIntervalCount,
				    time  = this.setIntervalTime,
				    list  = this.TrackPlayData, //轨迹数据
				    len   = list.length;
				var arrayData = [],pagestart = 0,dataIndex=0; 
			    this.myTask = this.doTimerTask(function(aa){
			    	if(dataIndex == len - 1){
			    	    this.clearTimerTask(); //清空任务
			    	    return ;
			    	}
			    	var startNum = pagestart * count;
			    	var endNum   = (pagestart + 1) * count;
			    	if(endNum >len){
			    	    endNum = len;
			    	}
				    for (var index = startNum; index < endNum; index++) {
			    		var record = list[index];
			    		if(dataIndex == 0 || dataIndex == len -1){
				      	}else{
				      	     var midCfg = {
						             lnglat      : record["point"],
					            	 iconSize    : [8,8],
					            	 width       : 230,
					            	 msg         : record["contentMsg"], 
					            	 iconUrl     : this.trackIcon,
					            	 clickEnable : true,
					            	 iconAnchor  : [2, 4]
					            	 
						      };
				      	      this.TrackPlayData_marker.push(this.showTrackPoint(midCfg));
				      	}
				      	dataIndex ++;
			    	}
			    	pagestart ++;
			    },this,time);
		   },
		   //清除marker 点
		   clearTrackData:function(){
		   	   var list = this.TrackPlayData_marker || [];
		   	   if(list && list.length>0){
		   	   	   console.log("清除marker 点",list.length);
		   	       for (var index = 0; index < list.length; index++) {
					   var record = list[index];
					   this.removeOverLayer(record);
				   }
		   	   }
		   },
		   //清除
		   clearMidTrackMarker:function(){
			   this.clearTimerTask(); //清空任务
			   this.clearTrackData();
		   },
		   doTimerTask:function(callback,scope,time){
			    var timeTask = setInterval(function(){
			        callback.call(scope || this);
			    },time);
			    return timeTask;
		   },
		   clearTimerTask:function(){
				var task = this.myTask;
				if(task){
				     clearInterval(task);
				}
		   },
		   //轨迹回放工具栏
		   getProccessingBar: function() {
				if (!this.Proccessingbar) {
					 this.sliderBar = new Ext.Slider({
					 	layout  :"fit",
					 	minValue: 0,
	        			maxValue: 100,
					 	margins : "1px"
					});
					this.Proccessingbar = new Ext.Toolbar({
					 	layout:"fit",
					 	disabled : true,
						items: this.sliderBar
					});
				}
				return this.Proccessingbar;
		   },
		   getMainMap:function(){
			   if (!this.mainMap) {
					 this.mainMap = new Ext.Panel({
						border : false,
						frame  : false,
						layout : "fit",
						items  : this.getMapContainer()
					});
				}
				return this.mainMap;
			},
		   //覆盖主面板
		   getMainPanel: function() {
				if (!this.mainpanel) {
					 this.mainpanel = new Ext.Panel({
						border : false,
						frame  : false,
						layout : "fit",
						tbar   : this.getMapToolBar(),
						bbar   : this.getProccessingBar(),
						items  : this.getMainMap(),
			            listeners:{
			               scope:this,
			               render: function(panel){ 
			               	  this.playTrackBar().render(panel.bbar); 
						   }
			            }
					});
				}
			    return this.mainpanel;
		   },
		   //查询面板
		   getSearchQueryPanel:function(){
		    	this.getStartTime = new Ext.form.DateTimeField({
		    		    width : 145,
		    		    height : 23,
						format : "Y-m-d H:i:s",
						allowBlank : false,
						fieldLabel:"开始时间",
						blankText : "请输入开始时间",
						value : new Date().clearTime(),
						listeners : {
							scope : this,
							blur : function(sttime) {
								var mivalue = sttime.getValue();
								this.getendTime.setMinValue(mivalue);
								this.getendTime.focus();
							},
							render:function(comb){
								if(comb.getEl() ){
									comb.getEl().dom.style.color = "black";
								}
							}
						}
					});
					this.getendTime = new Ext.form.DateTimeField({
						width  : 145,
						height : 23,
						format : "Y-m-d H:i:s",
						fieldLabel:"结束时间",
						allowBlank : false,
						value : new Date(),
						blankText : "请输入结束时间",
						listeners : {
							scope : this,
							render:function(comb){
								if(comb.getEl() ){
									comb.getEl().dom.style.color = "black";
								}
							}
						}
					});
				        
				//form表单	
			    var form =  new Ext.FormPanel({
			        frame : false,
			        border: false,
		            labelAlign : 'right',
		            labelWidth : 60,
		            autoHeight : true,
		            headerStyle:{border:"0px"},
			        items: [{
			            layout: 'form',
			            frame : false,
	                	border: false,
	                	style : "margin-top:2px;",
			            items : [this.getStartTime,this.getendTime]
			        }]
			    });
				var sarchTrack = function(){
					this.fireEvent("trackQuery");
				};
			    var newPanel = new Ext.Panel({
				     frame : false,
				     items : form,
				     layout: "fit",
				     border: false,
				     autoHeight:true,
				     bbar : new Ext.Toolbar({
				     	buttonAlign : "center",
						items : [{
				          xtype : "button",
				          iconCls : "icon-find",
				          text  : "轨迹查询",
				          scope : this,
				          handler : sarchTrack
				        }]
					 })
				 });
			    return newPanel;
			},
		    //获取轨迹查询的面板对象
		    getQueryTrackPanel:function(){
		       var trackPanel = new Ext.Panel({
					borde : true,
					frame : false,
					width : 230,
					shadow : false,
					floating : true,
					x : 88,
					y : 34,
					iconCls :"icon-controller",
	                title  : "历史轨迹查询",
					layout: "fit",
					items : this.getSearchQueryPanel(),
					draggable: {
	                     onDrag : function(e){
	                     	 var parentPanel = this.panel.findParentByType("panel");
	                     	 var parentPostion =parentPanel.getPosition();
	                     	 var pel = this.proxy.getEl();
	                     	 //当前父对象的宽度
	                     	 var parentWidth  = parentPanel.getWidth();
	                     	 var parentHeight = parentPanel.getHeight();
	                     	 //拖动对象的宽度
	                     	 var dragWidth    = pel.getWidth(); 
	                     	 var dragHeight   = pel.getHeight(); 
	                     	 //计算相对的
	                         var maxX = parentWidth  - dragWidth;
	                         var maxY = parentHeight - dragHeight;
	                         this.x = pel.getLeft(true) - parentPostion[0] ;
	                         this.y = pel.getTop(true) -  parentPostion[1];
	                         if(this.x < 0){this.x = 1;}
						     if(this.x > maxX){ this.x = maxX; }
						     if(this.y < 0){ this.y = 1; }
						     if(this.y > maxY){ this.y = maxY;}
	                     },
	                     endDrag : function(e){
	                         this.panel.setPosition(this.x, this.y);
	                     }
	                 },
					 listeners : {
						scope : this,
						render : function(panel) {
							var container = panel.container;
							if (container) {
								container.setStyle("zIndex", 6000);
							}
						}
					}
			      });
		      return trackPanel;
		   },
		   /*****************************************************************************************************************/
		   getGPSDisance : function(start,end) { 
				var lat1 = start["lat"], lng1 = start["lng"], lat2= end["lat"], lng2= end["lng"];
				var toRad = function (d) {
					return d * Math.PI / 180;
				}
			    var dis = 0;
			    var radLat1 = toRad(lat1);
			    var radLat2 = toRad(lat2);
			    var deltaLat = radLat1 - radLat2;
			    var deltaLng = toRad(lng1) - toRad(lng2);
			    var dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLng / 2), 2)));
			    return dis * 6378137;
		   } ,
		   //显示轨迹数据
		   showTrackDataOnPartMap:function(trackData){
		        //工具栏启用
				this.TrackPlayData = []; // 轨迹数据点
		        var tdistance = 0; // 设置距离
		        if(trackData && trackData.length>0){
		        	var total = trackData.length - 1;
		        	this.getProccessingBar().setDisabled(false);
		            this.bottombar.setDisabled(false);
		            
		            this.sliderBar.setMaxValue(total - 1); //设置总进度条
		        	
		        	var listTrackdata = [],deviceObj = this.getDeviceObj(); //终端的基本信息
		        	var partData = [];
		        	var firstPointDistance = null;
		            for (var i = 0; i < trackData.length; i++) {
		            	  var data = trackData[i];
		            	  var lng = data["x"],lat = data["y"];
		            	  var termName  =  deviceObj["text"];//车牌号
		            	  var simcard   =  deviceObj["simcard"];
		            	  var deptName   =  deviceObj["deptName"] || "";
		            	  var vehicleType   =  deviceObj["vehicleTypeName"] || "";
		            	  var vehicleBrand   =  deviceObj["vehicleBrandName"] || "";
		            	  var xdistance = Number(data["distance"] || "0.0");
		            	  if(i==0){
		            		  firstPointDistance = xdistance;
		            	  }
		            	  var point = this.createPoint(parseFloat(lng),parseFloat(lat));
		            	  var contentText = [
							'<b>车牌号码: </b> ' + termName,
//							'<br><b>手机号码: </b> ' + simcard,
							'<br><b>所属部门: </b> ' + deptName,
							'<br><b>车辆类型: </b> ' + vehicleType,
							'<br><b>车辆品牌: </b> ' + vehicleBrand,
							'<br><b>行驶车速: </b> ' + data["speed"]+"km/h",
							'<br><b>行驶时间: </b> ' + data["gpsTime"]];
		            	    Ext.apply(data, {
								point : point,
								Target : termName,
								contentMsg : contentText.join("")
						   });
						   this.TrackPlayData.push(data);
						   
						   if(i >0 ){
      	      				   var preData = data[i-1];
					           var tdis = this.getGPSDisance({
				      	           lat : data["y"],
				      	           lng : data["x"]
				      	       },{
				      	           lat : preData["y"],
				      	           lng : preData["x"]
				      	       });
				      	       if(tdis > this.limitdis){
				      	       
				      	       
				      	       }
						   }
						   
						   
						   
		            	   listTrackdata.push(point);
		            }
		            var trackLen = listTrackdata.length;
		            if(trackLen>0){
		            	  this.clearAllMap();//清除地图覆盖物
		            	  //添加轨迹路线
		                  this.TrackLine= this.addPolyLine({
			                  	points:listTrackdata
		                  });
		            }
		             this.showTrackPointIcon(); //显示轨迹数据点
		        }else{
		            console.log("没有轨迹数据");
		        }
		   }
		   
	});
	
})();



