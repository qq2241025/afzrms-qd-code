(function() {
	// 企业管理模块
	GAS.PermissionRoleQuery = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.userListUrl = "data/system/role/roleList.json";
			this.userManagerUrl = "js/module/system/role/RoleManager.js"; // 加载的URL地址
			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						title : "角色列表",
						items : this.getMainPanel()
					});
			GAS.PermissionRoleQuery.superclass.constructor.apply(this,
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
								columnLines : true,
								enableColumnMove : true,
								enableColumnResize : true
							},
							store : this.getGridStore(),
							columns : this.getGridColumn(),
							bbar : this.getGridPageBar(),
							sm : this.getCheckboxModel(),
							tbar : this.getGridToolBar()
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
		getGridStore : function() {
			if (!this.gridstore) {
				this.gridstore = new Ext.data.JsonStore({
							autoLoad : true,
							fields : ["id", "roleName", "roleStatic",
									"creator", "registTime", "desc"],
							root : 'data',
							totalProperty : 'total',
							proxy : new Ext.data.HttpProxy({
										url : this.userListUrl
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
						header : "角色名称",
						dataIndex : "roleName",
						flex : 1
					}, {
						header : "角色权限分配",
						dataIndex : "roleName",
						flex : 1
					}, {
						header : "角色状态",
						dataIndex : "roleStatic",
						flex : 1
					}, {
						header : "创建人",
						dataIndex : "creator",
						flex : 1
					}, {
						header : "信息备注",
						dataIndex : "desc",
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
				this.roleName = new Ext.form.TextField({
							fieldLabel : "角色名称",
							name : "roleName",
							width : 100,
							anchor : "92%"
						});
				// 角色状态
				this.roleStatic = new Ext.form.TextField({
							fieldLabel : "角色状态",
							name : "roleStatic",
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
							items : ["角色名称:", this.roleName, "角色状态",
									this.roleStatic, querybtn, "->"]
									.concat(this.getToorBtn())
						});
			}
			return this.gridtoolBar;
		},
		// 查询
		doQuerySeach : function() {
			var username = this.userName.getValue();
			var store = this.getGridStore();
			Ext.apply(store.baseParams, {
						username : username
					});
			// store.load();
			store.filterBy(function(record) {
						var flag = record.get('username') == username;
						if (Ext.isEmpty(deptname) && Ext.isEmpty(deptnum)) {
							flag = true;
						}
						return flag;
					});
		},
		// 刷新
		refreshGrid : function() {
			var store = this.getGridStore();
			store.load();
		},
		toAdd : function() {
			var jsUrl = this.userManagerUrl;
			GAS.importlib(jsUrl, function() {
						var window = new GAS.UserManager({
									action : "insert"
								});
						window.show();
					}, this);
		},
		// 修改数据
		update : function() {
			var list = this.getGridPanel().getSelectionModel().getSelections();
			if (list.length == 1) {
				var jsUrl = this.userManagerUrl;
				GAS.importlib(jsUrl, function() {
							var window = new GAS.UserManager({
										action : "update"
									});
							window.show();
						}, this);
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
				GAS.AOP.send({
							url : this.deleteUrl, // 加载模块
							params : {
								ids : selectList.join(",")
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
			} else {
				Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		// 工具栏
		getToorBtn : function() {
			return []
		}
	});

})();
