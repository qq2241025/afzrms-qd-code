(function() {
	/**
	 * 车辆监控 * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.BusMonitor = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};

			this.webSocketUrl = GAS.config["webSocketUrl"]; // webSocket 启动
			this.socketUserId = 1023553356;
			this.startWebSocket(); // 构造函数启动websocket,握手
			this.mormalIcon = GAS.config["veHicleIcon"]; // 加载图标
			this.loadingIcon = GAS.config["loadIcon"]; // 加载图标
			this.loadendIcon = GAS.config["loadendIcon"]; // 加载图标
			// 心跳设置URL
			this.timerSetingUrl = GAS.config["globalProxy"]
					+ "/terminalInstruction/timeInter?_dc="
					+ new Date().getTime();
			// 糙苏设置URL
			this.superSpeedUrl = GAS.config["globalProxy"]
					+ "/terminalInstruction/overspeedAlarm?_dc="
					+ new Date().getTime();
			this.signVerhicleUrl = GAS.config["globalProxy"]
					+ "/terminal/findAlarmSet?_dc=" + new Date().getTime();

			this.lookInfoClass = "lookInfo";

			GAS.isBugger = false;
			this.siSunb = false; // 是否已经订阅
			this.treeNodeAlarmCls = "treeAlarm";
			this.alarmIcon = "<img style='cursor:pointer;' title='报警' class='"
					+ this.treeNodeAlarmCls + "' src='" + GAS.iconPath
					+ "minalarm.png" + "' />"; // 树形菜单报警
			this.alarmNodeList = {}; // 存放树形报警DOM
			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						items : this.getMainPanel()
					});

			GAS.BusMonitor.superclass.constructor.apply(this, arguments);
		},
		// 启动startWebSocket
		startWebSocket : function() {
			try {
				this.socketIo = io.connect(this.webSocketUrl);
				this.resetUserId(); // 重新设置UserID
			} catch (e) {
				console.info("%c websocket异常", "color:red;");
			}
			console.log(this.webSocketUrl + "   NodeJs 实时消息推送正式开始");
			var me = this;
			// 连接
			this.socketIo.on('connect', function() {
						console.info("%c connect websocket ", "color:blue");
						var userId = me.getLoginUserId();
						var userInfo = '{"userId":"' + userId + '"}';
						console.log(userInfo);
						me.socketIo.emit('bind', userInfo); // 发送绑定订阅的主题
					});
			// 断开连接
			this.socketIo.on('disconnect', function() {
						console.info("%c disconnect websocket ", "color:red");
					});
			// websocket 收到消息
			this.socketIo.on('message', function(data) {
						me.receiveMsgHandler(data);
					});
		},
		// 实时定位
		timerAjaxLocation : function() {
			var check = this.getBusTreePanel().getSelectTreeNode();
			if (check && check.length > 0) {
				this.getMonitorMap().clearAllMap(); // 清楚当前的地图覆盖物
				this.clearAllNodeStyle(); // 清除所有的样式
				this.checkVehicleList = {}; // 勾选的
				this.locationGpsList = {}; // 定位的车辆终端marker
				this.mitorDevicesList = {}; // 监控的车辆集合Map
				this.alarmDevicesList = {}; // 报警的车辆集合Map
				this.locatoinGPSRecord = {};// 定位的车辆的记录reord;
				for (var index = 0; index < check.length; index++) {
					var node = check[index];
					var deveid = node["id"];
					this.checkVehicleList[deveid] = node;
				}
				this.subTheme(this.checkVehicleList);
			} else {
				Ext.Msg.alert('提示信息', '至少选择一个终端');
			}
		},
		// 实时定位
		cancleTimerAjaxLocation : function() {
			this.unSubTheme(this.checkVehicleList);
			this.getMonitorMap().clearAllMap(); // 清楚当前的地图覆盖物
			this.clearAllNodeStyle(); // 清除所有的样式
			this.checkVehicleList = {}; // 勾选的
			this.locationGpsList = {}; // 定位的车辆终端marker
			this.mitorDevicesList = {}; // 监控的车辆集合Map
			this.alarmDevicesList = {}; // 报警的车辆集合Map
			this.locatoinGPSRecord = {};// 定位的车辆的记录reord;
		},
		// 当点击的时候
		onTreeNodeClick : function(node) {
			var targetId = node.attributes["id"];
			var gpsMarker = this.getLocationByTargetID(targetId); // 找到监控的地图覆盖物
			var record = this.getMintorRecord(targetId) || {};
			var tipConent = record["msg"];
			if (gpsMarker && tipConent) { // 通过targetId 获取监控的车辆的记录返回record
				this.getMonitorMap().moveCarToCenterAndTip(gpsMarker, '',
						tipConent, targetId); // 居中显示和显示TIP
			}
		},
		// 清除所有的样式
		clearAllNodeStyle : function() {
			this.getSouthPanel().getMintorStore().removeAll(); // 清空监控列表
			this.getSouthPanel().getAlarmStore().removeAll(); // 清空报警列表
			var list = this.getCheckVehicleList();
			for (var targetId in list) {
				if (targetId) {
					this.setTreeNodeIcon(targetId, this.mormalIcon); // 设置为正常图标
					this.setTreeTextColor(targetId, "black"); // 回恢复正常颜色
				}
			}
			// 清除报警的dom
			for (var alarm in this.alarmNodeList) {
				var alarmNode = this.alarmNodeList[alarm];
				// 删除报警的dom对象
				if (alarmNode && alarmNode.parentNode) {
					alarmNode.parentNode.removeChild(alarmNode);
				}
			}
			this.alarmNodeList = {}; // 清空
		},
		// 订阅车辆信息【重新订阅信息】
		subTheme : function(listMap) {
			for (var targetId in listMap) {
				var treeNode = listMap[targetId];
				if (targetId) {
					var topicName = "gps_" + targetId;
					this.setTreeNodeIcon(targetId, this.loadingIcon);
					this.setTreeTextColor(targetId, "#FF0080"); // 定位中
					this.subSockectMsg(topicName); // 订阅消息
				}
			}
		},
		// 取消订阅车辆信息
		unSubTheme : function(listMap) {
			for (var targetId in listMap) {
				var treeNode = listMap[targetId];
				if (targetId) {
					var topicName = "gps_" + targetId;
					this.setTreeNodeIcon(targetId, this.mormalIcon); // 设置为正常图标
					this.setTreeTextColor(targetId, "black"); // 回恢复正常颜色
					this.unSubSockectMsg(topicName); // 取消订阅消息
				}
			}
		},
		// 设置节点图标问题
		setTreeNodeIcon : function(targetId, iconImage) {
			this.getBusTreePanel().setNodeIcon(targetId, iconImage);
		},
		// 设置节点字体
		setTreeTextColor : function(targetId, color) {
			this.getBusTreePanel().setNodeTextColor(targetId, color);
		},
		// 获取选择终端信息列表
		getCheckVehicleList : function() {
			return this.checkVehicleList || {};
		},
		// 通过key获取单个终端信息
		getSelectNodeByDeviceId : function(deveid) {
			var map = this.getCheckVehicleList();
			return map[deveid];
		},
		// 设置UserID
		resetUserId : function() {
			var userId = "webSocket_user_" + GAS.GUID(); // "15210523718";
			this.socketUserId = userId;
		},
		// 获取当前用户的ID
		getLoginUserId : function() {
			return this.socketUserId;
		},
		// webSocket 收到信息
		receiveMsgHandler : function(data) {
			var socket = this.getSocketIO();
			var userId = this.getLoginUserId(), uuids = "ed0c3481-ca7b-42b3-9a25-656835a1a8ba1023553356";
			var param = '{"userId":"' + userId + '","uuIds":["' + uuids + '"]}';
			socket.emit('clear', param, function() {
						console.log("clear all cache ");
					});
			var msgData = Ext.decode(data);
			if (msgData && Ext.isArray(msgData)) { // 存在并且是一个数组
				Ext.each(msgData, function(data) {
							var cxt = data["msgContent"];
							this.startAnalysisData(cxt); // 解析分析数据
						}, this);
			}
		},
		// 获取socket
		getSocketIO : function() {
			return this.socketIo;
		},
		// 订阅Socket消息
		subSockectMsg : function(topicName) {
			var socket = this.getSocketIO(), me = this;
			// 发送
			var userId = this.getLoginUserId();
			var msg = '{"userId":"' + userId + '","tags":["' + topicName
					+ '"]}';
			socket.emit('unsub', msg, function(result) {
						socket.emit('sub', msg, function(result) {
									if (result == "success") {
										me.siSunb = true; // 订购成功
									}
								});
					});
		},
		// 取消订阅Socket消息
		unSubSockectMsg : function(topicName) {
			var socket = this.getSocketIO(), me = this;
			// 发送
			var userId = this.getLoginUserId();
			var msg = '{"userId":"' + userId + '","tags":["' + topicName
					+ '"]}';
			socket.emit('unsub', msg, function(result) {
					});
		},
		// 开始分析websocket推送消息的数据
		startAnalysisData : function(recordData) {
			if (recordData.indexOf("LOC") >= 0) {
				console.log("%c 收到定位一条信息:" + recordData + "----" + this.siSunb,
						"color:blue");
			} else {
				console.log("%c 收到报警一条信息:" + recordData + "----" + this.siSunb,
						"color:red");
			}
			// if(!this.siSunb){ //如果没有订阅消息不执行此操作
			// return ;
			// }
			// <1> 监控列表更新数据 || 报警列表更新数据
			// <2> 地图更新数据
			var array = recordData.split(",");
			var targetType = array[0], // 推送类型是定位【LOC】还是报警【ALARM】
			targetId = array[2];
			// 过滤筛选
			var selectnode = this.getSelectNodeByDeviceId(targetId); // 判断订阅的对象
																		// 订阅了就显示在列表中
			if (selectnode) {
				this.setTreeNodeIcon(targetId, this.loadendIcon); // 设置正常图标
				this.setTreeTextColor(targetId, "#2828FF"); // 定位成功后
				if (targetType == "LOC") { // 定位
					this.GPSLocationHandler(array, selectnode);
				} else if (targetType == "ALARM") { // 报警
					this.AlarmLocationHandler(array, selectnode);
				}
			}
		},
		// GPS监控处理
		GPSLocationHandler : function(array, selectnode) {
			// LOC,1,设备ID,X[纬度lng],Y[经度lat],Speed,Direction,Time,LocateType,length,posDes,[]
			// LOC,1,354525045393426,115.665074,34.472677,8.96368,249.89,2014-06-24
			// 15:53:50,1,0,,[]
			var targetId = array[2], lng = array[3], lat = array[4], time = array[7], speed = Number(array[5]
					|| 0).toFixed(2);

			var gpsDirAndIcon = GAS.getGPSDirection(array[6] || 0); // 计算GPS方向和对象的图标
			// GPS方向和图标
			var imgIcon = gpsDirAndIcon["key"], direct = gpsDirAndIcon["value"];
			var imageUrl = GAS.config["gpsimagePath"] + imgIcon + ".png";
			var record = {
				id : targetId, // 终端序列号
				deviceId : targetId, // 终端序列号,
				targetName : selectnode["text"],
				lng : lng, // 经度
				lat : lat, // 纬度
				speed : speed, // 速度
				direct : direct, // 方向
				iconUrl : imageUrl, // 图标
				time : time,
				vehicleNum : selectnode["text"].replace(/<[^>]+>/g, ""),
				simcard : selectnode["simcard"],
				vehicleType : selectnode["vehicleTypeName"],
				deptName : selectnode["deptName"],
				vehicleBrand : selectnode["vehicleBrandName"]
			};
			record["msg"] = this.getTipInfo(record);// 获取tip信息
			this.locatoinGPSRecord[targetId] = record; // 放入map监控集合中
			this.updateGPSMintorGridInfo(targetId, record);// <2>更新监控信息
		},
		// 通过targetId 获取监控的车辆的记录返回record
		getMintorRecord : function(targetId) {
			return this.locatoinGPSRecord[targetId];
		},
		// 获取tip信息
		getTipInfo : function(record) {
			var msg = ["<b>车牌号码:</b> " + record["targetName"],
					"<br><b>所属部门:</b> " + record["deptName"] || "",
					"<br><b>车辆类型:</b> " + record["vehicleType"] || "",
					"<br><b>车辆品牌:</b> " + record["vehicleBrand"] || "",
					"<br><b>行驶时间:</b> " + record["time"],
					"<br><b>行驶车速:</b> " + record["speed"] + " km/h",
					"<br><b>行驶方向:</b> " + record["direct"]];
			return msg.join("");
		},
		// 更新定位和列表信息
		updateGPSMintorGridInfo : function(targetId, record) {
			// 更新定位信息
			var gpsMarker = this.locationGpsList[targetId];
			if (gpsMarker) {// 更新GPS定位信息
				this.getMonitorMap().updateGPSLocation(gpsMarker, record);
			} else {// 定位GPS信息
				var gpsloc = this.getMonitorMap().startGPSLocation(targetId,
						record);// <1>定位终端
				this.locationGpsList[targetId] = gpsloc;
			}
			// 更新列表信息
			var mintorstore = this.getSouthPanel().getMintorStore(); // 监控store
			var oldrecord = this.mitorDevicesList[targetId];
			if (oldrecord) {
				oldrecord.set("time", record["time"]);
				oldrecord.set("direct", record["direct"]);
				oldrecord.set("speed", record["speed"]);
				oldrecord.set("lat", record["lat"]);
				oldrecord.set("lng", record["lng"]);
				mintorstore.commitChanges();
			} else {
				// 数据添加到监控列表
				var newRecord = new Ext.data.Record(record, targetId);
				mintorstore.add(newRecord);
				this.mitorDevicesList[targetId] = newRecord; // map存储
			}
			this.getSouthPanel().refreshMitorGrid(); // 刷新数据显示编号
			this.getSouthPanel().setMitorRecordHightShow(targetId); // 设置高亮
		},
		// 报警处理
		AlarmLocationHandler : function(array, selectnode) {
			// 报警列表
			var alarmStore = this.getSouthPanel().getAlarmStore(); // 监控store
			// ALARM,1,354525045444682,2,,116.300385,39.980826,0.2778,310.05,2015-02-09
			// 17:21:00,1,0,,[]
			// 标识0，记录条数1，设备ID2，报警类型3，报警信息描述(保存超速值或区域ID)4，经度5，纬度6，速度7，方向8，高度9，里程10，时间11，定位类型，位置描述长度，位置描述，扩展
			// 标识0，记录条数1，设备ID2，报警类型3，报警子类型4，超速报警阀值5，区域报警区域编号6，经度7，纬度8，速度9，方向10，高度11，里程12，时间13，定位类型，位置描述长度，位置描述，扩展

			var targetId = array[2], // 目标ID
			alarmType = array[3], // 报警类型
			alarmSubType = array[4], // 报警子类型
			speedThreshold = array[5], // 超速报警阀值
			areaNo = array[6], // 区域报警区域编号
			lng = array[7], // 经度
			lat = array[8], // 纬度
			time = array[13], // GPS time
			speed = Number(array[9] || 0).toFixed(2); // 速度
			//	                
			var gpsDirAndIcon = GAS.getGPSDirection(array[10] || 0); // 计算GPS方向和对象的图标
			// GPS方向和图标
			var imgIcon = gpsDirAndIcon["key"], direct = gpsDirAndIcon["value"];
			var imageUrl = GAS.config["gpsimagePath"] + "Alarm_" + imgIcon
					+ ".png";
			var record = {
				id : targetId, // 终端序列号
				deviceId : targetId, // 终端序列号,
				targetName : selectnode["text"],
				lng : lng, // 经度
				lat : lat, // 纬度
				speed : speed, // 速度
				direct : direct, // 方向
				iconUrl : imageUrl, // 图标
				time : time,
				vehicleNum : selectnode["text"].replace(/<[^>]+>/g, ""),
				simcard : selectnode["simcard"],
				vehicleType : selectnode["vehicleTypeName"] || "",
				deptName : selectnode["deptName"] || "",
				vehicleBrand : selectnode["vehicleBrandName"] || "",
				alarmType : alarmType == "1" ? "超速报警" : alarmType == "2"
						? "区域报警"
						: alarmType == "3" ? "超速、区域报警" : "",
				alarmSubType : alarmSubType == "null"
						? ""
						: alarmSubType == "0" ? "进区域" : alarmSubType == "1"
								? "出区域"
								: alarmSubType == "2" ? "区域限速" : "",
				speedThreshold : speedThreshold == "null" ? "" : speedThreshold,
				areaNo : areaNo == "null" ? "" : areaNo
			};
			record["msg"] = this.getTipInfo(record);// 获取tip信息
			this.locatoinGPSRecord[targetId] = record; // 放入map监控集合中
			// <1>警的时候同步更行监控列表
			this.updateGPSMintorGridInfo(targetId, record);

			// <2>开启右下角报警提示Tip信息框
			// this.StartAlarmAOP(record);
			// <3>左侧树形菜单图标报警样式
			this.startTreeAlarm(record);

			var oldrecord = this.alarmDevicesList[targetId];
			if (oldrecord) {
				// 更新store 中的信息
				oldrecord.set("time", record["time"]);
				oldrecord.set("direct", record["direct"]);
				oldrecord.set("speed", record["speed"]);
				oldrecord.set("lat", record["lat"]);
				oldrecord.set("lng", record["lng"]);
				oldrecord.set("alarmType", record["alarmType"]);
				oldrecord.set("alarmSubType", record["alarmSubType"]);
				oldrecord.set("speedThreshold", record["speedThreshold"]);
				oldrecord.set("areaNo", record["areaNo"]);
				alarmStore.commitChanges();;
			} else {
				var newRecord = new Ext.data.Record(record, targetId);
				this.alarmDevicesList[targetId] = newRecord; // map存储
				alarmStore.add(newRecord);
			}
			this.getSouthPanel().refreshAlarmGrid(); // 刷新报警数据显示编号
		},
		// 左侧树形菜单节点添加报警样式
		startTreeAlarm : function(record) {
			var targetId = record["deviceId"], me = this;
			var treeNode = this.getBusTreePanel().getNodeByTargetId(targetId); // 节点对象
			var nodeAnchor = treeNode.getUI().getTextEl(); // 文本dom
			var AlarmNode = this.alarmNodeList[targetId]; // 获取报警的dom
			if (!AlarmNode) {
				var alarmNode = Ext.DomHelper.insertAfter(nodeAnchor,
						this.alarmIcon); // 添加报警图标dom
				this.alarmNodeList[targetId] = alarmNode;
				var dom = Ext.fly(alarmNode);
				if (dom) {
					dom.on({
								scope : this,
								click : function() {
									this.lookAlarmHandler(record);
								}
							});
				}
			}
		},
		// 启用报警TIP
		StartAlarmAOP : function(record) {
			// 设置是否弹出TIP
			var isSetting = GAS.Cookie.getCookie("AlarmSetting"); // 默认开启
			if (GAS.isEmpty(isSetting)) {
				isSetting = true;
			}
			if (isSetting != true) {
				return;
			}
			if (this.alarWin) {
				this.alarWin.hideTips();
			} else {
				var tipCxt = "<b>车牌号</b>："
						+ record["targetName"]
						+ "于"
						+ record["time"]
						+ "报警<br/><b>报警类型</b>:"
						+ record["alarmType"]
						+ "<span style='color:blue;cursor:pointer;margin-left:4px;' class='"
						+ this.lookInfoClass + "'>点击查看<span>";
				this.alarWin = new GAS.AlarmWindowBox({
					html : '<div style="margin-left:4px;">' + tipCxt + '<div>',
					width : 200,
					iconCls : "icon-alarm",
					height : 110,
					title : "报警信息",
					listeners : {
						scope : this,
						afterrender : function(panel) {
							var container = panel.container, me = this;
							container.on({
								scope : this,
								mousedown : function(e, t) {
									var flg = Ext.fly(t);
									if (flg && flg.hasClass(this.lookInfoClass)) {
										me.lookAlarmHandler(record); // 报警设置
									}
								}
							});
						}
					}
				});
			}
			this.alarWin.show();
		},
		// //点击报警图标和 点击详情
		lookAlarmHandler : function(record) {
			var deviceId = record["deviceId"];
			var flag = this.getMainSouthPanel().collapsed; // 是否已经折叠
			if (flag) {
				this.getMainSouthPanel().expand(); // 展开
			}
			this.getSouthPanel().activeAlarmPanelRecord(deviceId); // 激活报警面板并显示报警的记录
		},
		// 获取所有定位的终端信息
		getAllLocationList : function() {
			return this.locationGpsList;
		},
		// 根据目标ID
		getLocationByTargetID : function(targetId) {
			return this.locationGpsList[targetId];
		},
		// 左侧树形菜单
		getBusTreePanel : function() {
			if (!this.BusTree) {
				this.BusTree = new GAS.VehiceTreePanel({
							frame : false,
							layout : "fit",
							border : false
						});
				this.BusTree.getMainTreePanel().on({
					scope : this,
					contextmenu : function(node, e) {
						var type = node.attributes["type"], deviceId = node.attributes["id"];
						if (node && node.isLeaf()) {
							var menu = this.getMenuItem(deviceId);
							menu.showAt(e.getXY());
							e.stopEvent();
						}
					},
					click : this.onTreeNodeClick
				});
			}
			return this.BusTree;
		}, // 中间地图
		getMonitorMap : function() {
			if (!this.mintorMap) {
				this.mintorMap = new GAS.MonitorMap({
							frame : false,
							layout : "fit",
							border : false
						});
				window["MonitorMap"] = this;
			}
			return this.mintorMap;
		},
		testLocation : function(targetId, cfg) {
			this.mintorMap.startGPSLocation(targetId, cfg);
		},
		// 下面监控信息Ext.Panel
		getSouthPanel : function() {
			if (!this.southPanel) {
				this.southPanel = new GAS.MonitorVehicle({
							frame : false,
							layout : "fit",
							border : false
						});
				this.southPanel.on({
							scope : this,
							mintorClick : function(targetId, record) {
								var gpsMarker = this
										.getLocationByTargetID(targetId);
								if (gpsMarker) {
									var tipConent = record.data["msg"];
									this.getMonitorMap().moveCarToCenterAndTip(
											gpsMarker, '', tipConent, targetId); // 居中显示和显示TIP
								}
							}
						});
			}
			return this.southPanel;
		},
		// 主面板
		getMainPanel : function() {
			if (!this.mianPanel) {
				this.mianPanel = new Ext.Panel({
							border : false,
							frame : false,
							layout : "border",
							items : [{
										border : true,
										frame : false,
										layout : "fit",
										width : 200,
										margins : "1px",
										items : this.getBusTreePanel(),
										region : "west"
									}, {
										border : false,
										frame : false,
										layout : "fit",
										items : this.getCenterPanel(),
										margins : "1px",
										region : "center"
									}]
						});
			}
			return this.mianPanel;
		},
		// 中间面板
		getCenterPanel : function() {
			if (!this.centerPanel) {
				this.centerPanel = new Ext.Panel({
							border : false,
							frame : false,
							layout : "border",
							items : [{
										border : true,
										frame : false,
										layout : "fit",
										items : this.getMonitorMap(),
										region : "center"
									}, this.getMainSouthPanel()]
						});
			}
			return this.centerPanel;
		},
		getMainSouthPanel : function() {
			if (!this.mainSouthPanel) {
				this.mainSouthPanel = new Ext.Panel({
							border : true,
							frame : false,
							layout : "fit",
							height : 180,
							collapsible : true,
							// collapsed:true,
							split : true,
							// collapseMode:"mini",
							margins : "1px",
							title : "监控车辆",
							items : this.getSouthPanel(),
							region : "south"
						});
			}
			return this.mainSouthPanel;
		},
		// 菜单
		getMenuItem : function(deviceId) {
			var items = [{
						text : '实时定位',
						deviceId : deviceId,
						scope : this,
						handler : this.timerAjaxLocation
					}, {
						text : '取消定位',
						deviceId : deviceId,
						scope : this,
						handler : this.cancleTimerAjaxLocation
					}, {
						text : '心跳设置',
						deviceId : deviceId,
						scope : this,
						handler : this.sendTimerCmd
					}, {
						text : '超速设置',
						deviceId : deviceId,
						scope : this,
						handler : this.superSpeedSetting
					}];
			var rightMenu = new Ext.menu.Menu({
						items : items
					});
			return rightMenu;
		},
		// 显示查询进度条
		showLoadMask : function() {
			this.Trackmask = Ext.Msg.show({
						title : '请稍等',
						msg : '正在下发指令，请稍后.....',
						progressText : '下发中...',
						width : 300,
						wait : true
					});
		},
		// 隐藏查询进度条
		hideLoadMask : function() {
			Ext.Msg.hide();
		},
		getSignVehicle : function(deviceId, handler) {
			var callback = function(res) {
				res = Ext.decode(res.responseText);
				handler.call(this, res);
			};
			Ext.Ajax.request({
						url : this.signVerhicleUrl,
						method : "post",
						params : {
							deviceId : deviceId
						},
						success : callback,
						failure : callback
					});
		},
		// js 多线程执行
		TestGPS : function() {
			var me = this;
			var thread = function(IEMI, timer) {
				setInterval(function() {
							var lng = 120.376807811 + Math.random() * 0.003;
							var lat = 36.26544111 + Math.random() * 0.002;
							var speed = 8.96368 + Math.random(10) * 10;
							var dir = 8.96368 + Math.random(10) * 360;
							var time = new Date().getTime();
							var msg = "LOC,1," + IEMI + "," + lng + "," + lat
									+ "," + speed + "," + dir + "," + time
									+ ":50,1,0,,[]";
							me.startAnalysisData(msg);
						}, timer);
			}
			var devidelist = [{
						IEMI : 354525045423835,
						timer : 3000
					}, {
						IEMI : 354525045371513,
						timer : 5000
					}, {
						IEMI : 354525045393426,
						timer : 6000
					}];
			// 创建了三个线程
			for (var index = 0; index < devidelist.length; index++) {
				var devide = devidelist[index];
				thread(devide["IEMI"], devide["timer"]);
			}
		},
		// 超速设置
		superSpeedSetting : function(btn) {
			var me = this;
			var callback = function(res, req) {
				res = Ext.decode(res.responseText);
				me.hideLoadMask();
				if (res && res["success"] && res["success"] == "true") {
					Ext.Msg.alert('信息提示', '指令下发成功');
				} else {
					Ext.Msg.alert('信息提示', '指令下发失败');
				}
			};
			var showWin = function(value) {
				me.getMsgBox({
							title : "超速设置【km/h】",
							scope : this,
							value : value || "",
							maxValue : 300,
							callback : function(val) {
								var dataPar = {
									max : Math.floor(val),
									deviceIds : deviceList.join(";")
								};
								me.showLoadMask();
								GAS.AOP.send({
											url : me.superSpeedUrl,
											params : dataPar,
											method : "POST",
											timeout : 2 * 60 * 1000,
											scope : this,
											callbackFn : callback
										});
							}
						});
			};

			var superspeedAjax = function(deviceList) {
				// 只有一辆车的时候查询车辆速度
				if (deviceList.length == 1) {
					var deviceIds = deviceList[0];
					me.getSignVehicle(deviceIds, function(data) {
								var speed = data["overspeedThreshold"];
								showWin(speed);
							});
				} else {
					showWin();
				}
			};
			var check = this.getBusTreePanel().getSelectTreeNode();
			if (check && check.length > 0) {
				var deviceList = [];
				for (var index = 0; index < check.length; index++) {
					var node = check[index];
					var deveid = node["id"];
					deviceList.push(deveid);
				}
				superspeedAjax(deviceList);
			} else {
				Ext.Msg.alert('提示信息', '至少选择一个终端');
			}
		},
		// 下发心跳时间
		sendTimerCmd : function(btn) {
			var me = this;
			var callback = function(res, req) {
				res = Ext.decode(res.responseText);
				me.hideLoadMask();
				if (res && res["success"] && res["success"] == "true") {
					Ext.Msg.alert('信息提示', '指令下发成功');
				} else {
					Ext.Msg.alert('信息提示', '指令下发失败');
				}
			};
			var setTimerAjax = function(deviceList) {
				me.getMsgBox({
							title : "设置心跳时间【秒】",
							scope : this,
							callback : function(val) {
								var dataPar = {
									interval : val,
									deviceIds : deviceList.join(";")
								};
								me.showLoadMask();
								GAS.AOP.send({
											url : me.timerSetingUrl,
											params : dataPar,
											method : "POST",
											timeout : 2 * 60 * 1000,
											scope : this,
											callbackFn : callback
										});
							}
						});
			};
			var check = this.getBusTreePanel().getSelectTreeNode();
			if (check && check.length > 0) {
				var deviceList = [];
				for (var index = 0; index < check.length; index++) {
					var node = check[index];
					var deveid = node["id"];
					deviceList.push(deveid);
				}
				setTimerAjax(deviceList);
			} else {
				Ext.Msg.alert('提示信息', '至少选择一个终端');
			}
		},
		// 设置下发的弹出框
		getMsgBox : function(cfg) {
			var textFiled = new Ext.form.NumberField({
						fieldLabel : cfg["fieldLabel"] || "",
						allowBlank : false,
						height : 22,
						value : cfg["value"],
						invalidText : "请输入数字",
						emptyText : "不能为空",
						maxValue : cfg["maxValue"] || 100,
						minValue : cfg["minValue"] || 0,
						anchor : "95%"
					});
			var btns = [{
						text : "确定",
						width : 55,
						handler : function() {
							var val = textFiled.getValue();
							cfg["callback"].call(cfg["scope"] || this, val);
							win.close();
						}
					}, {
						text : "取消",
						width : 55,
						handler : function() {
							win.close();
						}
					}], win = null;
			win = new Ext.Window({
						autoCreate : true,
						title : cfg["title"] || "",
						resizable : false,
						constrain : true,
						constrainHeader : true,
						minimizable : false,
						maximizable : false,
						modal : true,
						border : false,
						layout : "fit",
						bodyBorder : false,
						items : textFiled,
						buttonAlign : "center",
						width : 230,
						height : 92,
						plain : true,
						closable : true,
						fbar : new Ext.Toolbar({
									items : btns,
									enableOverflow : false
								})
					});
			// 关闭
			var canclehandler = function() {
				win.close();
			};
			var sureHandler = function() {

			};
			win.show();
		}
	});

})();
