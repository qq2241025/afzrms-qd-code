(function() {
	/**
	 * 超速报警查询 * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.AlarmOverSpeedQuery = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.alarmAreaQueryUrl = GAS.config["globalProxy"]
					+ "/tj/overspeedAlarm?_dc=" + new Date().getTime();
			this.cancleSpeedAlarmUrl = GAS.config["globalProxy"]
					+ "/terminalInstruction/cancleArea?_dc="
					+ new Date().getTime();

			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						title : "超速报警查询",
						items : this.getMainPanel()
					});
			GAS.AlarmOverSpeedQuery.superclass.constructor.apply(this,
					arguments);
		},
		// 主面板
		getMainPanel : function() {
			if (!this.mianPanel) {
				this.mianPanel = new Ext.Panel({
							border : false,
							frame : false,
							layout : "fit",
							items : this.getGridPanel()
						});
			}
			return this.mianPanel;
		},
		// GIRD列表
		getGridPanel : function() {
			if (!this.gridPanel) {
				this.gridPanel = new Ext.grid.GridPanel({
							autoScroll : true,
							layout : "fit",
							frame : false,
							border : false,
							margin : "1px",
							loadMask : {
								msg : "查询中..."
							},
							stripeRows : false,
							enableHdMenu : false,
							columnLines : true,
							viewConfig : {
								forceFit : true
							},
							store : this.getGridStore(),
							columns : this.getGridColumn(),
							sm : this.getCheckboxModel(),
							bbar : this.getGridPageBar(),
							tbar : this.getGridToolBar()
						});
			}
			return this.gridPanel;
		},
		// 分页
		getGridPageBar : function() {
			if (!this.pageBar) {
				this.pageBar = new Ext.PagingToolbar({
							store : this.getGridStore(),
							pageSize : this.pageSize,
							displayInfo : true,
							items : ['-', '每页显示', this.getGridPageSize(), '条'],
							emptyMsg : "没有数据",
							displayMsg : '显示第 {0} 条到 {1} 条记录，一共 {2} 条'
						});
			}
			return this.pageBar;
		},
		// 分页下拉
		getGridPageSize : function() {
			if (!this.combox) {
				this.combox = new Ext.form.ComboBox({
							mode : "local",
							width : 45,
							editable : false,
							value : this.pageSize,
							triggerAction : "all",
							displayField : "text",
							valueField : "value",
							store : new Ext.data.SimpleStore({
										fields : ["value", "text"],
										data : [[20, "20"], [30, "30"],
												[40, "40"]]
									}),
							listeners : {
								scope : this,
								select : function(combo, record, index) {
									this.pageSize = combo.getValue();
									this.getGridPageBar().pageSize = this.pageSize;
									this.onBtnRefesh();
								}
							}
						});
			}
			return this.combox;
		},
		// 刷新
		onBtnRefesh : function() {
			var store = this.getGridStore();
			Ext.apply(store.baseParams, {
						start : 0,
						limit : this.pageSize
					});
			store.load();
		},
		// GRID仓储
		getGridStore : function() {
			if (!this.gridstore) {
				this.gridstore = new Ext.data.JsonStore({
							autoLoad : true,
							fields : ["deptName", "termName", "simcard",
									"speedThreshold", "id", "alarmSubType",
									"distance", "gpsTime", "height", "speed",
									"direction", "deviceStatus", "alarmType",
									"deviceId"],
							root : 'data',
							totalProperty : 'total',
							proxy : new Ext.data.HttpProxy({
										url : this.alarmAreaQueryUrl
									}),
							listeners : {
								scope : this,
								beforeload : function(store, record) {
									var pageParam = this.getQueryParams();
									Ext.apply(store.baseParams, pageParam);
								}
							}
						});
			}
			return this.gridstore;
		},
		// 列
		getGridColumn : function() {
			var cls = [this.getCheckboxModel(), this.getGridLineNum(), {
						header : "deviceId",
						sortable : true,
						hidden : true,
						dataIndex : "deviceId",
						flex : 1
					}, {
						header : "车牌号码",
						sortable : true,
						dataIndex : "termName",
						flex : 1
					}, {
						header : "设备序列号",
						sortable : true,
						dataIndex : "deviceId",
						flex : 1
					}, {
						header : "所属部门",
						sortable : true,
						dataIndex : "deptName",
						flex : 1
					}, {
						header : "SIM号码",
						sortable : true,
						dataIndex : "simcard",
						flex : 1
					}, {
						header : "报警时间",
						sortable : true,
						dataIndex : "gpsTime",
						flex : 1
					}, {
						header : "行驶车速【km/h】",
						sortable : true,
						dataIndex : "speed",
						flex : 1
					}, {
						header : "超速阀值【km/h】",
						sortable : true,
						dataIndex : "speedThreshold",
						flex : 1
					}, {
						header : "行驶方向",
						sortable : true,
						dataIndex : "direction",
						flex : 1,
						renderer : function(val) {
							var fid = GAS.getGPSDirection(val || 0);
							return fid["value"];
						}
					}];
			return cls;
		},
		// 复选框
		getCheckboxModel : function() {
			if (!this.getcheckbox) {
				this.getcheckbox = new Ext.grid.CheckboxSelectionModel({});
			}
			return this.getcheckbox;
		},
		// 行号
		getGridLineNum : function() {
			if (!this.lineNum) {
				this.lineNum = new Ext.grid.RowNumberer({
							header : "序号",
							width : 34
						});
			}
			return this.lineNum;
		},
		// grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
				// 开始时间
				this.alarmStart = new Ext.form.DateField({
							fieldLabel : "开始时间",
							name : "roleName",
							format : "Y-m-d",
							width : 100,
							value : new Date().add(Date.DAY, -3),
							allowBlank : false,
							anchor : "92%"
						});
				// 结束时间
				this.alarmEnd = new Ext.form.DateField({
							fieldLabel : "结束时间",
							name : "roleName",
							format : "Y-m-d",
							width : 100,
							value : new Date(),
							allowBlank : false,
							anchor : "92%"
						});
				// 车辆牌号
				this.vehicleNum = new Ext.form.TextField({
							fieldLabel : "车辆牌号",
							name : "roleName",
							width : 100,
							anchor : "92%"
						});
				var querybtn = new Ext.Button({
							text : '查询',
							scope : this,
							width : 60,
							iconCls : "icon-searchFind",
							style : "margin-left:2px",
							handler : this.doQuerySeach
						});
				this.gridtoolBar = new Ext.Toolbar({
							enableOverflow : true, // 如果tbar溢出自动显示下三角
							items : ["开始时间:", this.alarmStart, "结束时间:",
									this.alarmEnd, "车牌号码:", this.vehicleNum,
									querybtn, "->", {
										text : "取消超速报警",
										scope : this,
										handler : this.cancleSpeedAlarm,
										iconCls : "icon-cancle"
									}]
						});
			}
			return this.gridtoolBar;
		},
		// 取消超速报警
		cancleSpeedAlarm : function() {
			var list = this.getGridPanel().getSelectionModel().getSelections(), selectList = [];
			if (list.length > 0) {
				for (var i = 0, len = list.length; i < len; i++) {
					var record = list[i], id = record.get("deviceId");
					selectList.push(id);
				}
				GAS.AOP.send({
							url : this.cancleSpeedAlarmUrl, // 加载模块
							params : {
								deviceIds : selectList.join(";")
							},
							scope : this,
							callbackFn : function(res) {
								res = Ext.decode(res.responseText);
								if (res["success"] && res["success"] == "true") {
									this.doQuerySeach(); // 刷新
									Ext.Msg.alert('提示信息', '操作成功');
								} else {
									Ext.Msg.alert('提示信息', '操作失败');
								}
							}
						});
			} else {
				Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		// 获取查询参数
		getQueryParams : function() {
			var params = {};
			var startTime = this.alarmStart.getValue().format("Y-m-d");
			var endTime = this.alarmEnd.getValue().format("Y-m-d");
			params["start"] = 0;
			params["limit"] = this.pageSize;
			params["beginTime"] = startTime;
			params["endTime"] = endTime;
			params["name"] = this.vehicleNum.getValue();
			return params;
		},
		// 报警查询
		doQuerySeach : function() {
			var params = this.getQueryParams();
			var store = this.getGridStore();
			Ext.apply(store.baseParams, params);
			store.load();
		}
	});

})();
