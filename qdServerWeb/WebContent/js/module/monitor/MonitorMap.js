(function() {
	/**
	 * 地图基类扩展 * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.MonitorMap = Ext.extend(GAS.BaseMap, {
		constructor : function(config) {
			config = config || {};
			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						items : this.getMainPanel()
					});
			this.listMarkerPoints = []; // 所有的点覆盖物的集合
			this.listMarkerTip = {};
			GAS.MonitorMap.superclass.constructor.apply(this, arguments);
			this.allInView = false; // 所有的覆盖物显示在当前视野
		},
		// 清除所有的车辆覆盖物
		clearAllMap : function() {
			this.hideMapInfoWindow(); // 隐藏气泡
			var list = this.listMarkerPoints;
			if (list && list.length > 0) {
				for (var i = 0; i < list.length; i++) {
					var marker = list[i];
					this.removeOverLayer(marker); // 调用地图删除覆盖物的方法
				}
				this.listMarkerPoints = []; // 清空所有的对象
				this.listMarkerTip = {};
			}
		},
		getMapbarTile : function() {
			return "车辆监控地图工具栏";
		},
		// 根据目标ID居中显示
		moveCarToCenter : function(marker) {
			var cpoint = marker.getPosition();
			this.setView(cpoint);
		},
		// 根据目标ID居中显示
		moveCarToCenterAndTip : function(marker, title, content, targetId) {
			var cpoint = marker.getPosition();
			this.setView(cpoint);
			this.showMapInfo(cpoint, title, content);
			var info = this.getMapInfoWindow();
			info.setTargetId(targetId);
		},
		// 开始定位信息
		startGPSLocation : function(targetID, cfg) {
			var point = this.createPoint(cfg["lng"], cfg["lat"]);
			var marker = this.addMarker({
						lnglat : point,
						iconSize : [17, 17],
						iconAnchor : [9, 9],
						iconUrl : cfg["iconUrl"],
						contentMsg : cfg["msg"]
					});
			this.listMarkerPoints.push(marker); // 多个经纬度
			this.openMarkerInfoWindow(marker, "", cfg["msg"], targetID);// 开始点注册气泡【不然点击的时候没有事件】
			return marker;
		},
		// 更新定位信息
		updateGPSLocation : function(marker, cfg) {
			var targetId = cfg["deviceId"], point = this.createPoint(
					cfg["lng"], cfg["lat"]);
			var imageUrl = cfg["iconUrl"];
			this.updateMarkerIconAndPostion(marker, imageUrl, point); // update
																		// 车辆信息和位置
			this.openMarkerInfoWindow(marker, "", cfg["msg"], targetId);
		},
		// marker打开信息对话气泡信息
		openMarkerInfoWindow : function(markPoint, title, content, targetId) {
			if (!markPoint) {
				console.error("markPoint 不能为空");
				return;
			}
			var markerDom = markPoint.getElement(), me = this;
			var postion = markPoint.getPosition();
			var info = this.getMapInfoWindow();
			// 判断某个终端点击了弹出气泡 更新位置【否则出现多个覆盖物，气泡错乱更新位置bug】
			if (info.getTargetId() == targetId) {
				info.updatePostion(postion); // 更新marker的位置信息
				info.setContent(content); // 更新文本内容
			}
			markerDom.addEventListener("click", function(e) {
						info.setTargetId(targetId);
						me.showMapInfo(postion, title, content);
					}, this);
		},
		// 多个marke居中
		onMutilMarkerInView : function() {
			var list = this.listMarkerPoints;
			// 只有一个点的情况
			if (list.length == 1) {
				var markerlnglat = list[0].getLatLng();
				this.setView(markerlnglat);
			} else {
				var listPoints = [];
				for (var i = 0; i < list.length; i++) {
					var markerlnglat = list[i].getLatLng();
					listPoints.push(markerlnglat);
				}
				var polyline = this.createPolyLine({
							points : listPoints
						});
				// 配置多个覆盖物是否显示当前视野范围内
				if (this.allInView) {
					this.polylineFitInView(polyline);
				}
			}
		},
		getCheckOnMap : function() {
			if (!this.checkBox) {
				this.checkBox = new Ext.form.Checkbox({
							boxLabel : "固定当前视野",
							tooltip : "固定当前视野",
							listeners : {
								scope : this,
								check : function(check, flag) {
									this.allInView = flag;
								}
							}
						});
			}
			return this.checkBox;
		},
		getIsInView : function() {
			return this.allInView;
		},
		// 覆盖工具栏内容
		getMapToolBar : function() {
			if (!this.baritems) {
				this.baritems = [
						"<b style='color:#15428b;'>" + this.getMapbarTile()
								+ "</b>", {
							xtype : 'tbfill'
						}, this.showDrawBtn ? this.getMapBtn() : "", // 显示画多边形,
						this.showClearBtn ? this.getClearBtn() : "", // 显示画多边形
						this.getCheckOnMap(), "-", this.getShowButtomMap(), "-"]
						.concat(this.getCommonMapOper());
			}
			return this.baritems;
		},
		// 覆盖主面板
		getMainPanel : function() {
			if (!this.mainpanel) {
				this.mainpanel = new Ext.Panel({
							border : false,
							frame : false,
							layout : "fit",
							items : this.getMainMap()
						});
			}
			return this.mainpanel;
		}
	});

})();
