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
	      	    this.terminalInfoUrl = GAS.config["globalProxy"] + "/terminal/terminalInfo?_dc="+new Date().getTime();//查询终端详情    
				GAS.MonitorMap.superclass.constructor.apply(this, arguments);
				this.allInView  =false ;  //所有的覆盖物显示在当前视野 
				this.showBusLabelText = false;
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
        	    	//console.log("如果默认配置显示车牌号则执行");
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
		   getShoweLabelText:function(){
		      if(!this.LabelTextButton){
		          this.LabelTextButton = new Ext.form.Checkbox({
		            boxLabel : "显示车牌号",
		            tooltip: "显示车牌号",
		            listeners:{
		            	scope :this,
		            	check:function(check,flag){
			               this.showBusLabelTextHander(flag);
			            }
		            }
		          });
		      }
		      return this.LabelTextButton;
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
		      	  	   console.log(labelcfg);
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
						 //this.getCheckOnMap(),"-",
						 this.getShoweLabelText(),"-",
						 this.getShowButtomMap(),"-",
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



