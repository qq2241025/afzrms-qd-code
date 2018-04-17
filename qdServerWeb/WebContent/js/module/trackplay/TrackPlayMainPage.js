(function() {
	// 轨迹查询主界面
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.TrackPlayMainPage = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						items : this.getMainPanel()
					});
			GAS.TrackPlayMainPage.superclass.constructor.apply(this, arguments);
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
										margins : "1px 2px 1px 1px",
										items : this.getBusTreePanel(),
										region : "west"
									}, {
										border : true,
										frame : false,
										layout : "fit",
										margins : "1px",
										items : this.getMapPanel(),
										region : "center"
									}]
						});
			}
			return this.mianPanel;
		},
		// 轨迹查询
		doSearchTrackQuery : function() {
			var list = this.getBusTreePanel().getSelectTreeNode();
			if (list && list.length == 1) {
				var deviceObj = list[0];
				var deviceId = deviceObj["id"];
				this.getMapPanel().doTrackQueryWithAjax(deviceId, deviceObj); // 轨迹查询
			} else {
				Ext.Msg.alert('提示信息', '选择一个终端');
			}
		},
		// 右边地图面板
		getMapPanel : function() {
			if (!this.mapPanel) {
				this.mapPanel = new GAS.TrackQueryMap({
							frame : false,
							layout : "fit",
							border : false
						});
				this.mapPanel.on({
							scope : this,
							trackQuery : this.doSearchTrackQuery
						});
			}
			return this.mapPanel;
		},
		// 左侧树形面板
		getBusTreePanel : function() {
			if (!this.BusTree) {
				this.BusTree = new GAS.VehiceTreePanel({
							frame : false,
							layout : "fit",
							border : false
						});
			}
			return this.BusTree;
		}
	});

})();
