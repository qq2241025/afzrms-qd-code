 (function(){
	/**车辆监控
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.BusMonitor = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.webSocketUrl = GAS.config["webSocketUrl"]; //webSocket 启动
	      	    this.socketUserId = 1023553356;
	      	    this.startWebSocket(); //构造函数启动websocket,握手
	      	    this.mormalIcon  = GAS.config["veHicleIcon"] ; //加载图标
	      	    this.loadingIcon = GAS.config["loadIcon"] ; //加载图标
	      	    this.loadendIcon = GAS.config["loadendIcon"] ; //加载图标
	      	    //心跳设置URL
	      	    this.timerSetingUrl  = GAS.config["globalProxy"] + "/terminalInstruction/timeInter?_dc="+new Date().getTime(); 
	      	    //糙苏设置URL
	      	    this.superSpeedUrl   = GAS.config["globalProxy"] + "/terminalInstruction/overspeedAlarm?_dc="+new Date().getTime(); 
	      	    this.signVerhicleUrl = GAS.config["globalProxy"] + "/terminal/findAlarmSet?_dc="+new Date().getTime(); 	 
				
	      	    this.lookInfoClass = "lookInfo";
	      	    this.locationGpsList = {};  //定位的车辆终端marker
	      	    this.locatoinGPSRecord = {};//定位的车辆的记录reord;
	      	    
	      	    this.listeneTimer = 2 * 1000; //监听时间
	      	    this.listeneTimerCount= 180; // 监听时间差值PS: 时间比较差值单位秒
	      	    
	      	    GAS.isBugger = false;
	      	    this.siSunb = false; //是否已经订阅
	      	    this.treeNodeAlarmCls = "treeAlarm";  
	      	    this.alarmIcon = "<img style='cursor:pointer;' title='报警' class='"+this.treeNodeAlarmCls+"' src='"+ GAS.iconPath+"minalarm.png"+"' />"; //树形菜单报警
	      	    this.alarmNodeList = {}; //存放树形报警DOM
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
				GAS.BusMonitor.superclass.constructor.apply(this, arguments);
		   },
		   //启动startWebSocket
		   startWebSocket:function(){
		   	    this.TestGPS();
		   },
		   //实时定位
		   timerAjaxLocation:function(){
		      var check =  this.getBusTreePanel().getSelectTreeNode();
		      if(check  && check.length > 0){
		      	    this.getMonitorMap().clearAllMap(); //清楚当前的地图覆盖物
		      	    this.clearAllNodeStyle(); //清除所有的样式
		      	    this.checkVehicleList = {}; //勾选的
		      	    this.locationGpsList = {};  //定位的车辆终端marker
		      	    this.mitorDevicesList = {}; //监控的车辆集合Map
	      	        this.alarmDevicesList = {}; //报警的车辆集合Map
	      	        this.locatoinGPSRecord = {};//定位的车辆的记录reord;
		      	    for (var index = 0; index < check.length; index++) {
		      	    	var node = check[index];
		      	    	var deveid =node["id"];
		      	    	this.checkVehicleList[deveid] = node;
		      	    }
		      	    this.subTheme(this.checkVehicleList);
		      }else{
		      	  　Ext.Msg.alert('提示信息', '至少选择一个终端');
		      }
		   },
		   //实时定位
		   cancleTimerAjaxLocation:function(){
			   this.unSubTheme(this.checkVehicleList);
			   this.getMonitorMap().clearAllMap(); //清楚当前的地图覆盖物
			   this.clearAllNodeStyle(); //清除所有的样式
			   this.checkVehicleList = {}; //勾选的
			   this.locationGpsList = {};  //定位的车辆终端marker
			   this.mitorDevicesList = {}; //监控的车辆集合Map
			   this.alarmDevicesList = {}; //报警的车辆集合Map
			   this.locatoinGPSRecord = {};//定位的车辆的记录reord;
		   },
		   //当点击的时候
		   onTreeNodeClick:function(node){
		       var targetId = node.attributes["id"];
		       var gpsMarker = this.getLocationByTargetID(targetId); //找到监控的地图覆盖物
		       var record = this.getMintorRecord(targetId)|| {}; 
		       var tipConent = record["msg"]; 
	           if(gpsMarker && tipConent){ //通过targetId 获取监控的车辆的记录返回record
	              this.getMonitorMap().moveCarToCenterAndTip(gpsMarker,'',tipConent,targetId); //居中显示和显示TIP
	           }
		   },
		   //清除所有的样式
		   clearAllNodeStyle:function(){
		   	    var  list = this.getCheckVehicleList();
		   	    for (var targetId in list) {
		   	    	 if(targetId){
		   	    	 	this.setTreeNodeIcon(targetId,this.mormalIcon);  //设置为正常图标
		   	    	 	this.setTreeTextColor(targetId,"black"); //回恢复正常颜色
		   	    	 }
		   	    }
		   	    //清除报警的dom
		   	    for (var alarm  in this.alarmNodeList ) {
		       	   var alarmNode = this.alarmNodeList[alarm];
		       	   //删除报警的dom对象
		       	   if(alarmNode && alarmNode.parentNode){
		       	   	  alarmNode.parentNode.removeChild(alarmNode);  
		       	   }
		       }
		       this.alarmNodeList = {}; //清空
		   },
		   //订阅车辆信息【重新订阅信息】
		   subTheme:function(listMap){
		   	   	  for (var targetId in listMap) {
		   	   	  	   var treeNode = listMap[targetId];
		   	   	  	   if(targetId){
		   	   	  	        var topicName = "gps_"+targetId;
		   	   	  	        this.setTreeNodeIcon(targetId,this.loadingIcon);
		   	   	  	        this.setTreeTextColor(targetId,"#FF0080"); //定位中
		   	   	      	    this.subSockectMsg(topicName); //订阅消息
		   	   	  	   }
		   	   	  }
		   },
		   //取消订阅车辆信息
		   unSubTheme:function(listMap){
			   for (var targetId in listMap) {
				   var treeNode = listMap[targetId];
				   if(targetId){
					   var topicName = "gps_"+targetId;
					   this.setTreeNodeIcon(targetId,this.mormalIcon);  //设置为正常图标
		   	    	   this.setTreeTextColor(targetId,"black"); //回恢复正常颜色
					   this.unSubSockectMsg(topicName); //取消订阅消息
				   }
			   }
		   },
		   //设置节点图标问题
		   setTreeNodeIcon:function(targetId,iconImage){
		      this.getBusTreePanel().setNodeIcon(targetId,iconImage);
		   },
		   //设置节点字体
		   setTreeTextColor:function(targetId,color){
		      this.getBusTreePanel().setNodeTextColor(targetId,color);
		   },
		   //获取选择终端信息列表
		   getCheckVehicleList:function(){
		      return  this.checkVehicleList || {};
		   },
		   //通过key获取单个终端信息
		   getSelectNodeByDeviceId:function(deveid){
		       var map = this.getCheckVehicleList();
		       return map[deveid];
		   },
		   //设置UserID 
		   resetUserId : function(){
		      var userId =  "webSocket_user_"+GAS.GUID(); //"15210523718";
		      this.socketUserId = userId;
		   },
		   //获取当前用户的ID
		   getLoginUserId : function(){
		      return this.socketUserId ;
		   },
		   //webSocket 收到信息
		   receiveMsgHandler:function(data){
		   	   var socket = this.getSocketIO();
		   	   var userId = this.getLoginUserId(),uuids = "ed0c3481-ca7b-42b3-9a25-656835a1a8ba1023553356";
		       var param = '{"userId":"'+userId+'","uuIds":["'+uuids+'"]}';
          	   socket.emit('clear',param,function(){
          	      GAS.debuglog("clear all cache");
          	   });
		       var msgData = Ext.decode(data);
		       if(msgData && Ext.isArray(msgData)){ //存在并且是一个数组
		             Ext.each(msgData,function(data){
		                  var cxt = data["msgContent"];
		                  this.startAnalysisData(cxt); //解析分析数据
		             },this);
		       }
		   },
		   //获取socket
		   getSocketIO:function(){
		      return this.socketIo;
		   },
		   //订阅Socket消息
		   subSockectMsg:function(topicName){
		   	    var socket = this.getSocketIO(),me= this;
		   	    //发送
		   	    var userId = this.getLoginUserId(); 
			    var msg = '{"userId":"'+userId+'","tags":["'+topicName+'"]}';
			    if(socket){
				    socket.emit('unsub',msg,function(result){
				         socket.emit('sub',msg,function(result){
					         if(result=="success"){
					         	me.startlistVehStatic(); //启动
					         	me.siSunb= true; //订购成功
					         }
				        }); 
			    	});
			    }
		   },
		   //取消订阅Socket消息
		   unSubSockectMsg:function(topicName){
		   	    var socket = this.getSocketIO(),me= this;
		   	    if(!socket){
		   	    	return ;
		   	    }
		   	    //发送
		   	    var userId = this.getLoginUserId(); 
			    var msg = '{"userId":"'+userId+'","tags":["'+topicName+'"]}';
			    socket.emit('unsub',msg,function(result){
			       GAS.debuglog("取消订阅");
			    });
		   },
		   //开始分析websocket推送消息的数据
		   startAnalysisData :function(recordData){
		   	    //<1> 监控列表更新数据 || 报警列表更新数据
		   	    // <2> 地图更新数据
		   	     var array = recordData.split(",");
		   	     if(!array){
		   	       return ;
		   	     }
                 var targetType = array[0],targetId= array[2]; //推送类型是定位【LOC】还是报警【ALARM】
                 var selectnode=this.getSelectNodeByDeviceId(targetId); //判断订阅的对象 订阅了就显示在列表中
                 if(selectnode){
		                this.setTreeNodeIcon(targetId,this.loadendIcon); //设置正常图标
		                this.setTreeTextColor(targetId,"#2828FF"); //定位成功后
		                
		                if(targetType == "LOC"){ //定位
		                      this.GPSLocationHandler(array,selectnode);
		                }else if(targetType == "ALARM"){ //报警
		                      this.AlarmLocationHandler(array,selectnode);
		                }
                }
		   },
		   /************************2015-11-27 hzg modify***************************/
		   //格式化时间
		   formatDateTime : function(time){
		       return new Date(time.replace(/-/g,"/"));
		   },
		   //检查是否跳点情况的问题
		   checkPushHops:function(targetId,nowTime){
		        var cacheVehicle =  this.locatoinGPSRecord[targetId];
                if(cacheVehicle){
                    var cacheTime= cacheVehicle["time"];
                    var catime = this.formatDateTime(cacheTime),nodate = this.formatDateTime(nowTime);
                    if(nodate.getTime() <  catime.getTime()){
                        return true;
                    }
                }
                return false;
		   },
		   //通过targetId删除地图覆盖物
		   removeMarkerByTargetId : function(targetId){
		        var gpsMarker = this.locationGpsList[targetId];
                if(gpsMarker){//删除地图覆盖物对象
                	this.getMonitorMap().removeOverLayer(gpsMarker);
                	this.getMonitorMap().hideMapInfoWindow();
                	this.getMonitorMap().removeGPSMarkerLabel(targetId); //剔除显现的时候删除车牌号
                }
		   },
		   stopListVehStatic : function(){
		       if(this.staticTimer){
		   	       clearInterval(this.staticTimer); //取消定时器timer
		   	   }
		   },
		   isGPSRecordEmpty:function(){
		      var flag = true;
		      for (var targetId  in this.locatoinGPSRecord) {
		      	   flag = false;
		      	   break;
		      }
		      return flag;
		   },
		   /************************2015-11-27 hzg modify***************************/
		   //GPS监控处理
		   GPSLocationHandler:function(array,selectnode){
		   	   //LOC,1,设备ID,X[纬度lng],Y[经度lat],Speed,Direction,Time,LocateType,length,posDes,[]
		   	   //LOC,1,354525045393426,115.665074,34.472677,8.96368,249.89,2014-06-24 15:53:50,1,0,,[]
		   	   var targetId= array[2], lng = array[3],lat = array[4],time = array[7],speed = Number(array[5] || 0).toFixed(2);
	           var direction = parseFloat(array[6]);
	           var gpsDirAndIcon = GAS.getGPSDirection(direction); //计算GPS方向和对象的图标
               var direct = gpsDirAndIcon["value"]; //GPS方向和图标
               var targetName = selectnode["name"].replace(/<[^>]+>/g,"");
               var typeColor  = "#"+selectnode["color"] || "#000000";
               var dirverName = selectnode["driverName"] || "暂无数据";
               var driverPhone = selectnode["driverPhone"] || "暂无数据";
               var imageUrl = GAS.config["gpsimagePath"]+ "normal_gps.png";
               if(lng == 0 || lat == 0 ){
                  return ;
               }
//             GAS.debuglog(time+"--收到【"+targetId+"】推送的一条定位信息:速度【"+speed+"】,位置【"+lng+","+lat+"】");
               var record = {
               	    id : targetId, //终端序列号
                    deviceId : targetId, //终端序列号,
                    targetName : targetName,
                    lng: lng, //经度
                    lat: lat, //纬度
                    speed : speed, //速度
                    direct : direct, //方向
                    iconUrl: imageUrl, //图标
                    time : time,
                    montiorTime: new Date(),
                    status :    "LOC",
                    driverName: dirverName,
                    driverPhone : driverPhone,
                    vehicleNum  : targetName,
                    simcard     : selectnode["simcard"],
                    typeColor　　: typeColor,
                    vehicleType : this.processValue(selectnode["vehicleTypeName"]),
                    deptName    : selectnode["deptName"],
                    vehicleBrand: this.processValue(selectnode["vehicleBrandName"])
                };
                record["msg"] = this.getTipInfo(record);//获取tip信息
                record["montiorTime"] = new Date();
                //根据时间检查是否跳点
                var isHops = this.checkPushHops(targetId,time); 
                if(!isHops){ //不存在跳点
                	this.locatoinGPSRecord[targetId] = record; //放入map监控集合中
	                this.updateMintorGPSCache(targetId,record); //更新定位缓存信息
	                //<3>移除由报警转为正常定位的车辆
		            this.removeAlramRecord(targetId);
                }
		   },
		   //判断在报警列表中有没有报警数据【报警---->定位】
		   removeAlramRecord:function(targetId){
	            //移除树上的报警图标
	            var alarmNode = this.alarmNodeList[targetId];
	            if(alarmNode && alarmNode.parentNode){
		       	   	alarmNode.parentNode.removeChild(alarmNode);  
		       	   	delete this.alarmNodeList[targetId];
	            }
		   },
		    //通过targetId 获取监控的车辆的记录返回record
		   getMintorRecord :function(targetId){
		       return  this.locatoinGPSRecord[targetId];
		   },
		   //获取tip信息
		   getTipInfo:function(record){
		        var msg = [
	                "<b>车牌号码:</b> " + record["targetName"],
					"<br><b>所属部门:</b> "+ record["deptName"] || "",
					"<br><b>车辆类型:</b> "+ record["vehicleType"] || "暂无数据",
					"<br><b>车辆品牌:</b> "+ record["vehicleBrand"] || "",
					"<br><b>行驶时间:</b> "+ record["time"] ,
					"<br><b>行驶车速:</b> "+ record["speed"]+" km/h" ,
					"<br><b>行驶方向:</b> "+ record["direct"]
                ];
                return msg.join("");
		   },
		   //地图旋转的角度
		   getMapRotation:function(){
		      return this.getMonitorMap().getMapRotation();
		   },
		   //更新报警和定位缓存
		   updateMintorGPSCache:function(targetId,record){
	   	    	//更新定位信息
                var gpsMarker = this.locationGpsList[targetId];
                if(gpsMarker){//更新GPS定位信息
                    this.getMonitorMap().updateGPSLocation(gpsMarker,record);
                }else{//定位GPS信息
                    var gpsloc = this.getMonitorMap().startGPSLocation(targetId,record);//<1>定位终端
                    this.locationGpsList[targetId] = gpsloc ;
                }
		   },
		   processValue:function(val){
		      if(val =="" || val =="null" || val==undefined){
		      	 return "暂无数据";
		      }
		      return val;
		   },
		    //报警处理【解析报警数据】
		   AlarmLocationHandler:function(array,selectnode){
		        	//报警列表
                    //标识0，记录条数1，设备ID2，报警类型3，报警子类型4，超速报警阀值5，区域报警区域编号6，经度7，纬度8，速度9，方向10，时间11，定位类型，位置描述长度，位置描述，扩展
		   	        //ALARM,1,354525045371554,1,null,50.0,null,120.38009681,36.26689545,21.14984,165.31,2015-11-20 11:40:19,1,0,,[]
                	var targetId= array[2], //目标ID
                	alarmType = array[3],  //报警类型
                	alarmSubType = array[4],  //报警子类型
                	speedThreshold = array[5],  //超速报警阀值
                	areaNo = array[6],  //区域报警区域编号
		   	        lng = array[7], //经度
		   	        lat = array[8], //纬度
	                time = array[11], //GPS time
	                speed = Number(array[9] || 0).toFixed(2); //速度
		            var direction = parseFloat(array[10]); 
	                var gpsDirAndIcon = GAS.getGPSDirection(direction); //计算GPS方向和对象的图标
		            var direct = gpsDirAndIcon["value"]; //GPS方向和图标
	                var imageUrl = GAS.config["gpsimagePath"]+"normal_alarm.png";
	                var targetName = selectnode["name"].replace(/<[^>]+>/g,"");
	                var typeColor  = "#"+selectnode["color"] || "#000000";
	                var dirverName = selectnode["driverName"] || "暂无数据";
               		var driverPhone = selectnode["driverPhone"] || "暂无数据";
               		if(lng == 0 || lat == 0 ){
	                  return ;
	                }
//	                GAS.debuglog("warn",time+"--收到【"+targetId+"】推送的一条报警信息:速度【"+speed+"】,区域编号【"+areaNo+"】,位置【"+lng+","+lat+"】");
	                
	                var record = {
	                	id : targetId, //终端序列号
	                    deviceId : targetId, //终端序列号,
	                    targetName : targetName,
	                    lng: lng, //经度
	                    lat: lat, //纬度
	                    speed : speed, //速度
	                    direct : direct, //方向
	                    iconUrl: imageUrl, //图标
	                    time : time,
	                    labelText:targetName,
	                    title :　targetName,
	                    montiorTime: new Date(),
	                    status :    "ALARM",
	                    typeColor : typeColor,
	                    vehicleNum  : targetName,
	                    driverName: dirverName,
                        driverPhone : driverPhone,
	                    simcard     : selectnode["simcard"],
	                    vehicleType : this.processValue(selectnode["vehicleTypeName"]),
	                    deptName    : this.processValue(selectnode["deptName"]),
	                    vehicleBrand: this.processValue(selectnode["vehicleBrandName"]),
	                    alarmType   : alarmType=="1"?"超速报警":alarmType=="2"?"区域报警":alarmType=="3"?"超速、区域报警":"",
	                    alarmSubType: alarmSubType=="null"?"":alarmSubType=="0"?"进区域":alarmSubType=="1"?"出区域":alarmSubType=="2"?"区域限速":"",
	                    speedThreshold: speedThreshold=="null"?"":speedThreshold,
	                    areaNo: areaNo!="null" ? areaNo : " "
	                };
	                record["msg"] = this.getTipInfo(record);//获取tip信息
	                var isHops = this.checkPushHops(targetId,time);
	                if(!isHops){ //不存在跳点数据
	                	this.locatoinGPSRecord[targetId] = record; //放入map监控集合中
		                //<1>警的时候同步缓存信息
		                this.updateMintorGPSCache(targetId,record); 
		                //<2>更新监控【定位和报警】列表信息
	                	//<3>左侧树形菜单图标报警样式
	                	this.startTreeAlarm(record);
	                }
		   },
		   //左侧树形菜单节点添加报警样式
		   parseDom:function (arg) { 
			　　 var objE = document.createElement("div"); 
			　　 objE.innerHTML = arg; 
			　　 return objE.childNodes[0]; 
		   },
		   startTreeAlarm:function(record){
		       var targetId = record["deviceId"],me= this;
		       var treeNode = this.getBusTreePanel().getNodeByTargetId(targetId); //节点对象
		       var nodeAnchor = treeNode.getUI().getTextEl(); //文本dom
		       var AlarmNode = this.alarmNodeList[targetId];  //获取报警的dom
		       if(!AlarmNode &&  nodeAnchor){
		       	  var alarmNode = this.parseDom(this.alarmIcon);
		       	  nodeAnchor.parentNode.appendChild(alarmNode);
		       	  this.alarmNodeList[targetId] = alarmNode;
		       	  var dom = Ext.fly(alarmNode);
		       	  if(dom){
		       	      dom.on({
		       	      	scope : this,
		       	      	click : function(){
		       	      	   this.lookAlarmHandler(record);
		       	      	}
		       	      });
		       	  }
		       }
		   },
		   ////点击报警图标和 点击详情 
		   lookAlarmHandler:function(record){
		   	    var deviceId = record["deviceId"];
		   	    this.getSouthPanel().activeAlarmPanelRecord(deviceId); //激活报警面板并显示报警的记录
		   },
		    //获取所有定位的终端信息
		   getAllLocationList:function(){
		      return this.locationGpsList;
		   },
		   //根据目标ID
		   getLocationByTargetID:function(targetId){
		       return this.locationGpsList[targetId];
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
							 var type = node.attributes["type"],deviceId = node.attributes["id"];
							 if(node && node.isLeaf()){
						         var menu = this.getMenuItem(deviceId,node);
								 menu.showAt(e.getXY());
								 e.stopEvent();
							 }
						},
						click : this.onTreeNodeClick
			       });
		       }
		       return this.BusTree;
		   }, //中间地图
		   getMonitorMap:function(){
		   	    if(!this.mintorMap){
	                this.mintorMap =new GAS.MonitorMap({
	                	 frame  : false,
	                	 layout : "fit",
		           	     border : false
					});
					window["LBS"] = this;
		        }
		        return this.mintorMap;
		   },
		   testLocation:function(targetId,cfg){
		      this.mintorMap.startGPSLocation(targetId,cfg);
		   },
		   //下面监控信息Ext.Panel
		   getSouthPanel:function(){},
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
								border : true,
								frame  : false,
								layout : "border",
								width  : 215,
								margins: "1px",
								split : true,
								collapseMode:"mini",
								items  : [{
								     layout : "fit",
								     region : "center",
								     frame  : false,
								     border : false,
								     items : this.getBusTreePanel()
								}],
								region : "west"
							},{
								border : false,
								frame  : false,
								layout : "fit",
								items  : this.getCenterPanel(),
								margins: "1px",
								region : "center"
						}]
			        });
			    }
			   return  this.mianPanel;
		   },
		   clearMintoTask:function(){
		   	    if(this.minTorTask){
		   	    	clearInterval(this.minTorTask);
		   	    }
		   },
		   startMintoTask:function(){
		   	    var me = this;
		   	    this.clearMintoTask();
		   },
		    //中间面板
		   getCenterPanel:function(){
		        if(!this.centerPanel){
	                this.centerPanel =new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
								border : true,
								frame  : false,
								layout : "fit",
								items  : this.getMonitorMap(),
								region : "center"
							}]
			        });
		        }
		        return this.centerPanel;
		   },
		   //菜单
		   getMenuItem:function(deviceId,node){
		   	    var isLeaf = false;
		   	    if(node && node.isLeaf()){
		   	        isLeaf = true;
		   	    }
		        var items = [{
						text : '实时定位',
						deviceId : deviceId,
						scope   : this,
						handler : this.timerAjaxLocation
				   },{
						text : '取消定位',
						deviceId : deviceId,
						scope   : this,
						handler : this.cancleTimerAjaxLocation
				   }];
				var rightMenu = new Ext.menu.Menu({
					items : items
				});
		       return  rightMenu;
		   },
		   getSignVehicle:function(deviceId,handler){
		   	   var callback = function(res){
		   	      res  = Ext.decode(res.responseText);
		   	      handler.call(this,res);
		   	   };
		       Ext.Ajax.request({
		           url  : this.signVerhicleUrl,
		           method : "post",
		           params : {
		             deviceId : deviceId
		           },
		           success:callback,
		           failure:callback
		       });
		   },
		   //js 多线程执行
		   TestGPS :function(){
		   	    var me = this;
		        var thread = function(IEMI,timer){
	           	   var threadTimer = setInterval(function(){
				   	       var lng = 120.37687811 + Math.random()*0.78;
					   	   var lat = 36.2654111  + Math.random()*0.37;
					   	   var speed = 8.96368 + Math.random(10)*10;
					   	   var dir = 8.96368 + Math.random(10)*360;
					   	   var time = new Date().format("Y-m-d H:i:s");
					   	   var msg = "";
					   	   if(Math.random() *10 >5){
					   	   	   var areaNo  =  parseInt(Math.random() * 30)+1;
					   	   	   msg = "ALARM,1,"+IEMI+",2,222,12,"+areaNo+","+lng+","+lat+","+speed+","+dir+","+time+",1,0,,[]";
					   	   }else{
					   	       msg = "LOC,1,"+IEMI+","+lng+","+lat+","+speed+","+dir+","+time+",1,0,,[]";
					   	   }
			           	   me.startAnalysisData(msg);
		       	    },timer); 
		       	    return threadTimer;
		       }
		       var timerList = [1,2,3,4,6,7,8,9,10,11];
		       var devidelist = GAS.devidelist;
		       var  listMap ={};
		       for (var i = 0; i < devidelist.length; i++) {
		       	    var targetid = devidelist[i];
		       	    var timeDate= (timerList[Math.round(Math.random()* 10)] || 2) *1000 ;
		       	    var timer =  thread(targetid,timeDate); //模拟线程
		       	    listMap[targetid] = timer;
		       }
		       window["ThreadGroups"] = listMap;
		       return "start"
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
		   }
	});
	
	
})();
