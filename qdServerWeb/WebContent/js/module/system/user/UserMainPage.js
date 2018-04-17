(function() {
	// 用户查询主页面
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.UserQueryMainPage = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.rootNodeId = 1;
			this.userListUrl = GAS.config["globalProxy"] + "/user/list?_dc="
					+ new Date().getTime(); // 查询接口
			this.deleteUrl = GAS.config["globalProxy"] + "/user/delete?_dc="
					+ new Date().getTime(); // 删除接口
			this.comboxTreeUrl = GAS.config["globalProxy"] + "/dept/tree?_dc="
					+ new Date().getTime(); // 部门下拉列表

			this.userManagerUrl = "js/module/system/user/UserManager.js"; // 加载的URL地址
			Ext.apply(this, {
						border : false,
						frame : false,
						layout : "fit",
						title : "用户列表",
						items : this.getMainPanel()
					});
			GAS.UserQueryMainPage.superclass.constructor.apply(this, arguments);
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
		// 邮件菜单
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
							fields : ["id", "deptId", "deptName", "remark",
									"name", "isUsed", "account", "userType",
									"roleId", "contact", "createBy", "roleName"],
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
						header : "用户ID",
						dataIndex : "id",
						flex : 1,
						sortable : true,
						hidden : true
					}, {
						header : "用户ID",
						dataIndex : "deptId",
						flex : 1,
						sortable : true,
						hidden : true
					}, {
						header : "角色ID",
						dataIndex : "roleId",
						flex : 1,
						sortable : true,
						hidden : true
					}, {
						header : "用户名",
						dataIndex : "account",
						sortable : true,
						flex : 1

					}, {
						header : "真实姓名",
						sortable : true,
						dataIndex : "name",
						flex : 1
					}, {
						header : "创建人",
						sortable : true,
						dataIndex : "createBy",
						flex : 1
					}, {
						header : "所属部门",
						sortable : true,
						dataIndex : "deptName",
						flex : 1
					}, {
						header : "电话",
						sortable : true,
						dataIndex : "contact",
						flex : 1
					}, {
						header : "创建时间",
						sortable : true,
						dataIndex : "createTime",
						flex : 1
					}, {
						header : "信息备注",
						sortable : true,
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
				// 上级部门
				this.upDept = new Ext.form.ComboBoxTree({
							triggerAction : "all",
							width : 120,
							title : "所属部门",
							fieldLabel : "所属部门",
							displayField : "text",
							valueField : "id",
							autoLoad : true,
							dataRoot : "result",
							showClearBtn : true,
							anchor : "95%",
							style : "padding-top:2px;",
							dataUrl : this.comboxTreeUrl, // 下拉树形菜单URL地址
							baseParams : {
								id : this.rootNodeId
								// 请求参数
							}
						});
				this.userName = new Ext.form.TextField({
							fieldLabel : "真实姓名",
							name : "username",
							width : 100,
							anchor : "92%"
						});
				this.account = new Ext.form.TextField({
							fieldLabel : "账号",
							name : "account",
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
							items : ["账号:", this.account, "真实姓名:",
									this.userName, "所属部门", this.upDept,
									querybtn, "->"].concat(this.getToorBtn())
						});
			}
			return this.gridtoolBar;
		},
		// 查询
		doQuerySeach : function() {
			var username = this.userName.getValue();
			var account = this.account.getValue();
			var upDeptId = this.upDept.getHiddenValue();

			var store = this.getGridStore();
			Ext.apply(store.baseParams, {
						name : username,
						account : account,
						deptId : upDeptId
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
			var jsUrl = this.userManagerUrl;
			GAS.importlib(jsUrl, function() {
						var window = new GAS.UserManager(config);
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
		// 添加
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
					selectList.push("ids=" + id);
				}
				GAS.AOP.send({
							url : this.deleteUrl, // 加载模块
							params : selectList.join("&"), // 操作方法, // 操作方法
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
