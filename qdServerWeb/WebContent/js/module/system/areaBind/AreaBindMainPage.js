(function() {
	// 区域报警查询
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.AreaBindMainPage = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.vehicleTreeUrl = GAS.config["globalProxy"]
					+ "/vehicleGroup/treeTerminal?_dc=" + new Date().getTime(); // 树形菜单
			this.ruleListUrl = GAS.config["globalProxy"]
					+ "/alarmruleArea/list?_dc=" + new Date().getTime(); // 规则
			this.deleteUrl = GAS.config["globalProxy"]
					+ "/alarmruleArea/delete?_dc=" + new Date().getTime(); // 规则
			this.bindAreaAlarm = GAS.config["globalProxy"]
					+ "/terminalInstruction/areaAlarm?_dc="
					+ new Date().getTime(); // 绑定
			this.unbindAreaAlarm = GAS.config["globalProxy"]
					+ "/terminalInstruction/cancleArea?_dc="
					+ new Date().getTime(); // 取消绑定

			this.ruleManagerUrl = [
					"js/module/system/areaBind/AreaBindManager.js",
					"js/module/system/areaBind/RowEditor.js"]; // 加载的URL地址

			Ext.apply(this, {
						border : false,
						frame : true,
						layout : "fit",
						items : this.getMainPanel()
					});
			GAS.AreaBindMainPage.superclass.constructor.apply(this, arguments);
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
										region : "west",
										width : 200,
										margins : "1px",
										layout : "fit",
										items : this.getLeftPanel()
									}, {
										border : true,
										frame : false,
										region : "center",
										layout : "fit",
										margins : "1px",
										title : "规则列表",
										items : this.getGridPanel()
									}]
						});
			}
			return this.mianPanel;
		},
		getLeftPanel : function() {
			if (!this.leftRreePanel) {
				this.leftRreePanel = new GAS.VehiceTreePanel({
							dataRoot : "result",
							title : "车辆列表",
							dataUrl : this.vehicleTreeUrl
						});
			}
			return this.leftRreePanel;
		},
		// 右键菜单
		getGridRightMenu : function() {
			var rightClick = new Ext.menu.Menu({
						items : [{
									iconCls : 'icon-add',
									text : '添加数据',
									scope : this,
									handler : function(e) {
										this.toAdd();
									}
								}, {
									text : '修改数据',
									iconCls : 'icon-update',
									scope : this,
									handler : function(e) {
										this.update();
									}
								}, {
									text : '删除数据',
									scope : this,
									iconCls : 'icon-delete',
									handler : function(e) {
										this.doremove();
									}
								}]
					});
			return rightClick;
		},
		// 双击修改
		onDblClickHandler : function(grid, rowindex, e) {
			var record = this.getGridStore().getAt(rowindex);
			var recordData = record.data;
			this.doCommonHandler({
						action : "update",
						loadRecord : recordData
					});
		},
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
							stripeRows : true,
							enableHdMenu : false,
							columnLines : true,
							viewConfig : {
								forceFit : true
							},
							store : this.getGridStore(),
							columns : this.getGridColumn(),
							bbar : this.getGridPageBar(),
							sm : this.getCheckboxModel(),
							tbar : this.getGridToolBar(),
							listeners : {
								scope : this,
								rowcontextmenu : function(grid, rowindex, e) {
									e.preventDefault();
									this.getGridRightMenu().showAt(e.getXY());
								},
								// 双击修改
								rowdblclick : this.onDblClickHandler
							}
						});
			}
			return this.gridPanel;
		},
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
		// 点击左侧的组织结构暴露的查询的【根据组合结构查询列表】
		onTreeClickhandler : function(parentid) {
			var store = this.getGridStore();
			var params = {
				start : 0,
				limit : this.pageSize
			};
			if (!Ext.isEmpty(parentid)) { // 存在
				params["deptId"] = parentid;
			} else {
				delete store.baseParams["deptId"];
			}
			Ext.apply(store.baseParams, params);
			store.load();
		},
		getGridStore : function() {
			if (!this.gridstore) {
				this.gridstore = new Ext.data.JsonStore({
							autoLoad : true,
							fields : ["id", "name", "remark", "name",
									"createTime", "createBy", "areas"],
							root : 'data',
							totalProperty : 'total',
							proxy : new Ext.data.HttpProxy({
										url : this.ruleListUrl
									}),
							listeners : {
								scope : this,
								beforeload : function(store, record) {
									var pageParam = {
										start : 0,
										limit : this.pageSize
									};
									Ext.apply(store.baseParams, pageParam);
								}
							}
						});
			}
			return this.gridstore;
		},
		getGridColumn : function() {
			var cls = [this.getCheckboxModel(), this.getGridLineNum(), {
						header : "规则ID",
						dataIndex : "id",
						flex : 1,
						sortable : true,
						hidden : true
					}, {
						header : "规则areas",
						dataIndex : "areas",
						flex : 1,
						sortable : true,
						hidden : true
					}, {
						header : "规则名称",
						dataIndex : "name",
						flex : 1,
						sortable : true
					}, {
						header : "创建时间",
						sortable : true,
						dataIndex : "createTime",
						flex : 1
					}, {
						header : "规则描述",
						dataIndex : "remark",
						flex : 1,
						sortable : true
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
				this.ruleName = new Ext.form.TextField({
							fieldLabel : "规则名称",
							name : "username",
							width : 100,
							anchor : "92%"
						});

				var querybtn = new Ext.Button({
							text : '查询',
							scope : this,
							width : 60,
							iconCls : "icon-searchFind",
							handler : this.doQuerySeach
						});
				this.gridtoolBar = new Ext.Toolbar({
							enableOverflow : true, // 如果tbar溢出自动显示下三角
							items : ["规则名称:", this.ruleName, querybtn, "->"]
									.concat(this.getToorBtn())
						});
			}
			return this.gridtoolBar;
		},
		// 查询
		doQuerySeach : function() {
			var ruleName = this.ruleName.getValue();
			var store = this.getGridStore();
			Ext.apply(store.baseParams, {
						name : ruleName
					});
			store.load();
		},
		// 刷新
		refreshGrid : function() {
			var store = this.getGridStore();
			store.load();
		},
		// 添加修改公共方法
		doCommonHandler : function(config) {
			var jsUrl = this.ruleManagerUrl;
			GAS.require(jsUrl, function() {
						var window = new GAS.AreaBindSetting(config);
						window.show();
						// 监听 submit 提交参数 就刷新当前的列表
						window.on({
									scope : this,
									submit : function() {
										this.refreshGrid();
									}
								});
					}, this);
		},
		// 获取终端的ID列表
		getSelectTreeDevideList : function() {
			var list = this.getLeftPanel().getSelectTreeNode();
			var newList = [];
			if (list && list.length > 0) {
				for (var i = 0; i < list.length; i++) {
					var data = list[i], id = data["id"];
					newList.push(id);
				}
			}
			return newList;
		},
		// 下发指令
		bindDevices : function() {
			var list = this.getSelectTreeDevideList();
			if (list.length > 0) {
				var rulelist = this.getGridPanel().getSelectionModel()
						.getSelections();
				if (rulelist.length == 1) {
					var ruleId = rulelist[0].get("id");
					GAS.AOP.send({
								url : this.bindAreaAlarm, // 加载模块
								params : {
									deviceIds : list.join(","),
									alarmruleAreaId : ruleId
								}, // 操作方法, // 操作方法
								scope : this,
								callbackFn : function(res) {
									res = Ext.decode(res.responseText);
									if (res["success"]
											&& res["success"] == "true") {
										Ext.Msg.alert('提示信息', "绑定成功");
									} else {
										Ext.Msg.alert('提示信息', "绑定失败");
									}
								}
							});
				} else {
					Ext.Msg.alert('提示信息', '请选择一个规则');
				}
			} else {
				Ext.Msg.alert('提示信息', '请至少选择一个终端');
			}
		},
		// 取消
		unbindDevices : function() {
			var list = this.getSelectTreeDevideList();
			if (list.length > 0) {
				GAS.AOP.send({
							url : this.unbindAreaAlarm, // 加载模块
							params : {
								deviceIds : list.join(",")
							}, // 操作方法, // 操作方法
							scope : this,
							callbackFn : function(res) {
								res = Ext.decode(res.responseText);
								if (res["success"] && res["success"] == "true") {
									Ext.Msg.alert('提示信息', "解绑成功");
								} else {
									Ext.Msg.alert('提示信息', "解绑失败");
								}
							}
						});
			} else {
				Ext.Msg.alert('提示信息', '请至少选择一个终端');
			}
		},
		// 添加
		toAdd : function() {
			var deviceList = this.getSelectTreeDevideList();
			this.doCommonHandler({
						action : "insert",
						devideList : deviceList
					});
		},
		// 修改数据
		update : function() {
			var list = this.getGridPanel().getSelectionModel().getSelections();
			var deviceList = this.getSelectTreeDevideList();
			if (list.length == 1) {
				var data = list[0].data;
				this.doCommonHandler({
							action : "update",
							loadRecord : data,
							devideList : deviceList
						});
			} else {
				Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		// 删除记录
		doremove : function() {
			var list = this.getGridPanel().getSelectionModel().getSelections(), selectList = [];
			if (list.length > 0) {
				for (var i = 0, len = list.length; i < len; i++) {
					var record = list[i], id = record.get("id");
					selectList.push(id);
				}
				Ext.Msg.show({
							title : '请确认操作？',
							msg : '确认想要删除记录吗?',
							buttons : Ext.Msg.YESNO,
							icon : Ext.MessageBox.QUESTION,
							scope : this,
							fn : function(btn, text) {
								if (btn == 'yes') {
									this.removeAjaxHandler(selectList);
								}
							}
						});
			} else {
				Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		removeAjaxHandler : function(list) {
			GAS.AOP.send({
						url : this.deleteUrl, // 加载模块
						params : {
							ids : list.join(",")
						}, // 操作方法
						scope : this,
						callbackFn : function(res) {
							res = Ext.decode(res.responseText);
							if (res["success"]) {
								Ext.Msg.alert('提示信息', res["result"]
												|| res["msg"] || '成功删除数据');
								this.refreshGrid(); // 刷新
							} else {
								Ext.Msg.alert('提示信息', res["result"]
												|| res["msg"] || '删除数据失败');
							}
						}
					});
		},
		// 工具栏
		getToorBtn : function() {
			return [{
						name : "insert",
						text : "取消规则绑定",
						scope : this,
						iconCls : "icon-send",
						handler : this.unbindDevices
					}, {
						name : "insert",
						text : "终端规则绑定",
						scope : this,
						iconCls : "icon-bind",
						handler : this.bindDevices
					}, {
						name : "insert",
						text : "添加区域规则",
						scope : this,
						iconCls : "icon-add",
						handler : this.toAdd
					}, {
						name : "insert",
						text : "修改规则",
						scope : this,
						iconCls : "icon-update",
						handler : this.update
					}, {
						name : "insert",
						text : "删除规则",
						scope : this,
						iconCls : "icon-remove",
						handler : this.doremove
					}]
		}
	});

})();
