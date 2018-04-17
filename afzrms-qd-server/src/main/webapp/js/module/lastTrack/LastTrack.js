(function(){
	/**
	 * 最后位置表
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.LastTrack = Ext.extend(GAS.BaseMap,{
		  constructor : function(config) {
	      	    config = config || {};
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "查看车辆最后轨迹",
					items  : this.getMainPanel()
	      	    });
	      	    this.listMarkerPoints = {} ; //所有的点覆盖物的集合
	      	    this.ListMarkerLabel = {} ; //所有的点覆盖物的集合
				this.trackIcon =  GAS.iconPath+ "bus.gif";
	      	    this.selectLastTrackUrl = GAS.config["globalProxy"] + "/locrecord/getLastTrackData?_dc="+new Date().getTime();//查询终端详情 
	      	    this.AllLastTrackUrl = GAS.config["globalProxy"] + "/locrecord/getAllLastTrackData?_dc="+new Date().getTime();//查询终端详情 
				GAS.LastTrack.superclass.constructor.apply(this, arguments);
		   },
		   //清除所有的车辆覆盖物
		   clearAllMap:function(){
		   	  this.getMapPanel().hideMapInfoWindow();  //隐藏气泡
		      var maplist = this.listMarkerPoints;
		      if(maplist){
		          for (var i in maplist) {
		          	  var marker = maplist[i];
		          	  if(marker){
		          	  	 this.getMapPanel().removeOverLayer(marker); //调用地图删除覆盖物的方法
		          	  }
		          }
		          this.listMarkerPoints = {};  //清空所有的对象
		      }
		      //清楚label
		      var mapLabellist = this.ListMarkerLabel;
		      if(mapLabellist){
		          for (var i in mapLabellist) {
		          	  var label = mapLabellist[i];
		          	  if(label){
		          	  	 this.getMapPanel().removeOverLayer(label); //调用地图删除覆盖物的方法
		          	  }
		          }
		          this.ListMarkerLabel = {};  //清空所有的对象
		      }
		   },
		   getMapbarTile : function(){
			   return  "车辆最后位置";
		   },
		    //根据目标ID居中显示
		   moveCarToCenterAndTip:function(targetId){
		       var marker = this.listMarkerPoints[targetId];
		       if(marker){
		       	    var cfg = marker["config"];
		            var postion = marker.getPosition();
		        	this.getMapPanel().showMapInfo(postion,"",cfg["msg"]);//
		        	var info = this.getMapPanel().getMapInfoWindow();
		   	    	info.setTargetId(targetId);
		       }
		   },
		  //显示查询进度条
		   showLoadMask : function() {
				 this.Trackmask = Ext.Msg.show({
	             	title: '请稍等',   
					msg: '正在查询数据，请稍后.....',
					progressText: '查询中...',
					width:300,
					wait:true
				});
		   },
		   //隐藏查询进度条
		   hideLoadMask : function() {
			   Ext.Msg.hide();
		   },
		    //多个marke居中
		   setFitView:function(){
		      var list = this.listMarkerPoints;
		      //只有一个点的情况
		       if(list){
		      	  var listPoints = [];
		      	  for (var i in list) {
		      	  	 var marker = list[i];
		      	  	 if(marker){
		      	  	 	var markerlnglat = marker.getPosition();
		      	  	 	listPoints.push(markerlnglat);
		      	  	 }
		      	  }
		      	  if(listPoints.length >0){
		      	  	  var polyline = this.getMapPanel().createPolyLine({points:listPoints});
		      	 	  this.getMapPanel().polylineFitInView(polyline);
		      	  }
		      }
		   },
		   //覆盖工具栏内容
		   getMapToolBar:function(){
				 if(!this.baritems){
				 	  this.baritems = ["<b style='color:#15428b;'>"+this.getMapbarTile()+"</b>",{
							xtype: 'tbfill'
						 }].concat(this.getCommonMapOper());
				 }
				 return  this.baritems;
			},
			//点击叶子节点定位当前的车辆
			onTreeNodeClick:function(node){
			  if(node && node.isLeaf()){
			      var record = node.attributes;
			      var deviceid= record["id"];
			      this.moveCarToCenterAndTip(deviceid);
			  }
			},
			//左侧树形菜单
		   getBusTreePanel:function(){
		       if(!this.BusTree){
		           this.BusTree= new  GAS.VehiceTreePanel({
		           	    frame  : false,
		           	    layout : "fit",
		           	    border : false,
		           	    showRootChecked: true
		           });
		           this.BusTree.getMainTreePanel().on({
	           	        scope: this,
						contextmenu:function(node,e){
							 if(node){
						         var menu = this.getMenuItem(node);
								 menu.showAt(e.getXY());
								 e.stopEvent();
							 }
						},
						click : this.onTreeNodeClick
			       });
		       }
		       return this.BusTree;
		   },
		   getSelectTreeNodeRecord : function(){
		   	    var map = {};
		   	    var check =  this.getBusTreePanel().getSelectTreeNode();
			    if(check  && check.length > 0){
			     	   for (var i = 0; i < check.length; i++) {
			      	    	var node   = check[i];
			      	    	var deveid =node["id"];
			      	    	map[deveid] = node;
			      	   }
			    }
		   	   return map;
		   },
		   //查看选中的终端设备的位置
		   querySelectDevicePostion:function(){
		   	     var deviceList = [];
		   	     var check =  this.getBusTreePanel().getSelectTreeNode();
			     if(check  && check.length > 0){
			      	    var deviceList = [];
			      	    for (var i = 0; i < check.length; i++) {
			      	    	var node   = check[i];
			      	    	var deveid =node["id"];
			      	    	deviceList.push(deveid);
			      	    }
			      }else{
		      	  　		Ext.Msg.alert('提示信息', '至少选择一个终端');
		      	        return ;
		          }
		          this.showLoadMask();
		          GAS.AOP.send({
			       url   : this.selectLastTrackUrl,
			       method : "post",
			       params : {
		             deviceIds : deviceList.join("@")
		           },
			       scope : this,
			       callbackFn: function(res){
			       	   this.hideLoadMask();
		       	       var res = Ext.decode(res.responseText);
		               if(res && res["result"] && res["result"].length >0){
		                    var list = res["result"];
		                    this.showVehiclePostion(list);
		               }else{
		                    Ext.Msg.alert('提示信息', '没有找到对应的数据');
		               }
			       }
			     });
		   },
		   //添加车辆备注的信息处理
		   addVehicleRepair:function(menu){
		   	    var node = menu.node;
		   	    var check =  this.getBusTreePanel().getSelectTreeNode();
			    if(check  && check.length ==1){
			    	if(node && node.isLeaf()){
			   	    	var record = node.attributes;
			   	        var addMod = "js/module/lastTrack/VehicleAddWindow.js";
					    GAS.importlib(addMod,function(){
					    	    var win = new GAS.VehicleAddWindow({
					    	    	vehicleRecord : record
					    	    });
					    	    win.show();
				        },this);
			   	    }
			    }else{
			        Ext.Msg.alert('提示信息', '请选择一个终端');
			    }
		   	    
		   },
		   //查询所有的位置
		   queryAllDevicePostion:function(){
		   	 	  this.showLoadMask();
		          GAS.AOP.send({
				       url   : this.AllLastTrackUrl,
				       method : "post",
				       scope : this,
				       callbackFn: function(res){
				       	   this.hideLoadMask();
			       	       var res = Ext.decode(res.responseText);
			               if(res && res["result"] && res["result"].length >0){
			                    var list = res["result"];
		                        this.showVehiclePostion(list);
			               }else{
			                    Ext.Msg.alert('提示信息', '没有找到对应的数据');
			               }
				       }
			     });
		   },
		   //显示车辆的位置信息
		   showVehiclePostion:function(list){
		        if(list && list.length > 0 ){
		               this.clearAllMap();
		               var treeList=  this.getSelectTreeNodeRecord();
		               for (var i = 0; i < list.length; i++) {
		               	       var record = list[i];
		               	       var deviceId = record["deviceId"];
			            	   var lng = record["x"],lat = record["y"];
			            	   var termName  =  record["name"];//车牌号
			            	   var simcard   =  record["simcard"];
			            	   var deptName   =  record["deptName"] || "";
			            	   var vehicleType   =  record["vehicleTypeName"] || "暂无数据";
			            	   var vehicleBrand   =  record["vehicleBrandName"] || "暂无数据";
			            	   var xdistance = Number(record["distance"] || "0.0");
			            	   var point = this.getMapPanel().createPoint(parseFloat(lng),parseFloat(lat));
			            	   var treeNode = treeList[deviceId] ||{};
			            	   var typeColor = "#"+treeNode["color"] || "#000000";
			            	   var typeName = treeNode["vehicleTypeName"] || "";
			            	   
			            	   var contentText = [
								'<b>车牌号码: </b> ' + termName,
								'<br><b>所属部门: </b> ' + deptName,
								'<br><b>车辆类型: </b> ' + vehicleType,
								'<br><b>行驶车速: </b> ' + record["speed"]+"km/h",
								'<br><b>行驶时间: </b> ' + record["gpstime"]];
								var cfg = {
									 lnglat      : point,
	            	 				 iconSize    : [24,24],
	            	 				 iconAnchor  : [11,11],
					            	 iconUrl     : this.trackIcon,
					            	 clickEnable : true,
					            	 title : typeName+"-"+termName,
					            	 width       : 200,
					            	 msg  : contentText.join("")
								 }
								 var marker = this.getMapPanel().addMarker(cfg);
								 marker["config"] = cfg;
								 
								 var labelcfg = {
								     point:point,
								     fontColor : typeColor,
								     title:termName,
								     labelText: termName,
								     offsetTop: 12
								 }
								 var markerLabel = this.getMapPanel().addMarkerLabel(labelcfg);
								 this.ListMarkerLabel[deviceId] = markerLabel;
								 this.listMarkerPoints[deviceId] = marker ;
		               }
		               this.setFitView(); //居中显示
		        }
		   },
		    //菜单
		   getMenuItem:function(node){
		   	     var isLeaf = false;
		   	     if(node && node.isLeaf()){
		   	        isLeaf = true;
		   	     }
		   	     var items = [{
						text : '<b>查看位置</b>',
						scope   : this,
						handler : this.querySelectDevicePostion
				   },{
						text : '<b>备注处理</b>',
						scope   : this,
						node     : node,
						disabled : !isLeaf,
						handler : this.addVehicleRepair
				   },{
						text : '查看所有',
						scope   : this,
						handler : this.queryAllDevicePostion
				   }];
				   var rightMenu = new Ext.menu.Menu({
					items : items
				  });
		          return  rightMenu;
		   },
		   getMapPanel :function(){
		       if(!this.mapPanel){
			       this.mapPanel = new GAS.BaseMap({
			           title : "青岛地图",
			           hideTools: true
			       });
			    }
			    return  this.mapPanel;
		   },
		   //覆盖主面板
		   getMainPanel: function() {
				if (!this.mainpanel) {
					 this.mainpanel = new Ext.Panel({
						border : false,
						frame  : false,
						layout : "border",
						items  : [{ 
						             border : true,
									 frame  : false,
									 layout : "fit",
									 region : "west",
									 width  : 215,
									 margins: "1px",
									 split : true,
									 collapseMode:"mini",
								     items : this.getBusTreePanel()
							},{
								border : true,
								frame  : false,
								layout : "fit",
								items  : this.getMapPanel(),
								margins: "1px",
								region : "center"
						}]
					});
				}
			    return this.mainpanel;
		   }
	});
	
})();



