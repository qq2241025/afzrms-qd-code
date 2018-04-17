(function() {
	// 区域设置主界面
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.AreaAlarmMainPage = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.areaListUrl = GAS.config["globalProxy"]
					+ "/alarmArea/list?_dc=" + new Date().getTime();
			this.deleteUrl = GAS.config["globalProxy"]
					+ "/alarmArea/delete?_dc=" + new Date().getTime();
			this.areaManagerUrl = "js/module/system/areaSetting/AreaManager.js"; // 加载的URL地址
			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						title : "区域列表查询",
						items : this.getMainPanel()
					});
			GAS.AreaAlarmMainPage.superclass.constructor.apply(this, arguments);
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
							viewConfig : {
								forceFit : true,
								columnLines : true
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
										data : [[10, "10"], [20, "20"],
												[30, "30"]]
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
		getGridStore : function() {
			if (!this.gridstore) {
				this.gridstore = new Ext.data.JsonStore({
							autoLoad : true,
							fields : ["id", "isUsed", "createTime", "xys",
									"description", "name", "createBy", "remark"],
							root : 'data',
							totalProperty : 'total',
							proxy : new Ext.data.HttpProxy({
										url : this.areaListUrl
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
						header : "id",
						hidden : true,
						dataIndex : "id",
						menuDisabled : false,
						flex : 1
					}, {
						header : "xys",
						hidden : true,
						orderable : true,
						dataIndex : "xys",
						flex : 1
					}, {
						header : "区域名称",
						dataIndex : "name",
						orderable : true,
						flex : 1
					}, {
						header : "创建时间",
						dataIndex : "createTime",
						orderable : true,
						flex : 1
					}, {
						header : "描述信息",
						dataIndex : "description",
						flex : 1
					}, {
						header : "信息备注",
						dataIndex : "remark",
						flex : 1
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
				this.areaName = new Ext.form.TextField({
							fieldLabel : "区域名称",
							name : "areaName",
							width : 100,
							anchor : "92%"
						});
				this.gridtoolBar = new Ext.Toolbar({
							enableOverflow : true, // 如果tbar溢出自动显示下三角
							items : ["区域名称:", this.areaName, {
										text : '查询',
										scope : this,
										width : 60,
										iconCls : "icon-searchFind",
										handler : this.doQuerySeach
									}, "->"].concat(this.getToorBtn())
						});
			}
			return this.gridtoolBar;
		},
		// 查询
		doQuerySeach : function() {
			var areaName = this.areaName.getValue();
			var store = this.getGridStore();
			Ext.apply(store.baseParams, {
						name : areaName
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
			var jsUrl = this.areaManagerUrl;
			GAS.importlib(jsUrl, function() {
						var window = new GAS.AreaManager(config);
						window.show();
						/**
						 * 监听 submit 提交参数 就刷新当前的列表
						 */
						window.on({
									scope : this,
									submit : function() {
										this.refreshGrid();
									}
								});
					}, this);
		},
		toAdd : function() {
			this.doCommonHandler({
						action : "insert"
					});
		},
		// 修改数据
		update : function() {
			var list = this.getGridPanel().getSelectionModel().getSelections();
			if (list.length == 1) {
				var data = list[0].data;
				this.doCommonHandler({
							action : "update",
							loadRecord : data
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
		getBindVehicleid : function() {
			return this.vehicleId;
		},
		// 暴露设置绑定的函数
		doBindHandler : function(data) {
			var text = data["text"], vehicleId = data["id"];
			this.vehicleId = vehicleId;
			this.bindBtn.setDisabled(false);
			var tmsg = "当前车辆:" + text;
			this.labelText.setText(tmsg);
		},
		getToorBtn : function() {
			return [{
						name : "insert",
						text : "添加",
						scope : this,
						iconCls : "icon-add",
						handler : this.toAdd
					}, {
						name : "insert",
						text : "修改",
						scope : this,
						iconCls : "icon-update",
						handler : this.update
					}, {
						name : "insert",
						text : "删除",
						scope : this,
						iconCls : "icon-remove",
						handler : this.doremove
					}]
		}
	});

})();
